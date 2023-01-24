/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.command;

import de.dereingerostete.bungeeban.BungeeBan;
import de.dereingerostete.bungeeban.chat.Chat;
import de.dereingerostete.bungeeban.command.util.CommandUtils;
import de.dereingerostete.bungeeban.command.util.SimpleCommand;
import de.dereingerostete.bungeeban.entity.Punishment;
import de.dereingerostete.bungeeban.entity.ban.Ban;
import de.dereingerostete.bungeeban.entity.mute.Mute;
import de.dereingerostete.bungeeban.entity.player.BanPlayer;
import de.dereingerostete.bungeeban.entity.player.PlayerHistory;
import de.dereingerostete.bungeeban.util.Lang;
import de.dereingerostete.bungeeban.util.TimeUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class HistoryCommand extends SimpleCommand {

    public HistoryCommand() {
        super("history", "bungeeban.history", true);
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args, int arguments) {
        if (arguments == 0) {
            sendMessage(sender, "command.history.usage");
            return;
        }

        UUID targetUUID = CommandUtils.getUUIDByName(args, 0, sender, true);
        if (targetUUID == null) return;

        BanPlayer banPlayer = BungeeBan.getPlayer(targetUUID, true);
        if (banPlayer == null) {
            sendMessage(sender, "command.history.notSaved");
            return;
        }

        //Show base history if no other arguments specified
        if (arguments == 1) {
            showHistory(sender, banPlayer, args[0]);
            return;
        }

        String infoType = args[1].toLowerCase();
        switch (infoType) {
            case "activities":
                showActivities(sender, banPlayer);
                break;
            case "mutes":
                showPunishment(sender, banPlayer, Mute.class);
                break;
            case "bans":
                showPunishment(sender, banPlayer, Ban.class);
                break;
            default: sendMessage(sender, "command.history.unknownType");
        }
    } //TODO: Add BanInfo / MuteInfo Command and ChatLog Command!

    private void showPunishment(CommandSender sender, BanPlayer banPlayer, Class<? extends Punishment> typeClass) {
        String[] header = Lang.getLayout("command.history.punishment.header", false);
        String[] footer = Lang.getLayout("command.history.punishment.footer", false);
        String entry = Lang.getMessage("command.history.punishment.entry", false);

        PlayerHistory history = banPlayer.getHistory();
        List<? extends Punishment> punishments = history.getPunishmentByType(typeClass);

        for (String line : header) Chat.toPlayer(sender, line);
        punishments.forEach(punishment -> {
            int reason = punishment.getReason().getId();
            String date = TimeUtils.formatDate(punishment.getTimeOfPunishment());
            String punisher = punishment.getPunisherName();

            Chat.toPlayer(sender, Lang.replace(entry,
                    "reason", reason,
                    "time", date,
                    "punisher", punisher));
        });
        for (String line : footer) Chat.toPlayer(sender, line);
    }

    private void showActivities(@NotNull CommandSender sender, @NotNull BanPlayer banPlayer) {
        String[] header = Lang.getLayout("command.history.activities.header", false);
        String[] footer = Lang.getLayout("command.history.activities.footer", false);
        String entry = Lang.getMessage("command.history.activities.entry", false);

        String name = banPlayer.getLastServer() != null ? banPlayer.getLastServer() : "None";
        String lastServer = Lang.getMessage("command.history.activities.lastServer",
                false, "name", name);

        for (String line : header) Chat.toPlayer(sender, line);
        Chat.toPlayer(sender, lastServer);
        banPlayer.getActivity().forEach((serverName, millis) -> {
            String message = Lang.replace(entry, "name", serverName, "time", TimeUtils.format(millis));
            Chat.toPlayer(sender, message);
        });
        for (String line : footer) Chat.toPlayer(sender, line);
    }

    private void showHistory(@NotNull CommandSender sender,
                             @NotNull BanPlayer banPlayer, @NotNull String playerName) {
        String parametersKey = "command.history.generalParameters.";
        String yes = Lang.getMessage(parametersKey + "yes", false);
        String no = Lang.getMessage(parametersKey + "no", false);
        String online = Lang.getMessage(parametersKey + "online", false);
        String offline = Lang.getMessage(parametersKey + "offline", false);

        String banned = banPlayer.isBanned() ? yes : no;
        String muted = banPlayer.isMuted() ? yes : no;
        String activity = banPlayer.isOnline() ? online : offline;
        int bans = banPlayer.getTotalBans();
        int mutes = banPlayer.getTotalMutes();

        String[] lines = Lang.getPlainLayout("command.history.general", false);
        BaseComponent[] components = new BaseComponent[lines.length];
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            BaseComponent component = createComponent(line, "%banned%", banned, playerName, "bans");
            if (component != null) {
                components[i] = component;
                continue;
            }

            component = createComponent(line, "%muted%", muted, playerName, "mutes");
            if (component != null) {
                components[i] = component;
                continue;
            }

            component = createComponent(line, "%activity%", activity, playerName, "activities");
            if (component != null) {
                components[i] = component;
                continue;
            }

            component = createComponent(line, "%timesBanned%", bans, playerName, "bans");
            if (component != null) {
                components[i] = component;
                continue;
            }

            component = createComponent(line, "%timesMuted%", mutes, playerName, "mutes");
            if (component != null) components[i] = component;
            else components[i] = new TextComponent(line);
        }

        for (BaseComponent component : components)
            Chat.toPlayer(sender, component);
    }

    @Nullable
    private BaseComponent createComponent(@NotNull String string, @NotNull String parameter, @NotNull Object value,
                                          @NotNull String playerName, @NotNull String type) {
        int index = string.indexOf(parameter);
        if (index == -1) return null;

        String before = string.substring(0, index);
        String after = string.substring(index + parameter.length());
        BaseComponent component = new TextComponent(before + value + after);

        String command = '/' + getName() + ' ' + playerName + ' ' + type;
        ClickEvent event = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
        component.setClickEvent(event);
        return component;
    }

    @NotNull
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args, int arguments) {
        if (arguments == 2) return Arrays.asList("mutes", "bans", "activities");
        else return new ArrayList<>(0);
    }

}
