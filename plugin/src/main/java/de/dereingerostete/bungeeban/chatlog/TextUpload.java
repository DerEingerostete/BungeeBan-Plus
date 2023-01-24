/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.chatlog;

import de.dereingerostete.bungeeban.entity.mute.chatlog.ChatLog;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface TextUpload {

    @NotNull
    String post(@NotNull String content) throws IOException;

    @NotNull
    ChatLog.Link createLink(@NotNull String content) throws IOException;

}
