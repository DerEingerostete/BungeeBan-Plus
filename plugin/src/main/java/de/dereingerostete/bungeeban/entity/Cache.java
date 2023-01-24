/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity;

import com.google.common.collect.Maps;
import de.dereingerostete.bungeeban.BungeeBanPlugin;
import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.database.BanDatabase;
import de.dereingerostete.bungeeban.entity.player.BanPlayer;
import de.dereingerostete.bungeeban.entity.player.PunishablePlayer;
import de.dereingerostete.bungeeban.util.SchedulerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Cache implements AutoCloseable {
    protected final ScheduledTask clearTask;
    protected final Plugin plugin;

    protected final Map<UUID, CachedPlayer> addressMap;
    protected final Map<UUID, Activity> activityMap;
    protected final long cacheDuration;

    public Cache(Plugin plugin, long cacheDuration) {
        this.cacheDuration = cacheDuration;
        this.plugin = plugin;

        TaskScheduler scheduler = ProxyServer.getInstance().getScheduler();
        clearTask = scheduler.schedule(plugin, this::refreshCache, 30L, 30L, TimeUnit.SECONDS);

        addressMap = Maps.newHashMap();
        activityMap = Maps.newHashMap();
    }

    /**
     * Checks all maps for old entries and removes them
     */
    public void refreshCache() {
        long time = System.currentTimeMillis();
        Map.copyOf(addressMap).entrySet()
                .stream()
                .filter(entry -> !entry.getValue().getPlayer().isOnline())
                .filter(entry -> entry.getValue().getLeaveTime() != -1)
                .filter(entry -> entry.getValue().getLeaveTime() + cacheDuration <= time)
                .forEach(entry -> {
                    clearChatLog(entry.getKey());
                    addressMap.remove(entry.getKey());
                });
    }

    private void clearChatLog(UUID uuid) {
        SchedulerUtils.runAsync(() -> {
            try {
                BanDatabase database = BungeeBanPlugin.getDatabase();
                database.clearMessages(uuid);
            } catch (SQLException exception) {
                Logging.warning("Failed to clear ChatLog of " + uuid, exception);
            }
        });
    }

    public void cacheActivity(@NotNull UUID uuid, @NotNull String serverName) {
        activityMap.put(uuid, new Activity(serverName, System.currentTimeMillis()));
    }

    public void cachePlayer(@NotNull BanPlayer player) {
        UUID uuid = player.getUniqueId();
        CachedPlayer cachedPlayer = new CachedPlayer(player, -1);
        addressMap.put(uuid, cachedPlayer);
    }

    public void removePlayer(@NotNull UUID uuid) throws SQLException {
        CachedPlayer player = addressMap.get(uuid);
        if (player == null) return;
        player.setLeaveTime(System.currentTimeMillis());

        BanPlayer banPlayer = getPlayer(uuid);
        if (!(banPlayer instanceof PunishablePlayer)) return;
        BanDatabase database = BungeeBanPlugin.getDatabase();
        database.updatePlayerActivity(banPlayer);
    }

    @Nullable
    public Activity getActivity(@NotNull UUID uuid) {
        return activityMap.get(uuid);
    }

    public void removeActivity(@NotNull UUID uuid) {
        activityMap.remove(uuid);
    }

    @Nullable
    public BanPlayer getPlayer(@NotNull UUID uuid) {
        CachedPlayer cachedPlayer = addressMap.get(uuid);
        return cachedPlayer == null ? null : cachedPlayer.getPlayer();
    }

    @NotNull
    public List<BanPlayer> getPlayers() {
        return addressMap.values()
                .stream()
                .map(CachedPlayer::getPlayer)
                .collect(Collectors.toList());
    }

    @Override
    public void close() {
        clearTask.cancel();
        addressMap.clear();

        activityMap.forEach((uuid, activity) -> {
            BanPlayer player = getPlayer(uuid);
            if (!(player instanceof PunishablePlayer)) return;

            try {
                PunishablePlayer punishablePlayer = (PunishablePlayer) player;
                punishablePlayer.setLastServer(activity.getServerName());
                punishablePlayer.addActivity(activity.getServerName(),
                        System.currentTimeMillis() - activity.getJoinTime());
            } catch (SQLException exception) {
                Logging.warning("Failed to update activity of player '"
                        + uuid + "': " + activity, exception);
            }
        });
        activityMap.clear();
    }

    @Data
    @AllArgsConstructor
    public static class CachedPlayer {
        protected final @NotNull BanPlayer player;
        protected long leaveTime;
    }

    @Data
    @AllArgsConstructor
    public static class Activity {
        protected @NotNull String serverName;
        protected long joinTime;
    }

}
