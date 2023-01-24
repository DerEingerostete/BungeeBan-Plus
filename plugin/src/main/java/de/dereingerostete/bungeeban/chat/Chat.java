/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.chat;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class Chat {

    public static void toPlayer(@NotNull CommandSender sender, @NotNull String message) {
        BaseComponent component = new TextComponent(message);
        sender.sendMessage(component);
    }

    public static void toPlayer(@NotNull CommandSender sender, @NotNull BaseComponent component) {
        sender.sendMessage(component);
    }

    public static void toConsole(@NotNull String message) {
        CommandSender sender = ProxyServer.getInstance().getConsole();
        toPlayer(sender, message);
    }

    public static void broadcast(@NotNull String message) {
        Consumer<ProxiedPlayer> consumer = player -> toPlayer(player, message);
        ProxyServer.getInstance().getPlayers().forEach(consumer);
    }

    public static String stretchColorCode(@NotNull String message, @NotNull String colorCode) {
        StringBuilder builder = new StringBuilder();
        String[] strings = message.split(" ");
        for (String string : strings)
            builder.append(colorCode)
                .append(string)
                .append(' ');
        return builder.toString();
    }

    public static String stripColorCodes(@NotNull String string) {
        String[] colorCodes = {"§1", "§2", "§3",
                "§4", "§5", "§6",
                "§7", "§8", "§9",
                "§a", "§b", "§c",
                "§d", "§e", "§f",
                "§k", "§m", "§n",
                "§o", "§r"};
        String replaced = string;
        for (String colorCode : colorCodes)
            replaced = replaced.replaceAll(colorCode, "");
        return replaced;
    }

}
