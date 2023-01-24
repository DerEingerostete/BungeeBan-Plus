/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.command.util;

import de.dereingerostete.bungeeban.chat.Chat;
import de.dereingerostete.bungeeban.util.Lang;
import de.dereingerostete.bungeeban.util.SchedulerUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class SimpleCommand extends Command implements TabExecutor {
    protected final boolean async;

    public SimpleCommand(@NotNull String name, @Nullable String permission,
                         boolean async, @NotNull String... aliases) {
        super(name, permission, aliases);
        this.async = async;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Objects.requireNonNull(sender, "CommandSender is null");
        Objects.requireNonNull(args, "Args is null");
        Runnable runnable = () -> {
            if (sender instanceof ProxiedPlayer)
                execute((ProxiedPlayer) sender, args, args.length);
            else execute(sender, args, args.length);
        };

        if (async) SchedulerUtils.runAsync(runnable);
        else runnable.run();
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return onTabComplete(sender, args, args.length);
    }

    @NotNull
    public List<String> onTabComplete(CommandSender sender, String[] args, int arguments) {
        return new ArrayList<>(0);
    }

    public abstract void execute(CommandSender sender, String[] args, int arguments);

    public void execute(ProxiedPlayer player, String[] args, int arguments) {
        execute((CommandSender) player, args, arguments);
    }

    public void sendMessage(CommandSender sender, String key, Object... parameters) {
        String message = Lang.getMessage(key, true, parameters);
        Chat.toPlayer(sender, message);
    }

    public static void register(@NotNull Plugin plugin, @NotNull SimpleCommand command) {
        PluginManager manager = ProxyServer.getInstance().getPluginManager();
        manager.registerCommand(plugin, command);
    }

}