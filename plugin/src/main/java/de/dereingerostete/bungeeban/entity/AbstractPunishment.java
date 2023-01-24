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

import java.util.UUID;

@ToString
@AllArgsConstructor
@EqualsAndHashCode
public abstract class AbstractPunishment implements Punishment {
    protected final @NotNull String id;
    protected final @NotNull UUID player;
    protected final @NotNull UUID punisher;
    protected final @NotNull String punisherName;
    protected final @NotNull Reason reason;
    protected final long timeOfPunishment;
    protected final int timesBanned;

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public UUID getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public UUID getPunisher() {
        return punisher;
    }

    @NotNull
    @Override
    public String getPunisherName() {
        return punisherName;
    }

    @NotNull
    @Override
    public Reason getReason() {
        return reason;
    }

    @Override
    public long getTimeOfPunishment() {
        return timeOfPunishment;
    }

    @Override
    public int getTimesBanned() {
        return timesBanned;
    }

}
