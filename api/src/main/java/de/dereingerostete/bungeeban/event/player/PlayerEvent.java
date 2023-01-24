/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.event.player;

import de.dereingerostete.bungeeban.entity.player.BanPlayer;
import de.dereingerostete.bungeeban.event.Event;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PlayerEvent extends Event {
    protected final @NotNull BanPlayer player;

}

