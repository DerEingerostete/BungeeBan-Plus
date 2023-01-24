/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.mute;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
public class Message {
    protected final @NotNull UUID uniqueId;
    protected final @NotNull String message;
    protected final long timestamp;

}
