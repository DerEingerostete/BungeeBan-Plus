/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Punishment {

    /**
     * Gets the id of the punishment
     * The id is a unique nine-digit number and letter string to identify the punishment
     * @return The id of the punishment
     */
    @NotNull
    String getId();

    /**
     * Gets the punished player
     * @return The uuid of the punished player
     */
    @NotNull
    UUID getPlayer();

    /**
     * Gets the player that performed the punishment
     * @return The uuid of the player performing the punishment
     */
    @NotNull
    UUID getPunisher();

    /**
     * Gets the name of the player that performed the punishment
     * @return The name of the player performing the punishment
     */
    @NotNull
    String getPunisherName();

    /**
     * Gets the reason
     * @return The reason for the punishment
     */
    @NotNull
    Reason getReason();

    /**
     * Gets the time when the player got punished
     * @return The time of punishment
     */
    long getTimeOfPunishment();

    /**
     * The number of times a player has been punished for the same reason as this punished
     * @return The number of times
     */
    int getTimesBanned();

    /**
     * Gets the time when the punishment ends
     * @return The time when the punishment ends or -1 if the punishment is permanent
     */
    default long getTimeOfEnd() {
        return isPermanent() ? -1 : getTimeOfPunishment() + getDuration();
    }

    /**
     * Gets whether the punishment is permanent or not
     * @return True if permanent; False if not
     */
    default boolean isPermanent() {
        return getReason().isPermanent(getTimesBanned());
    }

    /**
     * Gets the duration of the punishment
     * @return The duration in milliseconds
     */
    default long getDuration() {
        return getReason().getDuration(getTimesBanned());
    }

    /**
     * Checks whether the punishment ended already
     * @return The activity state
     */
    default boolean hasEnded() {
        long timeOfEnd = getTimeOfEnd();
        return timeOfEnd != -1 && System.currentTimeMillis() >= timeOfEnd;
    }

}
