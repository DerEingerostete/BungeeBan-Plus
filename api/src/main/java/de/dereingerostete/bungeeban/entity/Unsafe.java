/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity;

import de.dereingerostete.bungeeban.database.BanDatabase;

/**
 * A class that contains unsafe values whose use should be avoided
 */
public interface Unsafe {

    /**
     * Gets the database used for saving punishments and player history
     * @return The active ban database
     */
    BanDatabase getDatabase();

}
