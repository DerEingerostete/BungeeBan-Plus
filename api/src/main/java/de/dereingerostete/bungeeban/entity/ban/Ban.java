/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.ban;

import de.dereingerostete.bungeeban.entity.Punishment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Ban extends Punishment {

    /**
     * Gets the banned ip hash
     * @return The ip hash that is banned
     */
    @Nullable
    String getIpHash();

    /**
     * Gets the reason
     * @return The reason for the punishment
     */
    @NotNull
    default BanReason getBanReason() {
        return (BanReason) getReason();
    }

    /**
     * Gets whether the ban is ip ban
     * @return True if the ban is an ip ban; False if otherwise
     */
    default boolean isIPBan() {
        return getBanReason().isIPBan();
    }

}
