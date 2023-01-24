/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban;

import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.database.BanDatabase;
import de.dereingerostete.bungeeban.entity.Cache;
import de.dereingerostete.bungeeban.entity.Reason;
import de.dereingerostete.bungeeban.entity.Reasons;
import de.dereingerostete.bungeeban.entity.Unsafe;
import de.dereingerostete.bungeeban.entity.ban.Ban;
import de.dereingerostete.bungeeban.entity.ban.BanReason;
import de.dereingerostete.bungeeban.entity.mute.MuteReason;
import de.dereingerostete.bungeeban.entity.player.BanPlayer;
import de.dereingerostete.bungeeban.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class BungeeBanAPI implements BanAPI {
    protected final @NotNull EventHandler handler;
    protected final @NotNull BanDatabase database;
    protected final @NotNull Reasons reasons;
    protected final @NotNull Cache cache;

    public BungeeBanAPI(@NotNull BanDatabase database,
                        @NotNull Reasons reasons,
                        @NotNull Cache cache) {
        this.database = database;
        this.reasons = reasons;
        this.cache = cache;
        handler = new EventHandler();
    }

    @NotNull
    @Override
    public EventHandler getEventHandler() {
        return handler;
    }

    @Override
    @Nullable
    public BanPlayer getPlayer(@NotNull UUID uuid, boolean lookup) {
        BanPlayer player = cache.getPlayer(uuid);
        if (!lookup || player != null) return player;

        try {
            return database.getPlayer(uuid);
        } catch (SQLException exception) {
            Logging.warning("Failed to lookup player (" + uuid + ") from database", exception);
            return null;
        }
    }

    @Override
    @NotNull
    public BanPlayer createPlayer(@NotNull UUID uuid) {
        try {
            return database.createPlayer(uuid);
        } catch (SQLException exception) {
            throw createException("creating a new player (" + uuid + ")", exception );
        }
    }

    @Override
    @NotNull
    public List<Ban> getBansByIP(@NotNull String hash) {
        try {
            return database.getBansByIP(hash);
        } catch (SQLException exception) {
            throw createException("getting bans by ip hash (" + hash + ")", exception);
        }
    }

    @Override
    @NotNull
    public List<BanPlayer> getCachedPlayers() {
        return cache.getPlayers();
    }

    @Override
    @NotNull
    public List<Reason> getReasons() {
        return reasons.getReasons();
    }

    @Override
    @Nullable
    public BanReason getBanReasonById(int id) {
        return reasons.getReasonById(id, BanReason.class);
    }

    @Override
    @Nullable
    public MuteReason getMuteReasonById(int id) {
        return reasons.getReasonById(id, MuteReason.class);
    }

    @Override
    @NotNull
    public Unsafe getUnsafe() {
        return () -> database;
    }

    protected RuntimeException createException(String what, Throwable throwable) {
        return new RuntimeException("An unexpected error occurred while " + what, throwable);
    }

}
