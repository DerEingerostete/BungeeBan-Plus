/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.command;

import de.dereingerostete.bungeeban.BungeeBanPlugin;
import de.dereingerostete.bungeeban.chat.Chat;
import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.chatlog.ChatLogger;
import de.dereingerostete.bungeeban.command.util.CommandUtils;
import de.dereingerostete.bungeeban.command.util.SimpleCommand;
import de.dereingerostete.bungeeban.database.BanDatabase;
import de.dereingerostete.bungeeban.entity.mute.chatlog.ChatLog;
import de.dereingerostete.bungeeban.entity.mute.chatlog.TextUpload;
import de.dereingerostete.bungeeban.util.Lang;
import de.dereingerostete.bungeeban.util.TimeUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class ChatLogCommand extends SimpleCommand {

    public ChatLogCommand() {
        super("chatlog", "bungeeban.chatlog", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args, int arguments) {
        if (arguments < 2) {
            sendMessage(sender, "command.chatlog.usage");
            return;
        }

        String action = args[0].toLowerCase();
        switch (action) {
            case "view":
                viewChatLogById(sender, args[1]);
                break;
            case "player":
                viewChatLogByUUID(sender, args[1]);
                break;
            case "create":
                createChatLog(sender, args[1]);
                break;
            default: sendMessage(sender, "command.chatlog.usage");
        }
    }

    protected void viewChatLogById(@NotNull CommandSender sender, @NotNull String id) {
        ChatLog chatLog;
        try {
            TextUpload upload = BungeeBanPlugin.getChatLogger().getUpload();
            BanDatabase database = BungeeBanPlugin.getDatabase();
            chatLog = database.getChatLog(id, upload);

            if (chatLog == null) {
                sendMessage(sender, "command.chatlog.unknownId");
                return;
            }
        } catch (SQLException exception) {
            Logging.warning("Failed to load chatlog by id", exception);
            sendMessage(sender, "command.unknownError");
            return;
        }

        try {
            ChatLog.Link link = chatLog.generateLink();
            String url = link.getURL();
            String duration = TimeUtils.format(link.getLifetime());

            String[] layout = Lang.getLayout("command.chatlog.created", true, "duration", duration);
            TextComponent[] components = new TextComponent[layout.length];
            for (int i = 0; i < layout.length; i++) {
                String line = layout[i];
                if (line.contains("%url%")) {
                    line = line.replace("%url%", url);
                    TextComponent component = new TextComponent(line);
                    ClickEvent event = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
                    component.setClickEvent(event);
                    components[i] = component;
                } else components[i] = new TextComponent(line);
            }

            for (TextComponent component : components) Chat.toPlayer(sender, component);
        } catch (IOException exception) {
            Logging.warning("Failed to create link from chatlog", exception);
            sendMessage(sender, "command.unknownError");
        }
    }

    protected void viewChatLogByUUID(@NotNull CommandSender sender, @NotNull String playerName) {
        UUID targetUUID = CommandUtils.getUUIDByName(playerName, sender, true);
        if (targetUUID == null) return;

        List<ChatLog> chatLogs;
        try {
            TextUpload upload = BungeeBanPlugin.getChatLogger().getUpload();
            BanDatabase database = BungeeBanPlugin.getDatabase();
            chatLogs = database.getChatLogs(targetUUID, upload);
            chatLogs.sort(Comparator.comparingLong(ChatLog::getCreatedAt));
        } catch (SQLException exception) {
            Logging.warning("Failed to load chat-logs by uuid", exception);
            sendMessage(sender, "command.unknownError");
            return;
        }

        if (chatLogs.isEmpty()) {
            sendMessage(sender, "command.chatlog.playerHasNone");
            return;
        }

        String[] header = Lang.getLayout("command.chatlog.header", false);
        String[] footer = Lang.getLayout("command.chatlog.footer", false);
        String entry = Lang.getMessage("command.chatlog.entry", false);

        for (String line : header) Chat.toPlayer(sender, line);
        chatLogs.forEach(chatLog -> {
            String id = chatLog.getId();
            String date = TimeUtils.formatDate(chatLog.getCreatedAt());

            String message = Lang.replace(entry, "id", id, "time", date);
            TextComponent component = new TextComponent(message);

            String command = '/' + getName() + " view " + id;
            ClickEvent event = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
            component.setClickEvent(event);
            Chat.toPlayer(sender, component);
        });
        for (String line : footer) Chat.toPlayer(sender, line);
    }

    protected void createChatLog(@NotNull CommandSender sender, @NotNull String playerName) {
        UUID targetUUID = CommandUtils.getUUIDByName(playerName, sender, true);
        if (targetUUID == null) return;

        try {
            ChatLogger logger = BungeeBanPlugin.getChatLogger();
            logger.createChatLog(targetUUID);
            sendMessage(sender, "command.chatlog.success");
        } catch (IOException | SQLException | IllegalStateException exception) {
            String message = exception.getMessage();
            if (message != null && message.equals("The player has no cached messages")) {
                sendMessage(sender, "command.chatlog.noMessages");
                return;
            }

            Logging.warning("Failed to create chatlog of player " + targetUUID, exception);
            sendMessage(sender, "command.unknownError");
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(CommandSender sender, String[] args, int arguments) {
        if (arguments == 1) return Arrays.asList("view", "player", "create");
        else return new ArrayList<>(0);
    }

}
