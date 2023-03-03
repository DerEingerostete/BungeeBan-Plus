/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.database;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.dereingerostete.bungeeban.BungeeBan;
import de.dereingerostete.bungeeban.BungeeBanPlugin;
import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.chatlog.ChatLogger;
import de.dereingerostete.bungeeban.entity.Punishment;
import de.dereingerostete.bungeeban.entity.ban.Ban;
import de.dereingerostete.bungeeban.entity.ban.BanReason;
import de.dereingerostete.bungeeban.entity.mute.BungeeChatLog;
import de.dereingerostete.bungeeban.entity.mute.BungeeMute;
import de.dereingerostete.bungeeban.entity.mute.Mute;
import de.dereingerostete.bungeeban.entity.mute.MuteReason;
import de.dereingerostete.bungeeban.entity.mute.chatlog.ChatLog;
import de.dereingerostete.bungeeban.entity.mute.chatlog.Message;
import de.dereingerostete.bungeeban.entity.mute.chatlog.TextUpload;
import de.dereingerostete.bungeeban.entity.player.BanPlayer;
import de.dereingerostete.bungeeban.entity.player.BungeeBanPlayer;
import de.dereingerostete.bungeeban.entity.player.PlayerHistory;
import de.dereingerostete.bungeeban.util.Config;
import de.dereingerostete.bungeeban.util.Configs;
import de.dereingerostete.bungeeban.util.MapUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BungeeBanDatabase extends SQLDatabase implements BanDatabase {
    protected final int chatBacklog;

    public BungeeBanDatabase(DatabaseType type) {
        super(type);

        Config config = Configs.getChatLogConfig();
        JSONObject object = config.getJSONObject("actions");
        Preconditions.checkNotNull(object, "No actions object defined ChatLog config");
        chatBacklog = object.optInt("chatBacklog", 50);
    }

    @Override
    public void onConnected() throws SQLException {
        Logging.info("Established database connection (" + type.getName() + ")");
        update("CREATE TABLE IF NOT EXISTS `Punishments` (`uuid` VARCHAR(64), `id` VARCHAR(64), `reasonId` INT, " +
                "`punisher` VARCHAR(64), `punisherName` VARCHAR(64), `timeOfPunishment` BIGINT, `timesBanned` INT, " +
                "`extraAttributes` LONGTEXT, `type` SMALLINT);");
        update("CREATE TABLE IF NOT EXISTS `PunishmentHistory` (`uuid` VARCHAR(64), `id` VARCHAR(64), " +
                "`reasonId` INT, `punisher` VARCHAR(64), `punisherName` VARCHAR(64), `timeOfPunishment` BIGINT, " +
                "`timesBanned` INT, `extraAttributes` LONGTEXT, `type` SMALLINT);");
        update("CREATE TABLE IF NOT EXISTS `PlayerActivity` (`uuid` VARCHAR(64), " +
                "`activities` LONGTEXT, `lastServer` VARCHAR);");
        update("CREATE TABLE IF NOT EXISTS `LatestChat` (`uuid` VARCHAR(64) NOT NULL, " +
                "`message` TEXT NOT NULL, `server` TEXT NOT NULL, `timestamp` BIGINT NOT NULL);");
        update("CREATE TABLE IF NOT EXISTS `ChatLogs` (`uuid` VARCHAR(64) NOT NULL, " +
                "`id` VARCHAR(12) NOT NULL, `timestamp` BIGINT NOT NULL, `filePath` VARCHAR NOT NULL);");
    }

    @Nullable
    @Override
    public BanPlayer getPlayer(@NotNull UUID uuid) throws SQLException {
        ResultSet resultSet = query("select * from Punishments where uuid=? limit 2;", uuid.toString());

        Ban ban = null;
        Mute mute = null;
        while (resultSet.next()) {
            Punishment punishment = loadPunishment(resultSet, uuid);
            if (punishment instanceof Ban) ban = (Ban) punishment;
            else if (punishment instanceof Mute) mute = (Mute) punishment;
        }

        resultSet.close();
        resultSet = query("select * from PunishmentHistory where uuid=?;", uuid.toString());

        List<Punishment> punishments = Lists.newArrayList();
        while (resultSet.next()) punishments.add(loadPunishment(resultSet, uuid));
        PlayerHistory history = new PlayerHistory(punishments);

        resultSet.close();
        resultSet = query("select * from PlayerActivity where uuid=? limit 1;", uuid.toString());

        Map<String, Long> activityMap;
        String lastServer;
        if (resultSet.next()) {
            lastServer = resultSet.getString("lastServer");

            String activities = resultSet.getString("activities");
            MapUtils.StringConverter stringConverter = new MapUtils.StringConverter();
            MapUtils.LongConverter longConverter = new MapUtils.LongConverter();
            activityMap = MapUtils.fromString(activities, stringConverter, longConverter);
        } else {
            activityMap = Maps.newHashMap();
            lastServer = null;
        }

        return new BungeeBanPlayer(uuid, history, ban,
                mute, activityMap, lastServer);
    }

    protected Punishment loadPunishment(@NotNull ResultSet resultSet,
                                        @NotNull UUID playerUuid) throws SQLException {
        String punisherName = resultSet.getString("punisherName");
        String punisher = resultSet.getString("punisher");
        UUID punisherUuid = UUID.fromString(punisher);

        String extraAttributes = resultSet.getString("extraAttributes");
        long timeOfPunishment = resultSet.getLong("timeOfPunishment");

        String id = resultSet.getString("id");
        int reasonId = resultSet.getInt("reasonId");
        int timesBanned = resultSet.getInt("timesBanned");

        short punishmentType = resultSet.getShort("type");
        switch (punishmentType) {
            case 0: {
                BanReason reason = BungeeBan.getBanReasonById(reasonId);
                Preconditions.checkNotNull(reason, "Unknown reason");
                return new de.dereingerostete.bungeeban.entity.ban.
                        BungeeBan(id, playerUuid, punisherUuid, punisherName, reason,
                        timeOfPunishment, timesBanned, extraAttributes);
            }
            case 1: {
                MuteReason reason = BungeeBan.getMuteReasonById(reasonId);
                Preconditions.checkNotNull(reason, "Unknown reason");

                ChatLog chatLog = null;
                try {
                    if (extraAttributes != null) {
                        String[] values = extraAttributes.split(",", 3);
                        String chatlogId = values[0];
                        long createdAt = Long.parseLong(values[1]);
                        File file = new File(values[2]);
                        chatLog = new BungeeChatLog(chatlogId, createdAt, file);
                    }
                } catch (IOException exception) {
                    throw new SQLException("Failed to load ChatLog", exception);
                }

                return new BungeeMute(id, playerUuid, punisherUuid, punisherName,
                        reason, timeOfPunishment, timesBanned, chatLog);

            }
            default: throw new IllegalStateException("Unknown punishment type");
        }
    }

    @Override
    @NotNull
    public List<Ban> getBansByIP(@NotNull String ipHash) throws SQLException {
        ResultSet resultSet = query("select * from Punishments where type=?;", 0);
        List<Ban> bans = Lists.newArrayList();

        while (resultSet.next()) {
            String extraAttributes = resultSet.getString("extraAttributes");
            if (extraAttributes != null && !extraAttributes.contains(ipHash)) continue;

            UUID uuid = UUID.fromString(resultSet.getString("uuid"));
            Punishment punishment = loadPunishment(resultSet, uuid);

            if (!(punishment instanceof Ban)) {
                Logging.warning("Punishment should implement Ban (Type: "
                        + punishment.getClass().getCanonicalName() + ")! Skipping!");
                continue;
            }

            bans.add((Ban) punishment);
        }
        resultSet.close();
        return bans;
    }

    @Override
    @NotNull
    public BanPlayer createPlayer(@NotNull UUID uuid) throws SQLException {
        update("insert into PlayerActivity (uuid, activities, lastServer) values (?, ?, ?);",
                uuid, "", null);

        PlayerHistory history = new PlayerHistory(Lists.newArrayList());
        return new BungeeBanPlayer(uuid, history, null, null, Maps.newHashMap(), null);
    }

    @Override
    public void updatePlayer(@NotNull BanPlayer player) throws SQLException {
        UUID uuid = player.getUniqueId();
        if (removePlayer(uuid)) Logging.warning("Could not remove player (" + uuid + ")");

        Punishment activePunishment = player.getBan();
        if (activePunishment != null) addPunishment(activePunishment);

        activePunishment = player.getMute();
        if (activePunishment != null) addPunishment(activePunishment);

        updatePlayerActivity(player);
        PlayerHistory history = player.getHistory();
        List<Punishment> punishments = history.getPunishments();

        for (Punishment punishment : punishments)
            addPunishmentHistory(punishment);
    }

    @Override
    public void addPunishment(@NotNull Punishment punishment) throws SQLException {
        addPunishment("Punishments", punishment);
    }

    @Override
    public void addPunishmentHistory(@NotNull Punishment punishment) throws SQLException {
        addPunishment("PunishmentHistory", punishment);
    }

    protected void addPunishment(@NotNull String database,
                                 @NotNull Punishment punishment) throws SQLException {
        String extraAttributes;
        short type;
        if (punishment instanceof Ban) {
            type = 0;
            extraAttributes = ((Ban) punishment).getIpHash();
        } else if (punishment instanceof Mute) {
            type = 1;

            Mute mute = (Mute) punishment;
            ChatLog chatLog = mute.getChatLog();
            if (chatLog != null) extraAttributes = chatLog.getId() + ","
                    + chatLog.getCreatedAt() + "," + chatLog.getFile().getPath();
            else extraAttributes = null;
        } else {
            extraAttributes = null;
            type = -1;
        }

        update("insert into " + database +
                        " (uuid, id, reasonId, punisher, punisherName," +
                        " timeOfPunishment, timesBanned, extraAttributes, type) " +
                        "values (?, ?, ?, ?, ?, ?, ?, ?, ?);",
                punishment.getPlayer(),
                punishment.getId(),
                punishment.getReason().getId(),
                punishment.getPunisher(),
                punishment.getPunisherName(),
                punishment.getTimeOfPunishment(),
                punishment.getTimesBanned(),
                extraAttributes,
                type
        );
    }

    @Override
    public void updatePlayerActivity(@NotNull BanPlayer player) throws SQLException {
        String activities = MapUtils.toString(player.getActivity());
        String lastServer = player.getLastServer();
        String uuidString = player.getUniqueId().toString();

        ResultSet resultSet = query("select uuid from PlayerActivity where uuid = ? LIMIT 1;", uuidString);
        if (resultSet.next()) {
            update("update PlayerActivity SET activities=?, lastServer=? " +
                            "WHERE rowid = (SELECT rowid FROM PlayerActivity WHERE uuid=? LIMIT 1);",
                    activities, lastServer, uuidString);
        } else update("insert into PlayerActivity (uuid, activities, lastServer) values (?, ?, ?);",
                    uuidString, activities, lastServer);
        resultSet.close();
    }

    @Override
    public void removePunishment(@NotNull String id) throws SQLException {
        update("delete from Punishments where rowid = (select rowid from Punishments where id=? limit 1);", id);
    }

    @Override
    public void removeHistory(@NotNull String id) throws SQLException {
        update("delete from PunishmentHistory where rowid = " +
                "(select rowid from PunishmentHistory where id=? limit 1);", id);
    }

    @Override
    public void removeHistory(@NotNull UUID uuid) throws SQLException {
        update("delete from PunishmentHistory where uuid=?;", uuid.toString());
    }

    @Override
    public boolean removePlayer(@NotNull UUID uuid) throws SQLException {
        String uuidString = uuid.toString();
        boolean removedPunishments = update("remove from Punishments where uuid=?;", uuidString) > 0;
        boolean removedHistory = update("remove from PunishmentHistory where uuid=?;", uuidString) > 0;
        boolean removedActivity = update("remove from PlayerActivity where uuid=?;", uuidString) > 0;
        return removedPunishments || removedHistory || removedActivity;
    }

    @Override
    public void addMessage(@NotNull Message message) throws SQLException {
        UUID uuid = message.getUniqueId();
        update("insert into LatestChat (uuid, message, server, timestamp) VALUES (?, ?, ?, ?);",
                uuid, message.getMessage(), message.getServer(), message.getTimestamp());

        ResultSet resultSet = query("select uuid, timestamp from " +
                "LatestChat where uuid = ? ORDER BY timestamp DESC;", uuid);
        long deleteAfter = -1;
        int i = 0;
        while (resultSet.next()) {
            i++;
            if (i >= chatBacklog) {
                deleteAfter = resultSet.getLong("timestamp");
                resultSet.close();
                break;
            }
        }
        if (deleteAfter != -1) update("delete from LatestChat where timestamp < ?;", deleteAfter);
    }

    @Override
    public void clearMessages(@NotNull UUID uuid) throws SQLException {
        update("delete from LatestChat where uuid = ?;", uuid);
    }

    @NotNull
    @Override
    public List<Message> getMessages(@NotNull UUID uuid) throws SQLException {
        ResultSet resultSet = query("select message, server, timestamp from LatestChat " +
                "where uuid = ? order by timestamp;", uuid);
        List<Message> messages = Lists.newArrayList();

        while (resultSet.next()) {
            String message = resultSet.getString("message");
            String server = resultSet.getString("server");
            long timestamp = resultSet.getLong("timestamp");
            messages.add(new Message(uuid, message, server, timestamp));
        }
        resultSet.close();
        return messages;
    }

    @Override
    public void addChatLog(@NotNull UUID uuid, @NotNull ChatLog chatLog) throws SQLException {
        update("insert into ChatLogs (uuid, id, timestamp, filePath) values (?, ?, ?, ?);",
                uuid, chatLog.getId(), chatLog.getCreatedAt(), chatLog.getFile().getPath());
    }

    @Override
    @NotNull
    public List<ChatLog> getChatLogs(@NotNull UUID uuid, @NotNull TextUpload upload) throws SQLException {
        ResultSet resultSet = query("select * from ChatLogs where uuid = ?;", uuid);
        List<ChatLog> chatLogs = Lists.newArrayList();

        while (resultSet.next()) {
            String id = resultSet.getString("id");
            long timestamp = resultSet.getLong("timestamp");
            String path = resultSet.getString("filePath");
            File file = new File(path);

            ChatLog chatLog = buildChatLog(id, timestamp, file, upload);
            chatLogs.add(chatLog);
        }
        resultSet.close();
        return chatLogs;
    }

    @Override
    @Nullable
    public ChatLog getChatLog(@NotNull String id, @NotNull TextUpload upload) throws SQLException {
        ResultSet resultSet = query("select * from ChatLogs where id = ?;", id);
        if (!resultSet.next()) return null;

        long timestamp = resultSet.getLong("timestamp");
        String path = resultSet.getString("filePath");
        File file = new File(path);
        resultSet.close();
        return buildChatLog(id, timestamp, file, upload);
    }

    @NotNull
    protected ChatLog buildChatLog(String id, long timestamp, File file, TextUpload upload) {
        return new ChatLog(id, timestamp, file) {

            @Override
            @NotNull
            public Link generateLink() throws IOException {
                String content;
                ChatLogger logger = BungeeBanPlugin.getChatLogger();
                String directoryPath = logger.getLogDirectoryPath();
                if (!file.getCanonicalPath().startsWith(directoryPath)) throw new IOException("File access not allowed");

                if (file.exists()) content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                else content = "ChatLog could not be found";
                return upload.createLink(content);
            }

        };
    }

}