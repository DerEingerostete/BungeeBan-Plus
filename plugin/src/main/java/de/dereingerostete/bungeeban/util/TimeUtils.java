/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.util;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    @NotNull
    public static String format(long time) {
        long seconds = Long.divideUnsigned(time, 1000);
        if (seconds > 60 * 60 * 24)
            return Lang.getMessage("timeLayout.days", false,
                    "D", seconds / 86400,
                    "H", seconds / 60 / 60 % 24,
                    "M", seconds / 60 % 60,
                    "S", seconds % 60);
        else if (seconds > 60 * 60)
            return Lang.getMessage("timeLayout.hours", false,
                    "H", seconds / 60 / 60,
                    "M", seconds / 60 % 60,
                    "S", seconds % 60);
        else if (seconds > 60)
            return Lang.getMessage("timeLayout.minutes", false,
                    "M", seconds / 60,
                    "S", seconds % 60);
        else return Lang.getMessage("timeLayout.seconds", false,
                    "S", seconds);
    }

    @NotNull
    public static String formatDate(long time) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(new Date(time));
    }

}
