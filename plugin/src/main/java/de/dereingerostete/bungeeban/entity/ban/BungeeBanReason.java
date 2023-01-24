/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.ban;

import de.dereingerostete.bungeeban.entity.AbstractReason;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@EqualsAndHashCode(callSuper = false)
public class BungeeBanReason extends AbstractReason implements BanReason {
    protected final BanType type;

    public BungeeBanReason(int id, @NotNull String name,
                           @NotNull String description,
                           @NotNull String displayName,
                           long[] durations, BanType type) {
        super(id, name, description, displayName, durations);
        this.type = type;
    }

    @NotNull
    @Override
    public BanType getType() {
        return type;
    }

}
