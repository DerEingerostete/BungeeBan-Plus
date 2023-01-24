/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.ban;

import de.dereingerostete.bungeeban.entity.Reason;
import org.jetbrains.annotations.NotNull;

public interface BanReason extends Reason {

    /**
     * Gets the type of ban
     * @return The ban type
     */
    @NotNull
    BanType getType();

    /**
     * Gets whether the ban is ip ban
     * @return True if the ban is an ip ban; False if otherwise
     */
    default boolean isIPBan() {
        return getType() == BanType.IP;
    }

    enum BanType {
        UUID,
        IP
    }

}
