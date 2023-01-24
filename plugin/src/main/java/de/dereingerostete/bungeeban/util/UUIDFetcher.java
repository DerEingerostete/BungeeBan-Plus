/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.util;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UUIDFetcher {
    private static final Map<String, UUID> uuidCache = Maps.newHashMap();
    private static final Map<UUID, String> nameCache = Maps.newHashMap();

    /**
     * Fetches the uuid synchronously and returns it
     *
     * @param name The name
     * @return The uuid
     */
    @Nullable
    public static UUID getUUID(String name) {
        return getUUIDAt(name, System.currentTimeMillis());
    }

    /**
     * Fetches the uuid synchronously for a specified name and time
     *
     * @param name The name
     * @param timestamp Time when the player had this name in milliseconds
     */
    @Nullable
    @SneakyThrows(IOException.class)
    public static UUID getUUIDAt(String name, long timestamp) {
        String lookupName = name.toLowerCase();
        UUID uuid = uuidCache.get(lookupName);
        if (uuid != null) return uuid;

        URL url = new URL(getUUIDUrl(name, timestamp));
        URLConnection connection = url.openConnection();
        connection.setReadTimeout(2000);

        InputStream inputStream = connection.getInputStream();
        String response = IOUtils.toString(inputStream, StandardCharsets.US_ASCII);
        if (response.isEmpty()) return null;

        Lookup lookup = Lookup.withName(response, name);
        uuidCache.put(lookupName, lookup.getUuid());
        nameCache.put(lookup.getUuid(), lookup.getName());
        return lookup.getUuid();
    }

    /**
     * Fetches the name synchronously and returns it
     *
     * @param uuid The uuid
     * @return The name
     */
    @Nullable
    @SneakyThrows(IOException.class)
    public static String getName(@NotNull UUID uuid) {
        String name = nameCache.get(uuid);
        if (name != null) return name;

        URL url = new URL(getNameUrl(uuid));
        URLConnection connection = url.openConnection();
        connection.setReadTimeout(2000);

        InputStream inputStream = connection.getInputStream();
        String response = IOUtils.toString(inputStream, StandardCharsets.US_ASCII);
        if (response.isEmpty()) return null;

        Lookup lookup = Lookup.withUUID(response, uuid);
        name = lookup.getName().toLowerCase();
        uuidCache.put(name, uuid);
        nameCache.put(uuid, name);
        return lookup.getName();
    }


    @NotNull
    public static String getUUIDUrl(@NotNull String name, long timestamp) {
        return "https://api.mojang.com/users/profiles/minecraft/" + name + "?at=" + (timestamp / 1000);
    }

    @NotNull
    public static String getNameUrl(@NotNull UUID uuid) {
        return "https://api.mojang.com/user/profiles/" + uuid + "/names";
    }

    public static void startTask(@NotNull Plugin plugin) {
        TaskScheduler scheduler = ProxyServer.getInstance().getScheduler();
        scheduler.schedule(plugin, UUIDFetcher::clearCache, 6L, 6L, TimeUnit.HOURS);
    }

    public static void clearCache() {
        nameCache.clear();
        uuidCache.clear();
    }

    @Data
    @RequiredArgsConstructor
    private static class Lookup {
        private final @NotNull UUID uuid;
        private final @NotNull String name;

        public static Lookup withName(@NotNull String response, @NotNull String name) {
            int startIndex = response.indexOf("\"id\"") + 6; //3 of "id" + 3 of ":"
            int endIndex = response.length() - 2;
            String uuidString = response.substring(startIndex, endIndex);
            return new Lookup(fromString(uuidString), name);
        }

        public static Lookup withUUID(@NotNull String response, @NotNull UUID uuid) {
            int startIndex = response.lastIndexOf("\"name\"") + 8; //6 of "name" + 3 of ":"
            int endIndex = response.indexOf("\"", startIndex);
            String name = response.substring(startIndex, endIndex);
            return new Lookup(uuid, name);
        }

        private static UUID fromString(String input) {
            return UUID.fromString(input.replaceFirst(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        }

    }

}
