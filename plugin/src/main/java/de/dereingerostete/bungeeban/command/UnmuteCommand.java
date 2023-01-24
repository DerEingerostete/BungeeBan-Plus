/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.command;

import com.google.common.collect.Lists;
import de.dereingerostete.bungeeban.BungeeBan;
import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.command.util.CommandUtils;
import de.dereingerostete.bungeeban.command.util.SimpleCommand;
import de.dereingerostete.bungeeban.entity.player.BanPlayer;
import de.dereingerostete.bungeeban.exception.BanException;
import de.dereingerostete.bungeeban.util.UUIDFetcher;
import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class UnmuteCommand extends SimpleCommand {

    public UnmuteCommand() {
        super("unmute", "bungeeban.unmute", true);
    }

    @Override
    public void execute(CommandSender sender, String[] args, int arguments) {
        if (arguments == 0) {
            sendMessage(sender, "command.unmute.usage");
            return;
        }

        UUID targetUUID = CommandUtils.getUUIDByName(args, 0, sender, false);
        if (targetUUID == null) return;

        try {
            BanPlayer banPlayer = BungeeBan.getPlayer(targetUUID, true);
            if (banPlayer == null) banPlayer = BungeeBan.createPlayer(targetUUID);

            if (banPlayer.isMuted()) {
                banPlayer.unmutePlayer();

                String name = UUIDFetcher.getName(targetUUID);
                sendMessage(sender, "command.unmute.success", "name", name);
                Logging.info("Player " + sender.getName() + " unmuted the player " + name + '/' + targetUUID);
            } else sendMessage(sender, "command.unmute.notMuted");
        } catch (BanException exception) {
            Logging.warning("Player " + sender.getName() + " failed to unmute player " + targetUUID, exception);
            sendMessage(sender, "command.unknownError");
        }
    }

}
