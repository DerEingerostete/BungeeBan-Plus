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
import de.dereingerostete.bungeeban.entity.mute.Mute;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the history of a player
 * <b>Warning: This dosen't contains active punishments</b>
 */
@RequiredArgsConstructor
public class PlayerHistory {
    protected final @NotNull List<Punishment> punishments;

    public void addPunishment(Punishment punishment) {
        punishments.add(punishment);
    }

    /**
     * Gets the list of punishments that the player had
     * @return The list of punishments
     */
    @NotNull
    public List<Punishment> getPunishments() {
        return punishments;
    }

    /**
     * Gets the list of bans
     * @return The list of bans
     */
    @NotNull
    public List<Ban> getBans() {
        return getPunishmentByType(Ban.class);
    }

    /**
     * Gets the list of mutes
     * @return The list of mutes
     */
    @NotNull
    public List<Mute> getMutes() {
        return getPunishmentByType(Mute.class);
    }

    /**
     * Gets the list of punishments by type
     * @param classType The class of the type
     * @param <T> The type to search for
     * @return The list of punishments by its type
     */
    @NotNull
    public <T> List<T> getPunishmentByType(@NotNull Class<T> classType) {
        return getPunishments().stream()
                .filter(classType::isInstance)
                .map(classType::cast)
                .collect(Collectors.toList());
    }

    /**
     * Gets the amount of punishment the player had
     * @return The amount of punishments
     */
    public int getAmount() {
        return getPunishments().size();
    }

}