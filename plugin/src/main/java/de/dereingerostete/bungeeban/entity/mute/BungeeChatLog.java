/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.mute;

import de.dereingerostete.bungeeban.BungeeBanPlugin;
import de.dereingerostete.bungeeban.chatlog.ChatLogger;
import de.dereingerostete.bungeeban.entity.mute.chatlog.ChatLog;
import de.dereingerostete.bungeeban.entity.mute.chatlog.TextUpload;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BungeeChatLog extends ChatLog {
    protected final String content;

    public BungeeChatLog(@NotNull String id, long createdAt, @NotNull File file) throws IOException {
        super(id, createdAt, file);
        if (file.exists()) content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        else content = "ChatLog could not be found";
    }

    @SneakyThrows(IOException.class)
    @NotNull
    @Override
    public Link generateLink() {
        ChatLogger logger = BungeeBanPlugin.getChatLogger();
        TextUpload upload = logger.getUpload();
        return upload.createLink(content);
    }

}
