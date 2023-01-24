/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.exception;

public class BanException extends Exception {

    public BanException(String message) {
        super(message);
    }

    public BanException(String message, Throwable cause) {
        super(message, cause);
    }

}
