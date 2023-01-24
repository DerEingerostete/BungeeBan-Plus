/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.database;

import de.dereingerostete.bungeeban.BungeeBanPlugin;
import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.util.Config;
import de.dereingerostete.bungeeban.util.Configs;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.File;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public abstract class SQLDatabase implements Database {
    protected final DatabaseType type;
    protected String currentAddress;
    protected Connection connection;
    protected ScheduledTask task;

    public SQLDatabase(DatabaseType type) {
        this.type = type;
    }

    @Override
    public void connect() throws SQLException {
        if (isConnected()) return;
        Config config = Configs.getDatabaseConfig();

        switch (type) {
            case MYSQL:
                JSONObject sqlObject = config.getJSONObject("mysql");
                if (sqlObject == null) throw new IllegalStateException("Database config is missing MySQL object");

                int port = sqlObject.optInt("port");
                String hostname = sqlObject.optString("hostname");
                String database = sqlObject.optString("database");
                String username = sqlObject.optString("username");
                String password = sqlObject.optString("password");

                currentAddress = hostname + ":" + port;
                connection = DriverManager.getConnection("jdbc:" + type.getName() + "://"
                                + currentAddress + "/" + database + "?autoReconnect=true",
                        username, password);
                break;
            case SQLITE:
                sqlObject = config.getJSONObject("sqlite");
                if (sqlObject == null) throw new IllegalStateException("Database config is missing SQLite object");

                String filePath = sqlObject.optString("path", null);
                if (filePath == null) throw new IllegalStateException("No file path was defined in SQLite object");

                File file = BungeeBanPlugin.getInstance().getDataFolder();
                currentAddress = file.getPath() + "/" + filePath;

                String url = "jdbc:" + type.getName() + ":" + currentAddress;
                Driver driver = DriverUtils.loadDriver(url, type.getDriverClass());
                connection = driver.connect(url, new Properties());
                break;
            default: throw new IllegalStateException("Unknown database type");
        }

        startTimer();
        onConnected();
    }

    protected void startTimer() {
        Config config = Configs.getDatabaseConfig();
        long delay = config.getLong("uptimeCheck", 900000L);

        TaskScheduler scheduler = ProxyServer.getInstance().getScheduler();
        task = scheduler.schedule(BungeeBanPlugin.getInstance(), () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("/* ping */ SELECT 1;");
                statement.executeQuery().close();
            } catch (SQLException exception) {
                Logging.warning("Database connection timed out");
                Logging.debug(exception);
            }
        }, delay, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Is called after a database connection has been established
     * @throws SQLException If a database access error occurs
     */
    public abstract void onConnected() throws SQLException;

    @Override
    public boolean isConnected() {
        try {
            if (connection == null || connection.isClosed()) return false;
            PreparedStatement statement = connection.prepareStatement("/* ping */ SELECT 1;");
            statement.executeQuery().close();
            return true;
        } catch (SQLException exception) {
            return false;
        }
    }

    @Override
    public void disconnect() throws SQLException {
        if (!isConnected()) return;
        if (task != null) task.cancel();
        connection.close();
        currentAddress = null;
    }

    @NotNull
    @Override
    public DatabaseType getType() {
        return type;
    }

    @Nullable
    @Override
    public String getAddress() {
        return currentAddress;
    }

    protected ResultSet query(@NotNull String sql, Object... objects) throws SQLException {
        return prepare(sql, objects).executeQuery();
    }

    protected int update(@NotNull String sql, Object... objects) throws SQLException {
        PreparedStatement statement = prepare(sql, objects);
        int update = statement.executeUpdate();
        statement.close();
        return update;
    }

    protected PreparedStatement prepare(@NotNull String sql, Object... objects) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < objects.length; i++) statement.setObject(i + 1, objects[i]);
        return statement;
    }

}
