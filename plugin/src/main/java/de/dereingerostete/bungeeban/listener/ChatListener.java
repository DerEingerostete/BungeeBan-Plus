/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.listener;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.dereingerostete.bungeeban.BungeeBan;
import de.dereingerostete.bungeeban.BungeeBanPlugin;
import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.database.BanDatabase;
import de.dereingerostete.bungeeban.entity.mute.chatlog.Message;
import de.dereingerostete.bungeeban.entity.mute.Mute;
import de.dereingerostete.bungeeban.entity.player.BanPlayer;
import de.dereingerostete.bungeeban.util.Config;
import de.dereingerostete.bungeeban.util.Configs;
import de.dereingerostete.bungeeban.util.MessageUtils;
import de.dereingerostete.bungeeban.util.SchedulerUtils;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChatListener implements Listener {
    protected final List<String> mutedCommands;
    protected final BanDatabase database;

    public ChatListener() {
        database = BungeeBanPlugin.getDatabase();

        Config config = Configs.getChatLogConfig();
        JSONArray array = config.getJSONArray("mutedCommands");
        Preconditions.checkNotNull(array, "Array is not defined in ChatLog config");

        mutedCommands = Lists.newArrayList();
        array.forEach(object -> mutedCommands.add(String.valueOf(object)));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMessage(ChatEvent event) {
        Connection sender = event.getSender();
        if (event.isCancelled() || !(sender instanceof ProxiedPlayer)) return;

        ProxiedPlayer player = (ProxiedPlayer) sender;
        UUID uuid = player.getUniqueId();

        BanPlayer banPlayer = BungeeBan.getPlayer(uuid, false);
        if (banPlayer == null) {
            Logging.warning("Message author not found in cache: "
                    + player.getName() + '/' + player.getUniqueId());
            return;
        }

        String message = event.getMessage();
        if (event.isCommand()) {
            String command = message.split(" ")[0];
            command = command.substring(1);
            if (!mutedCommands.contains(command)) return;
        }

        if (banPlayer.isMuted()) cancelEvent(event, banPlayer);
        SchedulerUtils.runAsync(() -> {
            try {
                String server = player.getServer().getInfo().getName();
                Message chatMessage = new Message(uuid, message, server, System.currentTimeMillis());
                database.addMessage(chatMessage);
            } catch (SQLException exception) {
                Logging.warning("Failed to append message to ChatLog: "
                        + message + " (by " + uuid + ')', exception);
            }
        });
    }

    protected void cancelEvent(@NotNull Cancellable cancellable,
                               @NotNull BanPlayer player) {
        cancellable.setCancelled(true);
        Mute mute = Objects.requireNonNull(player.getMute());
        player.sendMessage(MessageUtils.formatMute(mute));
    }

}
