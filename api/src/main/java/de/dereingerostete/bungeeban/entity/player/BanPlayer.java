/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.player;

import de.dereingerostete.bungeeban.entity.Punishment;
import de.dereingerostete.bungeeban.entity.ban.Ban;
import de.dereingerostete.bungeeban.entity.ban.BanReason;
import de.dereingerostete.bungeeban.entity.mute.chatlog.ChatLog;
import de.dereingerostete.bungeeban.entity.mute.Mute;
import de.dereingerostete.bungeeban.entity.mute.MuteReason;
import de.dereingerostete.bungeeban.exception.BanException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public interface BanPlayer extends PlayerActions {

    /**
     * Gets the uniqueId to identify the player
     * @return The uuid of the player
     */
    @NotNull
    UUID getUniqueId();

    /**
     * Gets the hashed remote address of this player
     * If the player is offline and is not cached null will be returned
     * @return The hashed remote address
     */
    @Nullable
    String getAddress();

    /**
     * Gets whether the player is currently online
     * @return True if the player is online; False if otherwise
     */
    boolean isOnline();

    /**
     * Gets the history of the player
     * @return The player history
     */
    @NotNull
    PlayerHistory getHistory();

    /**
     * Gets the player's activity
     * The map is divided into server names
     * and the total length of time the player has spent on them
     * @return The activity map
     */
    @NotNull
    Map<String, Long> getActivity();

    /**
     * Gets the server the player was last connected to
     * @return The name of the server
     */
    @Nullable
    String getLastServer();

    /**
     * Gets whether the player is currently banned
     * @return The current ban state
     */
    boolean isBanned();

    /**
     * Gets whether the player is currently muted
     * @return The current mute state
     */
    boolean isMuted();

    /**
     * Gets the current ban of the player
     * @return The current ban
     */
    @Nullable
    Ban getBan();

    /**
     * Gets the current mute of the player
     * @return The current mute
     */
    @Nullable
    Mute getMute();

    /**
     * Gets the current amount of bans (including the active ban)
     * @return The amount of bans
     */
    int getTotalBans();

    /**
     * Gets the current amount of mutes (including the active mute)
     * @return The amount of mutes
     */
    int getTotalMutes();

    /**
     * Gets the current amount of punishments (including the active punishments)
     * @return The amount of punishments
     */
    int getTotalPunishments();

    /**
     * Punishes the player
     * @param punishment The punishment that will be applied
     * @throws IllegalArgumentException Is thrown if the punishment is no instance of {@link Ban} or {@link Mute}
     * @throws BanException If an unexpected error has occurred
     */
    default void punish(@NotNull Punishment punishment) throws BanException {
        if (punishment instanceof Ban) banPlayer((Ban) punishment);
        else if (punishment instanceof Mute) mutePlayer((Mute) punishment);
        else throw new IllegalArgumentException("Punishment must be an instance of Ban or Mute");
    }

    /**
     * Pardons the player of the punishment
     * @param punishment The punishment to pardon
     * @throws IllegalArgumentException Is thrown if the punishment is no instance of {@link Ban} or {@link Mute}
     * @throws BanException If an unexpected error has occurred
     */
    default void pardon(@NotNull Punishment punishment) throws BanException {
        if (punishment instanceof Ban) unbanPlayer();
        else if (punishment instanceof Mute) unmutePlayer();
        else throw new IllegalArgumentException("Punishment must be an instance of Ban or Mute");
    }

    /**
     * Bans the player
     * @param ban The ban
     * @throws BanException If an unexpected error has occurred
     */
    void banPlayer(@NotNull Ban ban) throws BanException;

    /**
     * Bans the player with the given reason
     * @param reason The reason
     * @param punisherUUID The uuid of the punisher
     * @throws BanException If an unexpected error occurred
     */
    void banPlayer(@NotNull BanReason reason, @NotNull UUID punisherUUID) throws BanException;

    /**
     * Removes the current ban of the player
     * @throws BanException If an unexpected error has occurred
     */
    void unbanPlayer() throws BanException;

    /**
     * Mutes the player
     * @param mute The mute
     * @throws BanException If an unexpected error has occurred
     */
    void mutePlayer(@NotNull Mute mute) throws BanException;

    /**
     * Mutes the player with the given reason
     * @param reason The reason
     * @param punisherUUID The uuid of the punisher
     * @throws BanException If an unexpected error occurred
     */
    void mutePlayer(@NotNull MuteReason reason, @NotNull UUID punisherUUID) throws BanException;

    /**
     * Removes the current mute of the player
     * @throws BanException If an unexpected error has occurred
     */
    void unmutePlayer() throws BanException;

    /**
     * Creates a ChatLog containing the last messages of the player
     * @return The created ChatLog
     * @throws IllegalStateException If the player has no cached messages
     * @throws SQLException If a database access error occurs
     * @throws IOException If a I/O error occurs
     */
    @NotNull
    ChatLog createChatLog() throws IllegalStateException, SQLException, IOException;

    /**
     * Refreshes the player
     */
    void refresh();

}
