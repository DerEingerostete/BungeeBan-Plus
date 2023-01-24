/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.database;

import de.dereingerostete.bungeeban.entity.Punishment;
import de.dereingerostete.bungeeban.entity.ban.Ban;
import de.dereingerostete.bungeeban.entity.mute.chatlog.ChatLog;
import de.dereingerostete.bungeeban.entity.mute.chatlog.Message;
import de.dereingerostete.bungeeban.entity.mute.chatlog.TextUpload;
import de.dereingerostete.bungeeban.entity.player.BanPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface BanDatabase extends Database {

    /**
     * Gets a player by the uuid from the database
     * @param uuid The uuid of the database
     * @return The BanPlayer from the database or null if no player exists with the specified uuid
     * @throws SQLException If a database access error occurs
     */
    @Nullable
    BanPlayer getPlayer(@NotNull UUID uuid) throws SQLException;

    /**
     * Gets a list of bans by the ip hash
     * @param ipHash The ip hash to look for
     * @return A list of bans with the ip hash
     * @throws SQLException If a database access error occurs
     */
    @NotNull
    List<Ban> getBansByIP(@NotNull String ipHash) throws SQLException;

    /**
     * Creates a new player with the specified uuid
     * @param uuid The uuid of the player to be created
     * @return The newly created player
     */
    @NotNull
    BanPlayer createPlayer(@NotNull UUID uuid) throws SQLException;

    /**
     * Updates the player in the database
     * @param player The player to update
     * @throws SQLException If a database access error occurs
     * @deprecated This method is very time-consuming and can be
     * dangerous, as all the player's data is deleted from the database beforehand.
     */
    @Deprecated
    void updatePlayer(@NotNull BanPlayer player) throws SQLException;

    /**
     * Adds a punishment for a player to the database
     * @param punishment The punishment to add
     * @throws SQLException If a database access error occurs
     */
    void addPunishment(@NotNull Punishment punishment) throws SQLException;

    /**
     * Adds a punishment for the player's history to the database
     * @param punishment The punishment to add
     * @throws SQLException If a database access error occurs
     */
    void addPunishmentHistory(@NotNull Punishment punishment) throws SQLException;

    /**
     * Updates the activity of the player in the database
     * @param player The player to update
     * @throws SQLException If a database access error occurs
     */
    void updatePlayerActivity(@NotNull BanPlayer player) throws SQLException;

    /**
     * Removes a punishment from the database identified by its id
     * @param id The id of the punishment
     * @throws SQLException If a database access error occurs
     */
    void removePunishment(@NotNull String id) throws SQLException;

    /**
     * Removes a punishment from the history database identified by its id
     * @param id The id of the punishment
     * @throws SQLException If a database access error occurs
     */
    void removeHistory(@NotNull String id) throws SQLException;

    /**
     * Removes the history of a player from database
     * @param uuid The uuid of the player
     * @throws SQLException If a database access error occurs
     */
    void removeHistory(@NotNull UUID uuid) throws SQLException;

    /**
     * Removes a player from the database
     * @param uuid The uuid of the player
     * @return True if the player was removed; Fasle if otherwise
     * @throws SQLException If a database access error occurs
     */
    boolean removePlayer(@NotNull UUID uuid) throws SQLException;

    /**
     * Gets all messages by the uuid of a player
     * @param uuid The uuid of the player
     * @return A list of all recent messages
     * @throws SQLException If a database access error occurs
     */
    @NotNull
    List<Message> getMessages(@NotNull UUID uuid) throws SQLException;

    /**
     * Clears all messages by the uuid of a player
     * @param uuid The uuid of the player whose messages are to be removed
     * @throws SQLException If a database access error occurs
     */
    void clearMessages(@NotNull UUID uuid) throws SQLException;

    /**
     * Adds a message to the database
     * @param message The message to add
     * @throws SQLException If a database access error occurs
     */
    void addMessage(@NotNull Message message) throws SQLException;

    /**
     * Adds a chatlog to the database
     * @param uuid The uuid of the player
     * @throws SQLException If a database access error occurs
     */
    void addChatLog(@NotNull UUID uuid, @NotNull ChatLog chatLog) throws SQLException;

    /**
     * Gets all chat-logs by the uuid of a player
     * @param uuid The uuid of the player
     * @param upload The text uploader used to create links
     * @return A list of all chat-logs
     * @throws SQLException If a database access error occurs
     */
    @NotNull
    List<ChatLog> getChatLogs(@NotNull UUID uuid, @NotNull TextUpload upload) throws SQLException;

    /**
     * Gets a chatlog from the database identified by its id
     * @param id The id of the chatlog
     * @param upload The text uploader used to create links
     * @return The chatlog identified by the id
     * @throws SQLException If a database access error occurs
     */
    @Nullable
    ChatLog getChatLog(@NotNull String id, @NotNull TextUpload upload) throws SQLException;

}
