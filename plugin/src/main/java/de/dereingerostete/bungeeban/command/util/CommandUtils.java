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
import de.dereingerostete.bungeeban.util.UUIDFetcher;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CommandUtils {

    @Nullable
    public static UUID getUUIDByName(String[] args, int index, CommandSender sender, boolean allowSelf) {
        String enteredName = args[index];
        return getUUIDByName(enteredName, sender, allowSelf);
    }

    @Nullable
    public static UUID getUUIDByName(String playerName, CommandSender sender, boolean allowSelf) {
        if (!allowSelf && !(sender instanceof ProxiedPlayer) && sender.getName().equalsIgnoreCase(playerName)) {
            sendMessage(sender, "command.noSelfSelection");
            return null;
        }

        UUID uuid = UUIDFetcher.getUUID(playerName);
        if (uuid == null) {
            sendMessage(sender, "command.unknownPlayer");
            return null;
        }

        if (!allowSelf && sender instanceof ProxiedPlayer
                && ((ProxiedPlayer) sender).getUniqueId().equals(uuid)) {
            sendMessage(sender, "command.noSelfSelection");
            return null;
        } else return uuid;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean canPunish(@NotNull Object sender, @NotNull Object target) {
        String targetPermission = "bungeeban.punish";
        boolean targetCanPunish = target instanceof CommandSender ?
                ((CommandSender) sender).hasPermission(targetPermission) :
                target instanceof PermissionEntity && ((PermissionEntity) target).hasPermission(targetPermission);
        if (!targetCanPunish) return true;

        String senderPermission = "bungeeban.punish.bypass";
        return sender instanceof CommandSender ? ((CommandSender) sender).hasPermission(senderPermission) :
                sender instanceof PermissionEntity && ((PermissionEntity) sender).hasPermission(senderPermission);
    }

    /**
     * Gets the uuid of the command sender
     * <b>Note:</b> If the sender is the console the uuid will be all zeros
     * @param sender The sender which uuid should be resolved
     * @return The uuid of the sender
     */
    @NotNull
    public static UUID getUUID(@NotNull CommandSender sender) {
        if (sender instanceof ProxiedPlayer) return ((ProxiedPlayer) sender).getUniqueId();
        else return new UUID(0, 0);
    }

    /**
     * Loads permission entity from an online or offline player
     * <b>Warning: If the player is offline the server will look up the player
     * which will cause the server to lag if not handled asynchronous</b>
     * @param uuid The uuid of the player to look up
     * @return The permission entity
     */
    @NotNull
    public static PermissionEntity loadPermissionEntity(@NotNull UUID uuid) {
        ProxyServer server = ProxyServer.getInstance();
        ProxiedPlayer proxiedPlayer = server.getPlayer(uuid);
        if (proxiedPlayer != null) return proxiedPlayer::hasPermission;

        LuckPerms perms = LuckPermsProvider.get();
        UserManager manager = perms.getUserManager();
        User user = manager.loadUser(uuid).join();
        return permission -> user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public static void sendMessage(CommandSender sender, String key, Object... parameters) {
        String message = Lang.getMessage(key, true, parameters);
        Chat.toPlayer(sender, message);
    }

    public interface PermissionEntity {
        boolean hasPermission(@NotNull String permission);
    }

}
