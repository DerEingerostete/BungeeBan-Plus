/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban;

import de.dereingerostete.bungeeban.entity.Reason;
import de.dereingerostete.bungeeban.entity.Unsafe;
import de.dereingerostete.bungeeban.entity.ban.Ban;
import de.dereingerostete.bungeeban.entity.ban.BanReason;
import de.dereingerostete.bungeeban.entity.mute.MuteReason;
import de.dereingerostete.bungeeban.entity.player.BanPlayer;
import de.dereingerostete.bungeeban.event.EventHandler;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BungeeBan {
    protected static BanAPI banAPI;

    @ApiStatus.Internal
    public static void setBanAPI(BanAPI banAPI) {
        if (BungeeBan.banAPI != null) throw new IllegalStateException("The api was already set");
        BungeeBan.banAPI = banAPI;
    }

    /**
     * Gets the EventHandler used to register listeners
     * @return The active EventHandler
     */
    @NotNull
    public static EventHandler getEventHandler() {
        return banAPI.getEventHandler();
    }
    
    /**
     * Gets the interface that the api uses
     * @return The BanAPI interface
     */
    public static BanAPI getBanAPI() {
        return banAPI;
    }

    /**
     * Gets a player by its uuid
     * @param uuid The uuid of the player
     * @param lookup True if the player is to be looked up in the database;
     *               False if it is to be looked up in the cache only
     * @return The player or null if no player exists with the specified uuid
     */
    @Nullable
    public static BanPlayer getPlayer(UUID uuid, boolean lookup) {
        return banAPI.getPlayer(uuid, lookup);
    }

    /**
     * Creates a player with a given uuid
     * @param uuid The uuid of the player
     * @return The newly created player
     */
    @NotNull
    public static BanPlayer createPlayer(UUID uuid) {
        return banAPI.createPlayer(uuid);
    }

    /**
     * Gets a player by its uuid
     * <b>Note: This method also uses the database to look up the player,
     * which is why the method takes longer and should be avoided.</b>
     * @param uuid The uuid of the player
     * @return The player or null if no player exists with the specified uuid
     */
    @Nullable
    public static BanPlayer getPlayer(UUID uuid) {
        return banAPI.getPlayer(uuid);
    }

    /**
     * Gets a player from the cache by its uuid
     * @param uuid The uuid of the player
     * @return The player or null if no player exists with the specified uuid
     */
    @Nullable
    public static BanPlayer getCachedPlayer(UUID uuid) {
        return banAPI.getPlayer(uuid, false);
    }

    /**
     * Gets a list of all cached players
     * @return A list of all cached players
     */
    @NotNull
    public static List<BanPlayer> getCachedPlayers() {
        return banAPI.getCachedPlayers();
    }

    /**
     * Gets a ban reason by its id
     * @param id The id of the ban reason
     * @return The ban reason or null if no reason exists with the specified id
     */
    @Nullable
    public static BanReason getBanReasonById(int id) {
        return banAPI.getBanReasonById(id);
    }

    /**
     * Gets a mute reason by its id
     * @param id The id of the mute reason
     * @return The mute reason or null if no reason exists with the specified id
     */
    @Nullable
    public static MuteReason getMuteReasonById(int id) {
        return banAPI.getMuteReasonById(id);
    }

    /**
     * Gets all loaded reasons
     * @return The loaded reasons
     */
    @NotNull
    public static List<Reason> getReasons() {
        return banAPI.getReasons();
    }

    /**
     * Gets all loaded ban reasons
     * @return The loaded ban reasons
     */
    @NotNull
    public static List<BanReason> getBanReasons() {
        return banAPI.getReasonByType(BanReason.class);
    }

    /**
     * Gets all loaded mute reasons
     * @return The loaded mute reasons
     */
    @NotNull
    public static List<MuteReason> getMuteReasons() {
        return banAPI.getReasonByType(MuteReason.class);
    }

    /**
     * Gets a player by its ip hash
     * @param hash The hash of the player
     * @return The player by its hash
     */
    @NotNull
    public static List<Ban> getBansByIP(@NotNull String hash) {
        return banAPI.getBansByIP(hash);
    }

    /**
     * Gets an {@link Unsafe} instance
     * @return The active unsafe instance
     */
    @NotNull
    public static Unsafe getUnsafe() {
        return banAPI.getUnsafe();
    }


}
