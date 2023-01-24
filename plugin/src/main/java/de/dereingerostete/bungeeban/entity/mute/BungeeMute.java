/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.mute;

import de.dereingerostete.bungeeban.entity.AbstractPunishment;
import de.dereingerostete.bungeeban.entity.Reason;
import de.dereingerostete.bungeeban.entity.mute.chatlog.ChatLog;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@ToString
@EqualsAndHashCode(callSuper = false)
public class BungeeMute extends AbstractPunishment implements Mute {
    protected final @Nullable ChatLog chatLog;

    public BungeeMute(@NotNull String id, @NotNull UUID player,
                      @NotNull UUID punisher, @NotNull String punisherName,
                      @NotNull Reason reason, long timeOfPunishment, int timesBanned,
                      @Nullable ChatLog chatLog) {
        super(id, player, punisher, punisherName, reason, timeOfPunishment, timesBanned);
        this.chatLog = chatLog;
    }

    @Nullable
    @Override
    public ChatLog getChatLog() {
        return chatLog;
    }

}
