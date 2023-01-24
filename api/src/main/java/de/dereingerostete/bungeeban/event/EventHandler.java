/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Handler for registering {@link BanListener}
 */
public class EventHandler {
    protected final List<BanListener> listeners;

    public EventHandler() {
        listeners = Lists.newArrayList();
    }

    /**
     * Registers a new listener
     * @param listener The listener to register
     */
    public void registerListener(@NotNull BanListener listener) {
        Preconditions.checkNotNull(listener, "Listener cannot be null");
        listeners.add(listener);
    }

    /**
     * Unregisters a listener
     * @param listener The listener
     * @return If the listener was unregistered
     */
    public boolean unregisterListener(@NotNull BanListener listener) {
        Preconditions.checkNotNull(listener, "Listener cannot be null");
        return listeners.remove(listener);
    }

    /**
     * Calls an event to all listeners
     * @param event The event to call
     */
    public void callEvent(@NotNull Event event) {
        Preconditions.checkNotNull(event, "Event cannot be null");
        listeners.forEach(listener -> listener.callEvent(event));
    }

}
