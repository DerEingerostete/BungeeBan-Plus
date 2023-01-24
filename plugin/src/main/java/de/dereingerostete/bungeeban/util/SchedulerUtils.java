/*
 * Copyright (c) 2023 × DerEingerostete
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package de.dereingerostete.bungeeban.util;

import de.dereingerostete.bungeeban.BungeeBanPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class SchedulerUtils {

    public static void runAsync(Runnable runnable) {
        TaskScheduler scheduler = getScheduler();
        scheduler.runAsync(BungeeBanPlugin.getInstance(), runnable);
    }

    protected static TaskScheduler getScheduler() {
        return ProxyServer.getInstance().getScheduler();
    }

}
