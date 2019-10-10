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

package org.openksavi.sponge.restapi.server.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.server.security.User;

/**
 * A set of REST API server utility methods for a simple in-memory security.
 */
public abstract class RestApiInMemorySecurityUtils {

    public static final String PROPERTY_PASSWORD_FILE = "password.file";

    public static final String PASSWORD_FILE_FIELD_COMMENT = "#";

    public static final char PASSWORD_FILE_FIELD_SEPARATOR = ':';

    public static final char PASSWORD_FILE_ROLE_SEPARATOR = ',';

    public static final String DEFAULT_PASSWORD_ENTRY_FORMAT = "%s-%s";

    private RestApiInMemorySecurityUtils() {
        //
    }

    public static List<User> readUsers(String filename, Charset charset) {
        List<User> users = new LinkedList<>();

        try {
            FileUtils.readLines(new File(filename), charset).stream().map(String::trim).forEach(line -> {
                if (!line.startsWith(PASSWORD_FILE_FIELD_COMMENT) && !line.isEmpty()) {
                    List<String> fields = Arrays.asList(StringUtils.split(line, PASSWORD_FILE_FIELD_SEPARATOR));
                    Validate.isTrue(fields.size() == 3, "The entry '%s' is invalid", line);
                    String username = fields.get(0).trim();
                    String roleList = fields.get(1).trim();
                    String password = fields.get(2).trim();

                    Validate.isTrue(!username.isEmpty(), "The username in entry '%s' is empty", line);

                    List<String> roles = Arrays.asList(StringUtils.split(roleList, PASSWORD_FILE_ROLE_SEPARATOR)).stream().map(String::trim)
                            .filter(role -> !role.isEmpty()).collect(Collectors.toList());
                    users.add(new User(username, password, roles));
                }
            });
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        }

        return users;
    }

    public static String getPasswordFile(SpongeEngine engine) {
        return Validate.notNull(engine.getConfigurationManager().getProperty(PROPERTY_PASSWORD_FILE),
                "The password file property %s not foud", PROPERTY_PASSWORD_FILE);
    }

    public static String hashPassword(String username, String password, String passwordEntryFormat) {
        return password != null
                ? DigestUtils.sha512Hex(String.format(passwordEntryFormat, username != null ? username.toLowerCase() : "", password))
                : null;
    }

    public static String hashPassword(String username, String password) {
        return hashPassword(username, password, DEFAULT_PASSWORD_ENTRY_FORMAT);
    }
}
