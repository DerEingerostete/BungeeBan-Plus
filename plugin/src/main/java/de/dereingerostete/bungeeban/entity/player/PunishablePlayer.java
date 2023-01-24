/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.player;

import de.dereingerostete.bungeeban.BungeeBan;
import de.dereingerostete.bungeeban.BungeeBanPlugin;
import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.chatlog.ChatLogger;
import de.dereingerostete.bungeeban.database.BanDatabase;
import de.dereingerostete.bungeeban.entity.Cache;
import de.dereingerostete.bungeeban.entity.ban.Ban;
import de.dereingerostete.bungeeban.entity.ban.BanReason;
import de.dereingerostete.bungeeban.entity.mute.BungeeMute;
import de.dereingerostete.bungeeban.entity.mute.Mute;
import de.dereingerostete.bungeeban.entity.mute.MuteReason;
import de.dereingerostete.bungeeban.entity.mute.chatlog.ChatLog;
import de.dereingerostete.bungeeban.event.EventHandler;
import de.dereingerostete.bungeeban.event.player.PlayerBanEvent;
import de.dereingerostete.bungeeban.event.player.PlayerMuteEvent;
import de.dereingerostete.bungeeban.event.player.PlayerUnbanEvent;
import de.dereingerostete.bungeeban.event.player.PlayerUnmuteEvent;
import de.dereingerostete.bungeeban.exception.BanException;
import de.dereingerostete.bungeeban.util.MessageUtils;
import de.dereingerostete.bungeeban.util.UUIDFetcher;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public abstract class PunishablePlayer implements BanPlayer {
    protected final BanDatabase database = BungeeBanPlugin.getDatabase();
    protected final EventHandler handler = BungeeBan.getEventHandler();

    protected final @NotNull PlayerHistory history;
    protected @Nullable Ban ban;
    protected @Nullable Mute mute;

    protected final @NotNull Map<String, Long> activityMap;
    protected @Nullable String lastServer;

    @NotNull
    @Override
    public PlayerHistory getHistory() {
        return history;
    }

    @NotNull
    @Override
    public Map<String, Long> getActivity() {
        Cache cache = BungeeBanPlugin.getCache();
        Cache.Activity activity = cache.getActivity(getUniqueId());
        if (activity == null) return Map.copyOf(activityMap);

        String serverName = activity.getServerName();
        long playTime = activityMap.getOrDefault(serverName, -1L);
        if (playTime == -1) return Map.copyOf(activityMap);

        Map<String, Long> mapCopy = new HashMap<>(activityMap);
        mapCopy.replace(serverName, playTime + (System.currentTimeMillis() - activity.getJoinTime()));
        return mapCopy;
    }

    @Nullable
    @Override
    public String getLastServer() {
        return lastServer;
    }

    @Override
    public boolean isBanned() {
        return ban != null;
    }

    @Override
    public boolean isMuted() {
        return mute != null;
    }

    @Nullable
    @Override
    public Ban getBan() {
        return ban;
    }

    @Nullable
    @Override
    public Mute getMute() {
        return mute;
    }

    @Override
    public int getTotalBans() {
        return history.getBans().size();
    }

    @Override
    public int getTotalMutes() {
        return history.getMutes().size();
    }

    @Override
    public int getTotalPunishments() {
        int punishments = history.getPunishments().size();
        if (isBanned()) punishments++;
        if (isMuted()) punishments++;
        return punishments;
    }

    @Override
    public void banPlayer(@NotNull BanReason reason, @NotNull UUID punisherUUID) throws BanException {
        if (isBanned()) throw new BanException("The player is already banned");
        String randomId = RandomStringUtils.random(16, true, true);

        UUID uuid = getUniqueId();
        String address = getAddress();
        if (reason.isIPBan() && address == null) throw new BanException("No IP address found for player: " + uuid);

        String punisherName;
        ProxyServer server = ProxyServer.getInstance();
        ProxiedPlayer player = server.getPlayer(uuid);
        if (player != null) punisherName = player.getName();
        else punisherName = UUIDFetcher.getName(getUniqueId());
        Validate.notNull(punisherName, "Punisher is null");

        Ban ban = new de.dereingerostete.bungeeban.entity.ban.BungeeBan(randomId,
                uuid, punisherUUID, punisherName, reason, System.currentTimeMillis(),
                getTotalBans(), address);
        banPlayer(ban);
    }

    @Override
    public void banPlayer(@NotNull Ban ban) throws BanException {
        if (isBanned()) throw new BanException("The player is already banned");

        PlayerBanEvent event = new PlayerBanEvent(this, ban);
        handler.callEvent(event);
        if (event.isCancelled()) return;

        try {
            database.addPunishment(ban);
            history.addPunishment(ban);
            this.ban = ban;
        } catch (SQLException exception) {
            throw new BanException("An unexpected database error has occurred", exception);
        }

        disconnect(MessageUtils.formatBan(ban));
    }

    @Override
    public void unbanPlayer() throws BanException {
        if (!isBanned()) throw new BanException("The player is not banned");

        PlayerUnbanEvent event = new PlayerUnbanEvent(this);
        handler.callEvent(event);
        if (event.isCancelled()) return;

        try {
            if (ban != null) {
                database.removePunishment(ban.getId());
                database.addPunishmentHistory(ban);
                this.ban = null;
            }
        } catch (SQLException exception) {
            throw new BanException("An unexpected database error has occurred", exception);
        }
    }

    @Override
    public void mutePlayer(@NotNull MuteReason reason, @NotNull UUID punisherUUID) throws BanException {
        if (isMuted()) throw new BanException("The player is already muted");
        String randomId = RandomStringUtils.random(16, true, true);
        UUID uuid = getUniqueId();

        ChatLog log;
        try {
            ChatLogger logger = BungeeBanPlugin.getChatLogger();
            log = reason.requiresChatLog() ? logger.createChatLog(uuid) : null;
        } catch (IOException | SQLException exception) {
            throw new BanException("Failed to create ChatLog", exception);
        }

        String punisherName;
        ProxyServer server = ProxyServer.getInstance();
        ProxiedPlayer player = server.getPlayer(uuid);
        if (player != null) punisherName = player.getName();
        else punisherName = UUIDFetcher.getName(getUniqueId());
        Validate.notNull(punisherName, "Punisher is null");

        Mute mute = new BungeeMute(randomId, getUniqueId(), punisherUUID, punisherName,
                reason, System.currentTimeMillis(), getTotalMutes(), log);
        mutePlayer(mute);
    }

    @Override
    public void mutePlayer(@NotNull Mute mute) throws BanException {
        if (isMuted()) throw new BanException("The player is already muted");

        PlayerMuteEvent event = new PlayerMuteEvent(this, mute);
        handler.callEvent(event);
        if (event.isCancelled()) return;

        try {
            database.addPunishment(mute);
            history.addPunishment(mute);
            this.mute = mute;
        } catch (SQLException exception) {
            throw new BanException("An unexpected database error has occurred", exception);
        }
    }

    @Override
    public void unmutePlayer() throws BanException {
        if (!isMuted()) throw new BanException("The player is not muted");

        PlayerUnmuteEvent event = new PlayerUnmuteEvent(this);
        handler.callEvent(event);
        if (event.isCancelled()) return;

        try {
            if (mute != null) {
                database.removePunishment(mute.getId());
                database.addPunishmentHistory(mute);
                mute = null;
            }
        } catch (SQLException exception) {
            throw new BanException("An unexpected database error has occurred", exception);
        }
    }

    public void setLastServer(@Nullable String lastServer) throws SQLException {
        this.lastServer = lastServer;
        database.updatePlayerActivity(this);
    }

    public void addActivity(@NotNull String key, long addValue) throws SQLException {
        long value = activityMap.getOrDefault(key, 0L) + addValue;
        activityMap.put(key, value);
        database.updatePlayerActivity(this);
    }

    @NotNull
    @Override
    public ChatLog createChatLog() throws IllegalStateException, SQLException, IOException {
        ChatLogger logger = BungeeBanPlugin.getChatLogger();
        return logger.createChatLog(getUniqueId());
    }

    @Override
    public void refresh() {
        try {
            if (ban != null && ban.hasEnded()) {
                database.removePunishment(ban.getId());
                database.addPunishmentHistory(ban);
                ban = null;
            }

            if (mute != null && mute.hasEnded()) {
                database.removePunishment(mute.getId());
                database.addPunishmentHistory(mute);
                mute = null;
            }
        } catch (SQLException exception) {
            Logging.warning("Could not refresh player '" + getUniqueId() + "'", exception);
        }
    }

}
