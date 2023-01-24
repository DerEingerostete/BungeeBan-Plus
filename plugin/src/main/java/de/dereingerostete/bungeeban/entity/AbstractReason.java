/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class AbstractReason implements Reason {
    protected final int id;
    protected final @NotNull String name;
    protected final @NotNull String description;
    protected final @NotNull String displayName;
    protected final long[] durations;

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getId() {
        return id;
    }

    @NotNull
    @Override
    public String getDescription() {
        return description;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public long getDuration(int timesBanned) {
        return durations.length == 0 ? -1 : durations.length > timesBanned ?
                durations[timesBanned] : durations[durations.length - 1];
    }

    @Override
    public long[] getDurations() {
        return durations;
    }

    @Override
    public boolean isPermanent(int timesBanned) {
        return getDuration(timesBanned) < 0;
    }

}
