/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.util;

import de.dereingerostete.bungeeban.entity.Punishment;
import de.dereingerostete.bungeeban.entity.Reason;
import de.dereingerostete.bungeeban.entity.ban.Ban;
import de.dereingerostete.bungeeban.entity.mute.Mute;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class MessageUtils {

    public static String[] formatBan(@NotNull Ban ban) {
        return formatPunishment(ban, "ban");
    }

    public static String[] formatMute(@NotNull Mute mute) {
        return formatPunishment(mute, "mute");
    }

    private static String[] formatPunishment(@NotNull Punishment punishment, @NotNull String type) {
        Reason reason = punishment.getReason();
        String displayName = reason.getDisplayName();

        long timeOfEnd = punishment.getTimeOfEnd() - System.currentTimeMillis();
        String formattedTimeOfEnd = timeOfEnd == -1 ? "permanent" : TimeUtils.format(timeOfEnd < 0 ? 0 : timeOfEnd);
        String key = punishment.isPermanent() ? type + ".permanentMessage" : type + ".temporaryMessage";

        return Lang.getLayout(key, false,
                "REASON", displayName,
                "DURATION", formattedTimeOfEnd);
    }

    @NotNull
    public static BaseComponent createBanComponent(@NotNull Ban ban) {
        BaseComponent component = new TextComponent();
        for (String line : formatBan(ban)) component.addExtra(line + "\n");
        return component;
    }

}
