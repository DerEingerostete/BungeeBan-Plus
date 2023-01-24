/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.database;

import de.dereingerostete.bungeeban.chat.Logging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

public class DriverUtils {

    @NotNull
    public static Driver loadDriver(String url, String alternateDriverClass) throws SQLException {
        Driver driver = getRegisteredDriver(url);
        if (driver != null) {
            Logging.debug("Loaded SQLite Driver '" + driver.getClass().getCanonicalName() + '\'');
            return driver;
        }

        driver = registerDriver(alternateDriverClass);
        DriverManager.registerDriver(driver);

        Class<? extends Driver> driverClass = driver.getClass();
        Logging.debug("Registered SQLite Driver (" + driverClass.getCanonicalName() + ')');
        return driver;
    }

    @Nullable
    public static Driver getRegisteredDriver(String url) {
        try {
            return DriverManager.getDriver(url);
        } catch (SQLException exception) {
            return null;
        }
    }

    @NotNull
    public static Driver registerDriver(String className) throws SQLException {
        Constructor<?> constructor;
        try {
            Class<?> driverClass = Class.forName(className);
            Constructor<?>[] constructors = driverClass.getConstructors();

            constructor = Arrays.stream(constructors)
                    .filter(c -> c.getParameterCount() == 0)
                    .findAny().orElse(null);
        } catch (ClassNotFoundException exception) {
            throw new SQLException("Failed to find Driver '" + className + '\'');
        }

        if (constructor == null)
            throw new SQLException("Failed to find public constructor with no parameters");

        try {
            Object instance = constructor.newInstance();
            if (!(instance instanceof Driver))
                throw new SQLException("Instance of " + className
                        + " does not implement " + Driver.class.getCanonicalName());

            return (Driver) instance;
        } catch (ReflectiveOperationException exception) {
            throw new SQLException("Failed to create new instance from constructor", exception);
        }
    }

}
