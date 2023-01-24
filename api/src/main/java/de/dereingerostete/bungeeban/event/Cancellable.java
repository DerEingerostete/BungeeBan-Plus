/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.event;

/**
 * Events that implement this class indicates that they may be canceled
 */
public interface Cancellable {

    /**
     * Get whether this event is cancelled
     * @return the cancelled state of this event
     */
    boolean isCancelled();

    /**
     * Sets the cancelled state of this event
     * @param cancel the state to set
     */
    void setCancelled(boolean cancel);

}
