/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.ban;

import de.dereingerostete.bungeeban.entity.AbstractPunishment;
import de.dereingerostete.bungeeban.entity.Reason;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@ToString
@EqualsAndHashCode(callSuper = false)
public class BungeeBan extends AbstractPunishment implements Ban {
    protected final @Nullable String ipHash;

    public BungeeBan(@NotNull String id, @NotNull UUID player,
                     @NotNull UUID punisher, @NotNull String punisherName, @NotNull Reason reason,
                     long timeOfPunishment, int timesBanned, @Nullable String ipHash) {
        super(id, player, punisher, punisherName, reason, timeOfPunishment, timesBanned);
        this.ipHash = ipHash;
    }

    @Nullable
    @Override
    public String getIpHash() {
        return ipHash;
    }

}
