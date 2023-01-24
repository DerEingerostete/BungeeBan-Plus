/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.util;

import de.dereingerostete.bungeeban.BungeeBanPlugin;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Configs {
    public static Config defaultConfig;
    private static Config databaseConfig;
    private static Config chatLogConfig;

    public static void loadConfigs() throws IOException {
        Plugin plugin = BungeeBanPlugin.getInstance();
        File dataFolder = plugin.getDataFolder();

        defaultConfig = loadConfig("config.json", dataFolder);
        databaseConfig = loadConfig("database.json", dataFolder);
        chatLogConfig = loadConfig("chatlog.json", dataFolder);
    }

    public static Config getChatLogConfig() {
        return chatLogConfig;
    }

    public static Config getDefaultConfig() {
        return defaultConfig;
    }

    public static Config getDatabaseConfig() {
        return databaseConfig;
    }

    protected static Config loadConfig(String name, File directory) throws IOException {
        File file = new File(directory, name);
        return new Config(file);
    }

}
