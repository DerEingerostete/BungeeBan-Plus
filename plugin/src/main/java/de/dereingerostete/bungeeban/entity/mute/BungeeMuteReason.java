/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.mute;

import de.dereingerostete.bungeeban.entity.AbstractReason;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@EqualsAndHashCode(callSuper = false)
public class BungeeMuteReason extends AbstractReason implements MuteReason {
    protected final boolean requiresChatLog;

    public BungeeMuteReason(int id, @NotNull String name, @NotNull String description,
                            @NotNull String displayName,
                            long[] durations, boolean requiresChatLog) {
        super(id, name, description, displayName, durations);
        this.requiresChatLog = requiresChatLog;
    }

    @Override
    public boolean requiresChatLog() {
        return requiresChatLog;
    }

}
