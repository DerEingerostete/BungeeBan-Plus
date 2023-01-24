/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.database;

import lombok.ToString;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ToString
public enum DatabaseType {
    SQLITE("SQLite", "org.sqlite.JDBC"),
    MYSQL("MySQL", null);

    private final @NotNull String name;
    private final @Nullable String driverClass;

    DatabaseType(@NotNull String name, @Nullable String driverClass) {
        this.name = name;
        this.driverClass = driverClass;
    }

    /**
     * Gets the name of the database type
     * @return The database type
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Gets the driver class
     * @return The diver class name
     */
    @ApiStatus.Internal
    @Nullable
    public String getDriverClass() {
        return driverClass;
    }

    @Nullable
    public static DatabaseType byName(@Nullable String name) {
        try {
            if (name == null) return null;
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

}
