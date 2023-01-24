/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.event;

import de.dereingerostete.bungeeban.event.player.*;
import org.jetbrains.annotations.NotNull;

/**
 * Listener that contains all events
 */
public abstract class BanListener {

    /**
     * Is called when an event is executed
     * @param event The current event
     */
    public void onEvent(@NotNull Event event) {}

    /**
     * Is called when a player event is executed
     * @param event The current event
     */
    public void onPlayerEvent(@NotNull PlayerEvent event) {}

    /**
     * Is called when a player is banned
     * @param event The current event
     */
    public void onBan(@NotNull PlayerBanEvent event) {}

    /**
     * Is called when a player is unbanned
     * @param event The current event
     */
    public void onUnban(@NotNull PlayerUnbanEvent event) {}

    /**
     * Is called when a player is muted
     * @param event The current event
     */
    public void onMute(@NotNull PlayerMuteEvent event) {}

    /**
     * Is called when a player is unmuted
     * @param event The current event
     */
    public void onUnmute(@NotNull PlayerUnmuteEvent event) {}

    public final void callEvent(@NotNull Event event) {
        onEvent(event);
        if (event instanceof PlayerEvent) onPlayerEvent((PlayerEvent) event);
        if (event instanceof PlayerBanEvent) onBan((PlayerBanEvent) event);
        else if (event instanceof PlayerUnbanEvent) onUnban((PlayerUnbanEvent) event);
        else if (event instanceof PlayerMuteEvent) onMute((PlayerMuteEvent) event);
        else if (event instanceof PlayerUnmuteEvent) onUnmute((PlayerUnmuteEvent) event);
    }

}
