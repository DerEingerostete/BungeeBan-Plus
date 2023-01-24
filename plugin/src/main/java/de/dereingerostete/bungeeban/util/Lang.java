/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.util;

import de.dereingerostete.bungeeban.BungeeBanPlugin;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Class used for localization
 */
public class Lang {
    protected static Config config;
    protected static String prefix;

    public static void loadLanguage(@NotNull String languageKey) throws IOException {
        Plugin plugin = BungeeBanPlugin.getInstance();
        File dataFolder = plugin.getDataFolder();
        File file = new File(dataFolder, "lang/" + languageKey + ".json");
        config = new Config(file, "lang/" + file.getName());
    }

    public static void loadLanguage() throws IOException {
        Config defaultConfig = Configs.getDefaultConfig();
        String languageKey = defaultConfig.getString("language", "en");
        loadLanguage(languageKey);
        prefix = getMessage("prefix", false);
    }

    /**
     * Gets the message for the key in the loaded language
     * @param key The key to look up
     * @param parameters The parameters to insert (formatted by first the variable name and then the value)
     * @return The message from the loaded language
     */
    @NotNull
    public static String getMessage(@NotNull String key, boolean withPrefix, @NotNull Object... parameters) {
        Object resolvedObject = resolve(key);
        boolean noParams = parameters == null || parameters.length == 0;

        if (!(resolvedObject instanceof String)) {
            String[] layout = getLayout(key, withPrefix, parameters);
            return ArrayUtils.toLines(layout);
        }

        String string = (String) resolvedObject;
        string = noParams ? string : replace(string, parameters);

        return withPrefix ? prefix + string : string;
    }

    /**
     * Gets the formatted layout for the key in the loaded language
     * @param key The key to look up
     * @param parameters The parameters to insert (formatted by first the variable name and then the value)
     * @return The layout from the loaded language
     */
    @NotNull
    public static String[] getLayout(@NotNull String key, boolean withPrefix, Object... parameters) {
        String[] layout = getPlainLayout(key, withPrefix);
        if (parameters == null || parameters.length == 0) return layout;

        for (int i = 0; i < layout.length; i++)
            layout[i] = replace(layout[i], parameters);
        return layout;
    }

    /**
     * Gets the plain (unformatted) layout for the key in the loaded language
     * @param key The key to look up
     * @return The layout from the loaded language
     */
    @NotNull
    public static String[] getPlainLayout(@NotNull String key, boolean withPrefix) {
        Object resolvedObject = resolve(key);
        if (resolvedObject instanceof String) return new String[] {(String) resolvedObject};
        else if (!(resolvedObject instanceof JSONArray)) return new String[] {key};

        JSONArray array = (JSONArray) resolvedObject;
        int length = array.length();

        String[] formattedStrings = new String[length];
        for (int i = 0; i < length; i++) {
            Object object = array.opt(i);

            String string = String.valueOf(object);
            if (withPrefix /*&& i == 0*/) string = prefix + string;
            formattedStrings[i] = string;
        }

        return formattedStrings;
    }

    @NotNull
    public static String replace(@NotNull String string, @NotNull Object... parameters) {
        for (int i = 0; i < parameters.length - 1; i += 2) {
            Object parameterName = parameters[i];
            Object parameterValue = parameters[i + 1];
            string = string.replaceAll("%" + parameterName + "%", String.valueOf(parameterValue));
        }
        return string;
    }

    @Nullable
    private static Object resolve(@NotNull String key) {
        String[] split = key.split("\\.");
        Object current = config.getObject(split[0]);

        for (int i = 1; i < split.length; i++) {
            if (current instanceof JSONObject)
                current = ((JSONObject) current).opt(split[i]);
            else break;
        }
        return current;
    }

}
