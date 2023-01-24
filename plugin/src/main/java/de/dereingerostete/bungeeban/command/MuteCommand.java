/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.command;

import de.dereingerostete.bungeeban.BungeeBan;
import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.command.util.CommandUtils;
import de.dereingerostete.bungeeban.command.util.SimpleCommand;
import de.dereingerostete.bungeeban.entity.mute.MuteReason;
import de.dereingerostete.bungeeban.entity.player.BanPlayer;
import de.dereingerostete.bungeeban.exception.BanException;
import de.dereingerostete.bungeeban.util.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;

import java.util.UUID;

public class MuteCommand extends SimpleCommand {

    public MuteCommand() {
        super("mute", "bungeeban.mute", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args, int arguments) {
        if (arguments < 2) {
            sendMessage(sender, "command.mute.usage"); //TODO: Add /banreasons or /reasons <Ban|Mute> command
            return;
        }

        String enteredId = args[1];
        MuteReason reason;
        try {
            int id = Integer.parseInt(enteredId);
            if (id < 0) throw new NumberFormatException();

            reason = BungeeBan.getMuteReasonById(id);
            if (reason == null) {
                sendMessage(sender, "command.mute.unknownId");
                return;
            }
        } catch (NumberFormatException exception) {
            sendMessage(sender, "command.mute.invalidId");
            return;
        }

        UUID targetUUID = CommandUtils.getUUIDByName(args, 0, sender, false);
        if (targetUUID == null) return;
        if (!CommandUtils.canPunish(sender, CommandUtils.loadPermissionEntity(targetUUID))) {
            sendMessage(sender, "command.cannotPunish");
            return;
        }

        try {
            BanPlayer banPlayer = BungeeBan.getPlayer(targetUUID, true);
            if (banPlayer == null) banPlayer = BungeeBan.createPlayer(targetUUID);

            if (!banPlayer.isMuted()) {
                banPlayer.mutePlayer(reason, CommandUtils.getUUID(sender));

                String name = UUIDFetcher.getName(targetUUID);
                sendMessage(sender, "command.mute.success", "name", name);
                Logging.info("Player " + sender.getName() + " muted "
                        + name + '/' + targetUUID + " with the reason " + reason.getName());
            } else sendMessage(sender, "command.mute.alreadyMuted");
        } catch (BanException exception) {
            Logging.warning("Player " + sender.getName() + " failed to mute player " + targetUUID, exception);
            sendMessage(sender, "command.unknownError");
        }
    }

}