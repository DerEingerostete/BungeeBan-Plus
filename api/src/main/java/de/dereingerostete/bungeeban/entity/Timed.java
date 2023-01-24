/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity;

public interface Timed {

    /**
     * Gets the duration
     * @param timesBanned The amount of times a player was banned for a specified reason
     * @return The duration in milliseconds
     */
    long getDuration(int timesBanned);

    /**
     * Gets an array of all durations
     * @return The array of durations in milliseconds
     */
    long[] getDurations();

    /**
     * Gets if there is no duration
     * @param timesBanned The amount of times a player was banned for a specified reason
     * @return True if permanent; False if not
     */
    boolean isPermanent(int timesBanned);

}
