/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity;

import org.jetbrains.annotations.NotNull;

public interface Reason extends Identifier, Timed {

    /**
     * Gets the description of the reason
     * @return The description
     */
    @NotNull
    String getDescription();

    /**
     * Gets the display name
     * The display name will be visible for punished player
     * @return The display name
     */
    @NotNull
    String getDisplayName();

}
