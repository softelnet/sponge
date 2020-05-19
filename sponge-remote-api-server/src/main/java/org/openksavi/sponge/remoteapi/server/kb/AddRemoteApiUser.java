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

package org.openksavi.sponge.remoteapi.server.kb;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.remoteapi.server.util.RemoteApiSecurityUtils;

/**
 * Add a Remote API user to a password file.
 */
public class AddRemoteApiUser extends JAction {

    public void onCall(String passwordFile) {
        try {
            Charset charset = StandardCharsets.UTF_8;
            File file = new File(passwordFile);
            Validate.isTrue(file.isFile(), "Password file %s doesn't exist", passwordFile);

            String entry = getSponge().call(String.class, "GenerateRemoteApiPasswordEntry");
            String username = entry.split(":")[0];

            Validate.isTrue(
                    !RemoteApiSecurityUtils.readUsers(passwordFile, charset).stream().anyMatch(user -> user.getName().equals(username)),
                    "User %s already exists", username);

            FileUtils.write(file, "\n" + entry, charset, true);
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }
}
