/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;

public class MapUtils {

    @NotNull
    public static String toString(@NotNull Map<?, ?> map) {
        StringBuilder builder = new StringBuilder();
        map.forEach((key, value) ->
                builder.append(key)
                .append('=')
                .append(value)
                .append(';'));

        String string = builder.toString();
        if (string.length() == 0) return string;
        else return string.substring(0, string.length() - 1);
    }

    @NotNull
    public static <K, V> Map<K, V> fromString(@NotNull String string, @NotNull ObjectConverter<K> keyConverter,
                                              @NotNull ObjectConverter<V> valueConverter) {
        Map<K, V> map = Maps.newHashMap();
        String[] entries = string.split(";");

        for (String entry : entries) {
            String[] entryValues = entry.split("=", 2);
            Preconditions.checkState(entryValues.length == 2, "No key, value pair found ("
                            + Arrays.toString(entryValues) + ")");

            String key = entryValues[0];
            String value = entryValues[1];
            map.put(keyConverter.fromString(key), valueConverter.fromString(value));
        }

        return map;
    }

    public interface ObjectConverter<T> {
        T fromString(String string);
    }

    public static class StringConverter implements ObjectConverter<String> {

        @Override
        public String fromString(String string) {
            return string;
        }

    }

    public static class LongConverter implements ObjectConverter<Long> {

        @Override
        public Long fromString(String string) {
            return Long.parseLong(string);
        }

    }

}
