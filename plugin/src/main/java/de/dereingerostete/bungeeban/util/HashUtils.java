/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    @NotNull
    public static String hashAddress(@NotNull InetAddress address) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_256);
            digest.update(new byte[] {0, 1, 0, 2, 2, 5, 6, 8, 1, 8, 7, 6, 5, 0, 1, 2, 3});
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(exception);
        }

        byte[] bytes = digest.digest(address.getAddress());
        return Hex.encodeHexString(bytes);
    }

}
