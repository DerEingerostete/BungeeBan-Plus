/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.chatlog;

import com.pastebin.api.*;
import com.pastebin.api.request.PasteRequest;
import de.dereingerostete.bungeeban.entity.mute.chatlog.ChatLog;
import de.dereingerostete.bungeeban.entity.mute.chatlog.TextUpload;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PastebinAPI implements TextUpload {
    protected final PastebinClient client;
    protected final Options options;

    public PastebinAPI(@NotNull Options options) {
        this.options = options;
        PastebinClient.Builder builder = PastebinClient.builder();
        builder.developerKey(options.getDeveloperKey());
        builder.userKey(options.getUserKey());
        client = builder.build();
    }

    @NotNull
    @Override
    public String post(@NotNull String content) throws IOException {
        PasteRequest.Builder builder = PasteRequest.content(content);
        builder.name(RandomStringUtils.randomAlphabetic(8));
        builder.expiration(options.getExpiration());
        builder.visibility(options.getVisibility());
        builder.format(Format.NONE);

        try {
            return client.paste(builder.build());
        } catch (PastebinException exception) {
            throw new IOException(exception);
        }
    }

    @NotNull
    @Override
    public ChatLog.Link createLink(@NotNull String content) throws IOException {
        long lifetime;
        switch (options.getExpiration()) {
            case TEN_MINUTES:
                lifetime = 600000L;
                break;
            case ONE_HOUR:
                lifetime = 36000000L;
                break;
            case ONE_DAY:
                lifetime = 86400000L;
                break;
            case ONE_WEEK:
                lifetime = 604800000L;
                break;
            case TWO_WEEKS:
                lifetime = 1209600000;
                break;
            case ONE_MONTH:
                lifetime = 2592000000L;
                break;
            case SIX_MONTHS:
                lifetime = 15552000000L;
                break;
            case ONE_YEAR:
                lifetime = 31536000000L;
                break;
            case NEVER:
            default:
                lifetime = -1;
        }

        String url = post(content);
        return new ChatLog.Link(url, lifetime);
    }

    @Data
    public static class Options {
        protected final @NotNull String developerKey, userKey;
        protected final @NotNull Expiration expiration;
        protected final @NotNull Visibility visibility;
    }

}
