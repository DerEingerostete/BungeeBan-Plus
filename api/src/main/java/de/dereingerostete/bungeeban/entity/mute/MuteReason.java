/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.mute;

import de.dereingerostete.bungeeban.entity.Reason;

public interface MuteReason extends Reason {

    /**
     * Checks if the mute reason requires a chat log
     * @return True if a chat log is required
     */
    boolean requiresChatLog();

}
