/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.util;

import org.jetbrains.annotations.NotNull;

public class ArrayUtils {

    public static @NotNull String toString(Object @NotNull [] array, String connector) {
        int iMax = array.length - 1;
        StringBuilder builder = new StringBuilder();

        for (int i = 0; ; i++) {
            builder.append(array[i]);
            if (i == iMax) break;
            builder.append(connector);
        }
        return builder.toString();
    }

    public static @NotNull String toLines(Object[] array) {
        return toString(array, "\n");
    }

}
