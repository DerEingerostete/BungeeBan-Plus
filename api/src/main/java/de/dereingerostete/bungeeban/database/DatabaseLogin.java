/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.database;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseLogin {

    /**
     * Creates a new database connection
     * @param key The key of the database in the config
     * @return The created database connection
     * @throws SQLException If a database access error occurs
     */
    @NotNull
    DatabaseLogin.DatabaseConnection build(String key) throws SQLException;

    /**
     * Fetches the address / hostname of the database
     * @return The address / hostname
     */
    @NotNull
    String fetchAddress(String key);

    @Data
    class DatabaseConnection {
        protected final String address;
        protected final Connection connection;
    }

}
