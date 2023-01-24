/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.chatlog;

import com.google.common.base.Preconditions;
import com.pastebin.api.Expiration;
import com.pastebin.api.Visibility;
import de.dereingerostete.bungeeban.BungeeBanPlugin;
import de.dereingerostete.bungeeban.database.BanDatabase;
import de.dereingerostete.bungeeban.entity.mute.chatlog.ChatLog;
import de.dereingerostete.bungeeban.entity.mute.chatlog.Message;
import de.dereingerostete.bungeeban.entity.mute.chatlog.TextUpload;
import de.dereingerostete.bungeeban.util.Config;
import de.dereingerostete.bungeeban.util.Configs;
import de.dereingerostete.bungeeban.util.UUIDFetcher;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class ChatLogger {
    protected final @NotNull TextUpload upload;

    public ChatLogger() {
        Config config = Configs.getChatLogConfig();
        JSONObject actionsObject = config.getJSONObject("actions");
        Preconditions.checkNotNull(actionsObject, "No actions object is defined in ChatLog config");

        String defaultSelected = actionsObject.getString("uploader");
        Preconditions.checkNotNull(defaultSelected, "No uploader is defined in ChatLog config");
        upload = loadByName(defaultSelected);
    }

    @NotNull
    protected TextUpload loadByName(@NotNull String key) {
        Config config = Configs.getChatLogConfig();
        switch (key.toLowerCase()) {
            case "pastebin":
                JSONObject object = config.getJSONObject("pastebin");
                Preconditions.checkNotNull(object, "Pastebin object is not defined in ChatLog config");

                String developerKey = object.optString("developerKey", null);
                String userKey = object.optString("userKey", null);
                String expirationName = object.optString("expiration", null);
                String visibilityName = object.optString("visibility", null);

                Preconditions.checkNotNull(expirationName, "Expiration is not defined in ChatLog config");
                Preconditions.checkNotNull(visibilityName, "Visibility is not defined in ChatLog config");
                Preconditions.checkNotNull(developerKey, "DeveloperKey is not defined in ChatLog config");
                Preconditions.checkNotNull(userKey, "UserKey is not defined in ChatLog config");

                Visibility visibility = Visibility.valueOf(visibilityName);
                Expiration expiration = Expiration.valueOf(expirationName);
                PastebinAPI.Options options = new PastebinAPI.Options(developerKey, userKey, expiration, visibility);
                return new PastebinAPI(options);

            case "hastebin":
                object = config.getJSONObject("hastebin");
                Preconditions.checkNotNull(object, "ChatLog Config is missing hastebin object");

                String baseUrl = object.optString("url", null);
                Preconditions.checkNotNull(baseUrl, "No url defined in ChatLog config");
                return new HastebinAPI(baseUrl);
            default: throw new IllegalArgumentException("Unknown ChatLog uploader");
        }
    }

    @NotNull
    public ChatLog createChatLog(@NotNull UUID uuid) throws IOException, SQLException {
        BanDatabase database = BungeeBanPlugin.getDatabase();
        List<Message> messages = database.getMessages(uuid);
        Preconditions.checkState(!messages.isEmpty(), "The player has no cached messages");

        Config config = Configs.getChatLogConfig();
        String header = config.getString("header");

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss - dd.MM.yyyy");
        StringBuilder builder = new StringBuilder(header);
        builder.append("\n======================================================\n")
                .append("ChatLog created at ")
                .append(dateFormat.format(new Date()))
                .append("\n")
                .append("ChatLog of ").append(UUIDFetcher.getName(uuid)).append('/').append(uuid)
                .append("\n======================================================\n");

        messages.forEach(message -> builder.append('[')
                .append(dateFormat.format(message.getTimestamp()))
                .append("] [")
                .append(message.getServer())
                .append("] ")
                .append(message.getMessage())
                .append("\n"));

        String id = RandomStringUtils.random(12, true, true);
        File dataFolder = BungeeBanPlugin.getInstance().getDataFolder();
        File file = new File(dataFolder, "chatlogs/chatlog-" + System.currentTimeMillis() + "-" + id + ".txt");

        String content = builder.toString();
        FileUtils.write(file, content, StandardCharsets.UTF_8);
        ChatLog chatLog = new ChatLog(id, System.currentTimeMillis(), file) {

            @NotNull
            @Override
            public Link generateLink() throws IOException {
                return upload.createLink(content);
            }

        };
        database.addChatLog(uuid, chatLog);
        return chatLog;
    }

}
