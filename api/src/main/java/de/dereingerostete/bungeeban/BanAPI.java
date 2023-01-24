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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface BanAPI {

    /**
     * Gets the EventHandler used to register listeners
     * @return The active EventHandler
     */
    @NotNull
    EventHandler getEventHandler();

    /**
     * Gets a player by its uuid
     * @param uuid The uuid of the player
     * @param lookup True if the player is to be looked up in the database;
     *               False if it is to be looked up in the cache only
     * @return The player or null if no player exists with the specified uuid
     */
    @Nullable
    BanPlayer getPlayer(@NotNull UUID uuid, boolean lookup);

    /**
     * Creates a player with a given uuid
     * @param uuid The uuid of the player
     * @return The newly created player
     */
    @NotNull
    BanPlayer createPlayer(@NotNull UUID uuid);

    /**
     * Gets a player by its uuid
     * <b>Note: This method also uses the database to look up the player,
     * which is why the method takes longer and should be avoided.</b>
     * @param uuid The uuid of the player
     * @return The player or null if no player exists with the specified uuid
     */
    @Nullable
    default BanPlayer getPlayer(@NotNull UUID uuid) {
        return getPlayer(uuid, true);
    }

    /**
     * Gets a player from the cache by its uuid
     * @param uuid The uuid of the player
     * @return The player or null if no player exists with the specified uuid
     */
    @Nullable
    default BanPlayer getCachedPlayer(@NotNull UUID uuid) {
        return getPlayer(uuid, false);
    }


    /**
     * Gets a player by its ip hash
     * @param hash The hash of the player
     * @return The player by its hash
     */
    @NotNull
    List<Ban> getBansByIP(@NotNull String hash);

    /**
     * Gets a list of all cached players
     * @return A list of all cached players
     */
    @NotNull
    List<BanPlayer> getCachedPlayers();

    /**
     * Gets all loaded reasons
     * @return The loaded reasons
     */
    @NotNull
    List<Reason> getReasons();

    /**
     * Gets a ban reason by its id
     * @param id The id of the ban reason
     * @return The ban reason or null if no reason exists with the specified id
     */
    @Nullable
    BanReason getBanReasonById(int id);

    /**
     * Gets a mute reason by its id
     * @param id The id of the mute reason
     * @return The mute reason or null if no reason exists with the specified id
     */
    @Nullable
    MuteReason getMuteReasonById(int id);

    /**
     * Gets all loaded ban reasons
     * @return The loaded ban reasons
     */
    @NotNull
    default List<BanReason> getBanReasons() {
        return getReasonByType(BanReason.class);
    }

    /**
     * Gets all loaded mute reasons
     * @return The loaded mute reasons
     */
    @NotNull
    default List<MuteReason> getMuteReasons() {
        return getReasonByType(MuteReason.class);
    }

    /**
     * Gets all loaded reasons by type
     * @param typeClass The class of the type
     * @param <T> The type to search for
     * @return The list of reasons by its type
     */
    @NotNull
    default <T extends Reason> List<T> getReasonByType(@NotNull Class<T> typeClass) {
        return getReasons().stream()
                .filter(typeClass::isInstance)
                .map(typeClass::cast)
                .collect(Collectors.toList());
    }

    /**
     * Gets an {@link Unsafe} instance
     * @return The active unsafe instance
     */
    @NotNull
    Unsafe getUnsafe();

}
