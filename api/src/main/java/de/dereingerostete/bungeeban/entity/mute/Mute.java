/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.mute;

import de.dereingerostete.bungeeban.entity.Punishment;
import de.dereingerostete.bungeeban.entity.mute.chatlog.ChatLog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Mute extends Punishment {

    /**
     * Gets the reason
     * @return The reason for the punishment
     */
    @NotNull
    default MuteReason getMuteReason() {
        return (MuteReason) getReason();
    }

    /**
     * Gets the chat log
     * @return The chat log
     */
    @Nullable
    ChatLog getChatLog();

}
