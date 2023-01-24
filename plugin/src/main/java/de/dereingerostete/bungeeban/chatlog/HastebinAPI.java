/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.chatlog;

import com.google.common.base.Preconditions;
import de.dereingerostete.bungeeban.entity.mute.chatlog.ChatLog;
import de.dereingerostete.bungeeban.entity.mute.chatlog.TextUpload;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class HastebinAPI implements TextUpload {
    private final String baseUrl;

    public HastebinAPI(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @NotNull
    @Override
    public String post(@NotNull String content) throws IOException {
        Preconditions.checkNotNull(content, "Content cannot be null");
        URL url = new URL(baseUrl + "/documents");
        HttpURLConnection connection = getHttpConnection(url);

        connection.setRequestMethod("POST");
        connection.addRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setConnectTimeout(750);
        connection.setReadTimeout(750);
        connection.setUseCaches(false);
        connection.connect();

        OutputStream output = connection.getOutputStream();
        PrintStream printStream = new PrintStream(output, false, StandardCharsets.UTF_8);
        printStream.print(content);
        printStream.flush();
        printStream.close();

        InputStream inputStream = connection.getInputStream();
        String response = IOUtils.toString(inputStream, StandardCharsets.US_ASCII);

        connection.connect();
        inputStream.close();

        JSONObject object = new JSONObject(response);
        String key = object.optString("key", null);

        if (key == null) throw new IOException("Invalid json response");
        else return baseUrl + "/" + key;
    }

    @NotNull
    @Override
    public ChatLog.Link createLink(@NotNull String content) throws IOException {
        return new ChatLog.Link(post(content), 604800000L); //7 Days by default
    }

    @NotNull
    public String getBaseUrl() {
        return baseUrl;
    }

    @NotNull
    private HttpURLConnection getHttpConnection(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        if (!(connection instanceof HttpURLConnection))
            throw new IOException("URLConnection does not extend HttpURLConnection");
        return (HttpURLConnection) connection;
    }

}
