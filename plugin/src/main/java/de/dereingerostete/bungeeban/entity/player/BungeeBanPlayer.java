/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.player;

import de.dereingerostete.bungeeban.chat.Chat;
import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.entity.ban.Ban;
import de.dereingerostete.bungeeban.entity.mute.Mute;
import de.dereingerostete.bungeeban.util.ArrayUtils;
import de.dereingerostete.bungeeban.util.HashUtils;
import de.dereingerostete.bungeeban.util.Lang;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.UUID;

public class BungeeBanPlayer extends PunishablePlayer {
    protected final @NotNull UUID uuid;
    protected ProxiedPlayer player;
    protected String hashedAddress;

    public BungeeBanPlayer(@NotNull UUID uuid, @NotNull PlayerHistory history,
                           @Nullable Ban ban, @Nullable Mute mute,
                           @NotNull Map<String, Long> activityMap,
                           @Nullable String lastServer) {
        super(history, ban, mute, activityMap, lastServer);
        this.uuid = uuid;
    }

    public BungeeBanPlayer(@NotNull UUID uuid, @NotNull PlayerHistory history,
                           @Nullable Ban ban, @Nullable Mute mute,
                           @NotNull Map<String, Long> activityMap,
                           @Nullable String lastServer,
                           ProxiedPlayer player) {
        super(history, ban, mute, activityMap, lastServer);
        this.uuid = uuid;
        this.player = player;
    }

    @NotNull
    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Nullable
    @Override
    public String getAddress() {
        if (player != null) {
            SocketAddress address = player.getSocketAddress();
            if (address == null) return hashedAddress;

            if (address instanceof InetSocketAddress)
                return (hashedAddress = HashUtils.hashAddress(((InetSocketAddress) address).getAddress()));
            else {
                Logging.debug("Could not retrieve address of player '" + uuid
                        + "': Address is no InetSocketAddress, Type: "
                        + address.getClass().getCanonicalName());
                return hashedAddress;
            }
        } else return hashedAddress;
    }

    @Override
    public boolean isOnline() {
        return player != null ? player.isConnected() :
                (player = ProxyServer.getInstance().getPlayer(uuid)) != null && player.isConnected();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        updatePlayer();
        if (player != null) Chat.toPlayer(player, message);
    }

    @Override
    public void sendMessage(@NotNull String messageKey, Object... parameters) {
        sendMessage(Lang.getMessage(messageKey, true, parameters));
    }

    @Override
    public void sendMessage(@NotNull String[] lines) {
        sendMessage(ArrayUtils.toLines(lines));
    }

    @Override
    public void disconnect(@NotNull String message) {
        updatePlayer();
        if (player != null) player.disconnect(new TextComponent(message));
        else Logging.warning("Could not kick player " + uuid + ": ProxiedPlayer is null");
    }

    @Override
    public void disconnect(@NotNull String[] lines) {
        disconnect(ArrayUtils.toLines(lines));
    }

    @Override
    public void disconnect(@NotNull String messageKey, Object... values) {
        String[] message = Lang.getLayout(messageKey, false, values);
        disconnect(message);
    }

    protected void updatePlayer() {
        if (player == null) player = ProxyServer.getInstance().getPlayer(uuid);
    }

}
