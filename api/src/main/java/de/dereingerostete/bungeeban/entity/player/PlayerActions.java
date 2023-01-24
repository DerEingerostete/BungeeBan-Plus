/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.player;

import org.jetbrains.annotations.NotNull;

public interface PlayerActions {

    /**
     * Sends the player a translated message
     * @param messageKey The key of the message translation
     * @param values The values matching the message
     */
    void sendMessage(@NotNull String messageKey, Object... values);

    /**
     * Sends the player a message
     * @param message The message to send
     */
    void sendMessage(@NotNull String message);

    /**
     * Sends the player a message
     * @param lines The message to send as lines
     */
    void sendMessage(@NotNull String[] lines);

    /**
     * Kicks a player with a translated message
     * @param messageKey The key of the message translation
     * @param values The values matching the message
     */
    void disconnect(@NotNull String messageKey, Object... values);

    /**
     * Kicks a player with the given message
     * @param message The kick message
     */
    void disconnect(@NotNull String message);

    /**
     * Kicks a player with the given message
     * @param lines The kick message as lines
     */
    void disconnect(@NotNull String[] lines);

}
