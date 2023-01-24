/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban;

import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.chatlog.ChatLogger;
import de.dereingerostete.bungeeban.command.*;
import de.dereingerostete.bungeeban.command.util.SimpleCommand;
import de.dereingerostete.bungeeban.database.BanDatabase;
import de.dereingerostete.bungeeban.database.BungeeBanDatabase;
import de.dereingerostete.bungeeban.database.DatabaseType;
import de.dereingerostete.bungeeban.entity.Cache;
import de.dereingerostete.bungeeban.entity.Reasons;
import de.dereingerostete.bungeeban.listener.ChatListener;
import de.dereingerostete.bungeeban.listener.ConnectListener;
import de.dereingerostete.bungeeban.util.Config;
import de.dereingerostete.bungeeban.util.Configs;
import de.dereingerostete.bungeeban.util.Lang;
import de.dereingerostete.bungeeban.util.UUIDFetcher;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.logging.Handler;

public class BungeeBanPlugin extends Plugin {
    protected static @Getter Plugin instance;
    protected static @Getter BanDatabase database;
    protected static @Getter Reasons reasons;
    protected static @Getter Cache cache;
    protected static @Getter ChatLogger chatLogger;
    protected boolean pluginEnabled;

    @Override
    public void onEnable() {
        try {
            instance = this;
            pluginEnabled = true;
            Logging.info("Initializing " + getName() + "...");

            loadConfigs();
            loadLanguage();
            loadCache();
            loadDatabase();
            loadReasons();
            registerListeners();
            registerCommands();

            chatLogger = new ChatLogger();
            BungeeBan.setBanAPI(new BungeeBanAPI(database, reasons, cache));
            UUIDFetcher.startTask(this);

            Logging.info("Enabled " + getName());
        } catch (Throwable throwable) {
            Logging.warning("An unexpected error occurred while loading " + getName(), throwable);
            disableSelf();
        }
    }

    protected void disableSelf() {
        onDisable();
        for (Handler handler : getLogger().getHandlers()) handler.close();
        getProxy().getScheduler().cancel(this);

        PluginManager manager = getProxy().getPluginManager();
        manager.unregisterListeners(this);
        manager.unregisterCommands(this);

        //noinspection deprecation
        getExecutorService().shutdownNow();
        pluginEnabled = false;
    }

    protected void registerCommands() {
        SimpleCommand.register(this, new BanCommand());
        SimpleCommand.register(this, new MuteCommand());
        SimpleCommand.register(this, new ReasonsCommand());
        SimpleCommand.register(this, new UnbanCommand());
        SimpleCommand.register(this, new UnmuteCommand());
        SimpleCommand.register(this, new HistoryCommand());
        SimpleCommand.register(this, new ChatLogCommand());
    }

    protected void registerListeners() {
        registerListener(new ConnectListener());
        registerListener(new ChatListener());
    }

    protected void registerListener(@NotNull Listener listener) {
        PluginManager manager = getProxy().getPluginManager();
        manager.registerListener(this, listener);
    }

    protected void loadReasons() {
        reasons = new Reasons();
        try {
            loadIfNotPresent("reasons/banReasons.json");
            loadIfNotPresent("reasons/muteReasons.json");
            reasons.loadReasons("reasons/banReasons.json", Reasons.Type.BAN);
            reasons.loadReasons("reasons/muteReasons.json", Reasons.Type.MUTE);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load reasons");
        }
    }

    protected void loadDatabase() {
        Config defaultConfig = Configs.getDefaultConfig();
        String typeName = defaultConfig.getString("database");

        DatabaseType type = DatabaseType.byName(typeName);
        if (type == null) throw new IllegalStateException("No valid database was selected");

        database = new BungeeBanDatabase(type);
        try {
            database.connect();
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not connect to database (BanDatabase)", exception);
        }
    }

    private void loadConfigs() {
        try {
            Configs.loadConfigs();
            boolean debug = Configs.getDefaultConfig().getBoolean("debug", false);
            Logging.setDebug(debug);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load configs", exception);
        }
    }

    protected void loadLanguage() {
        try {
            loadIfNotPresent("lang/de.json");
            loadIfNotPresent("lang/en.json");
            Lang.loadLanguage();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load language file", exception);
        }
    }

    protected void loadCache() {
        Config config = Configs.getDefaultConfig();
        long cacheDuration = config.getLong("cacheDuration", 1800000L);
        cache = new Cache(this, cacheDuration);
    }

    protected void loadIfNotPresent(String fileName) throws IOException {
        Plugin plugin = BungeeBanPlugin.getInstance();
        File folder = plugin.getDataFolder();
        File file = new File(folder, fileName);

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) throw new IllegalStateException("Could not find file: " + fileName);

        FileUtils.copyToFile(inputStream, file);
        inputStream.close();
    }

    @Override
    public void onDisable() {
        if (!pluginEnabled) return;
        pluginEnabled = false;
        if (cache != null) {
            Logging.info("Flushing cache");
            cache.close();
        }

        if (database != null) {
            try {
                Logging.info("Disconnecting from database");
                database.disconnect();
            } catch (SQLException exception) {
                Logging.warning("Failed to disconnect from database");
            }
        }

        String disableMessage = "| " + getName() + " got disabled |";
        int repeatChars = disableMessage.length() - 2;
        String line = "|" + StringUtils.repeat('=', repeatChars) + "|";
        String emptySpace = "|" + StringUtils.repeat(' ', repeatChars) + "|";

        Logging.warning(line);
        Logging.warning(emptySpace);
        Logging.warning(disableMessage);
        Logging.warning(emptySpace);
        Logging.warning(line);
    }

    protected String getName() {
        return getDescription().getName();
    }

}
