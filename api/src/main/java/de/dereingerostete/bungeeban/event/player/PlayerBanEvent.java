/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.event.player;

import de.dereingerostete.bungeeban.entity.ban.Ban;
import de.dereingerostete.bungeeban.entity.player.BanPlayer;
import de.dereingerostete.bungeeban.event.Cancellable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class PlayerBanEvent extends PlayerEvent implements Cancellable {
    protected final @NotNull Ban ban;
    protected boolean cancelled;

    public PlayerBanEvent(@NotNull BanPlayer player, @NotNull Ban ban) {
        super(player);
        this.ban = ban;
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
