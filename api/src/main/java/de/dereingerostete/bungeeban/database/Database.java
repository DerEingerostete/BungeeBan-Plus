/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.database;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public interface Database {

    /**
     * Gets the type of the database
     * @return The type
     */
    @NotNull
    DatabaseType getType();

    /**
     * Gets the address / hostname of the database
     * @return The address / hostname or null if the database is disconnected
     */
    @Nullable
    String getAddress();

    /**
     * Attempts to connect to the database
     * @throws SQLException If a database access error occurs
     */
    @ApiStatus.Internal
    void connect() throws SQLException;

    /**
     * Gets whether the database is connected or not
     * @return The connection state
     */
    @ApiStatus.Internal
    boolean isConnected();

    /**
     * Attempts to disconnect from the database
     * @throws SQLException If a database access error occurs
     */
    @ApiStatus.Internal
    void disconnect() throws SQLException;

}
