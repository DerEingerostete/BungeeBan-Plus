/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.entity.mute.chatlog;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
public abstract class ChatLog {
    protected final @NotNull String id;
    protected final long createdAt;
    protected final @NotNull File file;

    /**
     * Gets the time of creation
     * @return The creation time
     */
    public long getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the file of the chatlog
     * @return The file
     */
    @NotNull
    public File getFile() {
        return file;
    }

    /**
     * Gets the id of the chatlog
     * @return The id of the chatlog
     */
    @NotNull
    public String getId() {
        return id;
    }

    /**
     * Generates a temporary link containing a pastebin of the log
     * @return A new generated link
     * @throws IOException If an upload error occurs
     */
    @NotNull
    public abstract Link generateLink() throws IOException;

    public static class Link {
        protected final @NotNull String url;
        protected final long lifetime;
        protected final long creationTime;

        public Link(@NotNull String url, long lifetime) {
            this.url = url;
            this.lifetime = lifetime;
            creationTime = System.currentTimeMillis();
        }

        /**
         * Gets the url of the pastebin
         * @return The url as a String
         */
        @NotNull
        public String getURL() {
            return url;
        }

        /**
         * Gets the lifetime of the link in milliseconds
         * The lifetime is the duration the link is accessible
         * @return The duration of the link
         */
        public long getLifetime() {
            return lifetime;
        }

        /**
         * Gets the time when the link was generated
         * @return The time of creation in milliseconds
         */
        public long getCreationTime() {
            return creationTime;
        }

    }

}
