/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity;

import com.google.common.collect.Lists;
import de.dereingerostete.bungeeban.BungeeBanPlugin;
import de.dereingerostete.bungeeban.chat.Logging;
import de.dereingerostete.bungeeban.entity.ban.BanReason;
import de.dereingerostete.bungeeban.entity.ban.BungeeBanReason;
import de.dereingerostete.bungeeban.entity.mute.BungeeMuteReason;
import net.md_5.bungee.api.plugin.Plugin;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class Reasons {
    protected final List<Reason> reasons;

    public Reasons() {
        reasons = Lists.newArrayList();
    }

    public void loadReasons(String fileName, Type type) throws IOException {
        Plugin plugin = BungeeBanPlugin.getInstance();
        File folder = plugin.getDataFolder();

        File file = new File(folder, fileName);
        if (!file.exists()) return;

        String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        JSONArray array = new JSONArray(fileContent);

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.optJSONObject(i);
                if (object == null) continue;

                Reason reason = loadReason(object, type);
                if (reason != null) reasons.add(reason);
                else throw new IllegalStateException("Reason is null");
            } catch (JSONException | IllegalStateException exception) {
                Logging.warning("Failed to load reason from file " + fileName
                        + " with type " + type + " (" + i + ")", exception);
            }
        }
    }

    @Nullable
    protected Reason loadReason(JSONObject object, Type type) {
        int id = object.getInt("id");
        String name = object.getString("name");
        String displayName = object.getString("displayName");
        String description = object.getString("description");

        JSONArray array = object.getJSONArray("duration");
        long[] durations = array.toList().stream()
                .filter(Long.class::isInstance)
                .mapToLong(obj -> (long) obj).toArray();

        switch (type) {
            case BAN:
                boolean ipBan = object.getBoolean("ipBan");
                BanReason.BanType banType = ipBan ? BanReason.BanType.IP : BanReason.BanType.UUID;
                return new BungeeBanReason(id, name, description, displayName, durations, banType);
            case MUTE:
                boolean requiresChatLog = object.getBoolean("requiresChatLog");
                return new BungeeMuteReason(id, name, description, displayName, durations, requiresChatLog);
            default:
                return null;
        }
    }

    public List<Reason> getReasons() {
        return Collections.unmodifiableList(reasons);
    }

    @Nullable
    public <T extends Reason> T getReasonById(int id, Class<T> clazz) {
        return reasons.stream()
                .filter(clazz::isInstance)
                .filter(reason -> reason.getId() == id)
                .map(clazz::cast)
                .findAny().orElse(null);
    }

    public enum Type {
        MUTE,
        BAN
    }

}
