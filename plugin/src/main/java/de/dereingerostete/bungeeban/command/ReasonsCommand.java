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
import de.dereingerostete.bungeeban.command.util.SimpleCommand;
import de.dereingerostete.bungeeban.entity.Reason;
import de.dereingerostete.bungeeban.util.Lang;
import de.dereingerostete.bungeeban.util.TimeUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReasonsCommand extends SimpleCommand {

    public ReasonsCommand() {
        super("reasons", "bungeeban.reasons", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args, int arguments) {
        if (arguments == 0) {
            sendMessage(sender, "command.reasons.usage");
            return;
        }

        if (arguments == 1) listReasons(sender, args);
        else showReasonInfo(sender, args);
    }

    protected void showReasonInfo(CommandSender sender, String[] args) {
        int id;
        try {
            id = Integer.parseInt(args[1]);
        } catch (NumberFormatException exception) {
            sendMessage(sender, "command.reasons.invalidId");
            return;
        }

        String type = args[0].toLowerCase();
        Reason reason;
        switch (type) {
            case "ban":
                reason = BungeeBan.getBanReasonById(id);
                break;
            case "mute":
                reason = BungeeBan.getMuteReasonById(id);
                break;
            default:
                sendMessage(sender, "command.reasons.unknownReason");
                return;
        }

        if (reason == null) {
            sendMessage(sender, "command.reasons.unknownId");
            return;
        }

        String[] layout = Lang.getLayout("command.reasons.infoMessage", false,
                "id", reason.getId(),
                "name", reason.getDisplayName(),
                "description", reason.getDescription());
        String durationMessage = Lang.getMessage("command.reasons.infoMessageDuration", false);

        long[] durations = reason.getDurations();
        String[] formattedDurations = new String[durations.length];
        for (int i = 0; i < durations.length; i++)
            formattedDurations[i] = Lang.replace(durationMessage, "duration",
                    TimeUtils.format(durations[i]), "timesBanned", i + ".");

        TextComponent component = new TextComponent("§8" + StringUtils.repeat('=', 16));
        sender.sendMessage(component);
        for (String line : layout) sender.sendMessage(new TextComponent(line));
        if (formattedDurations.length == 0) Chat.toPlayer(sender, "DEBUG: No durations found");
        for (String line : formattedDurations) sender.sendMessage(new TextComponent(line));
        Chat.toPlayer(sender, component);
    }

    protected void listReasons(CommandSender sender, String[] args) {
        String type = args[0].toLowerCase();
        List<? extends Reason> reasons;
        switch (type) {
            case "ban":
                reasons = BungeeBan.getBanReasons();
                break;
            case "mute":
                reasons = BungeeBan.getMuteReasons();
                break;
            default:
                sendMessage(sender, "command.reasons.unknownReason");
                return;
        }

        String message = Lang.getMessage("command.reasons.listMessage", true);
        String hoverMessage = Lang.getMessage("command.reasons.hoverMessage", false);

        BaseComponent[] lines = new BaseComponent[reasons.size()];
        for (int i = 0; i < lines.length; i++) {
            Reason reason = reasons.get(i);
            String formatted = message.replaceAll("%id%", String.valueOf(reason.getId()))
                    .replaceAll("%name%", reason.getDisplayName());
            TextComponent component = new TextComponent(formatted);

            Text text = new Text(hoverMessage.replace("%description%", reason.getDescription()));
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
            component.setHoverEvent(hoverEvent);

            String command = '/' + getName() + ' ' + type + ' ' + reason.getId();
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command);
            component.setClickEvent(clickEvent);
            lines[i] = component;
        }

        String header = "§8" + StringUtils.repeat('=', 16);
        TextComponent headerComponent = new TextComponent(header);

        Chat.toPlayer(sender, headerComponent);
        for (BaseComponent component : lines) Chat.toPlayer(sender, component);
        Chat.toPlayer(sender, headerComponent);
    }

    @NotNull
    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args, int arguments) {
        if (arguments == 1) return Arrays.asList("ban", "mute");
        else return new ArrayList<>(0);
    }

}