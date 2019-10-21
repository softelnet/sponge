/*
 * Copyright 2016-2019 The Sponge authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge.restapi.server.kb;

import java.io.Console;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.restapi.server.util.RestApiSecurityUtils;

/**
 * Generates a Remote API password file entry.
 */
public class GenerateRemoteApiPasswordEntry extends JAction {

    public String onCall() {
        Console console = Validate.notNull(System.console(), "Can't get the system console");

        String username = null;
        while (username == null || username.isEmpty()) {
            username = console.readLine("Enter a new user name: ");
            if (username != null) {
                username = username.trim();
            }
        }

        String group = null;
        while (group == null || group.isEmpty()) {
            group = console.readLine("Enter a new user group (or comma separated groups): ");
            if (group != null) {
                group = group.trim();
            }
        }
        group = group.replaceAll("\\s", "");

        String password = null;
        while (password == null || password.isEmpty()) {
            char[] passwordChars = console.readPassword("Enter a new user password: ");
            password = passwordChars != null ? String.valueOf(passwordChars) : null;
            if (password != null) {
                password = password.trim();
            }
        }

        return username + ":" + group + ":" + RestApiSecurityUtils.hashPassword(username, password);
    }
}
