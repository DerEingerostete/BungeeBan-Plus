/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.listener;

import de.dereingerostete.bungeeban.BungeeBan;
import de.dereingerostete.bungeeban.BungeeBanPlugin;
import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.entity.Cache;
import de.dereingerostete.bungeeban.entity.ban.Ban;
import de.dereingerostete.bungeeban.entity.player.BanPlayer;
import de.dereingerostete.bungeeban.entity.player.PunishablePlayer;
import de.dereingerostete.bungeeban.util.HashUtils;
import de.dereingerostete.bungeeban.util.MessageUtils;
import de.dereingerostete.bungeeban.util.SchedulerUtils;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ConnectListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onLogin(@NotNull LoginEvent event) {
        if (event.isCancelled()) return;
        PendingConnection connection = event.getConnection();
        UUID uuid = connection.getUniqueId();

        event.registerIntent(BungeeBanPlugin.getInstance());
        SchedulerUtils.runAsync(() -> {
            BanPlayer banPlayer;
            try {
                banPlayer = BungeeBan.getPlayer(uuid, true);
                if (banPlayer == null) {
                    Logging.debug("No player with the uuid '" + uuid + "' found. Creating one");
                    banPlayer = BungeeBan.createPlayer(uuid);
                }
            } catch (Throwable throwable) {
                Logging.warning("Failed to load player on login", throwable);
                event.setCancelReason(new TextComponent("§cSystem could not verify you!\nPlease try again later"));
                event.setCancelled(true);
                event.completeIntent(BungeeBanPlugin.getInstance());
                return;
            }

            Cache cache = BungeeBanPlugin.getCache();
            cache.cachePlayer(banPlayer);
            Logging.debug("Cached player: " + banPlayer.getUniqueId());

            if (banPlayer.isBanned()) {
                Ban ban = Objects.requireNonNull(banPlayer.getBan());
                if (ban.hasEnded()) {
                    banPlayer.refresh();
                    event.completeIntent(BungeeBanPlugin.getInstance());
                    return;
                }

                event.setCancelReason(MessageUtils.createBanComponent(ban));
                event.setCancelled(true);
            }
            event.completeIntent(BungeeBanPlugin.getInstance());
        });
    }

    @EventHandler
    public void onPreLogin(@NotNull PreLoginEvent event) {
        PendingConnection connection = event.getConnection();
        SocketAddress socketAddress = connection.getSocketAddress();

        if (!(socketAddress instanceof InetSocketAddress)) return;
        InetSocketAddress address = (InetSocketAddress) socketAddress;
        InetAddress inetAddress = address.getAddress();

        event.registerIntent(BungeeBanPlugin.getInstance());
        SchedulerUtils.runAsync(() -> {
            String hash = HashUtils.hashAddress(inetAddress);
            List<Ban> bans = BungeeBan.getBansByIP(hash);

            Ban ban = bans.stream()
                    .filter(b -> b.isIPBan() && !b.hasEnded())
                    .findAny().orElse(null);

            if (ban != null) {
                String[] formattedBan = MessageUtils.formatBan(ban);
                event.setCancelled(true);
                event.setCancelReason(new TextComponent(ArrayUtils.toString(formattedBan)));
            }
            event.completeIntent(BungeeBanPlugin.getInstance());
        });
    }

    /**
     * Used for PlayerActivity
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnected(@NotNull ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        ServerInfo info = event.getServer().getInfo();
        String name = info.getName();

        if (name == null) {
            Logging.warning("Server name is null: " + info);
            return;
        }

        PunishablePlayer punishablePlayer = getPlayer(uuid);
        if (punishablePlayer != null) updateActivity(uuid, name, punishablePlayer);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisconnect(@NotNull PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        PunishablePlayer punishablePlayer = getPlayer(uuid);
        if (punishablePlayer == null) return;

        String lastServer = punishablePlayer.getLastServer();
        if (lastServer == null) throw new IllegalStateException("LastServer cannot be null on quit");
        updateActivity(uuid, punishablePlayer.getLastServer(), punishablePlayer);

        SchedulerUtils.runAsync(() -> {
            try {
                Cache cache = BungeeBanPlugin.getCache();
                cache.removePlayer(uuid);
                cache.removeActivity(uuid);
            } catch (SQLException exception) {
                Logging.warning("Failed to update activity of player '" + uuid + "' on leave", exception);
            }
        });
    }

    protected void updateActivity(@NotNull UUID uuid, @NotNull String serverName,
                                  @NotNull PunishablePlayer punishablePlayer) {
        SchedulerUtils.runAsync(() -> {
            try {
                punishablePlayer.setLastServer(serverName);
            } catch (SQLException exception) {
                Logging.warning("Failed to lastServer activity of player '"
                        + uuid + "' from '" + punishablePlayer.getLastServer(), exception);
            }

            Cache cache = BungeeBanPlugin.getCache();
            Cache.Activity activity = cache.getActivity(uuid);
            if (activity == null) { //Check if player wasn't connected before
                cache.cacheActivity(uuid, serverName);
                return;
            }

            try {
                long current = System.currentTimeMillis();
                punishablePlayer.addActivity(activity.getServerName(), current - activity.getJoinTime());

                activity.setServerName(serverName);
                activity.setJoinTime(current);
            } catch (SQLException exception) {
                Logging.warning("Failed to update activity of player '" + uuid + "': " + activity, exception);
            }
        });
    }

    @Nullable
    protected PunishablePlayer getPlayer(@NotNull UUID uuid) {
        BanPlayer banPlayer = BungeeBan.getCachedPlayer(uuid);
        if (banPlayer == null) {
            Logging.warning("Failed to get ban player (" + uuid + ")");
            return null;
        }

        if (!(banPlayer instanceof PunishablePlayer)) {
            Logging.warning("Failed to save player activity: Player ("
                    + uuid + ") is no instance of " + PunishablePlayer.class.getName());
            return null;
        } else return (PunishablePlayer) banPlayer;
    }

}