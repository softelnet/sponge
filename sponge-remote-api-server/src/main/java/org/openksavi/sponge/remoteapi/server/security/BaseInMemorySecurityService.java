/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.server.security;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.remoteapi.server.util.RemoteApiSecurityUtils;

public abstract class BaseInMemorySecurityService extends BaseSecurityService {

    private String passwordEntryFormat;

    protected Set<User> users = Collections.synchronizedSet(new LinkedHashSet<>());

    protected BaseInMemorySecurityService() {
        //
    }

    public Set<User> getUsers() {
        return users;
    }

    public void addUser(User user) {
        Validate.isTrue(users.add(user), "User %s already exists", user.getName());
    }

    public void removeUser(String username) {
        users.removeAll(users.stream().filter(user -> Objects.equals(user.getName(), username)).collect(Collectors.toSet()));
    }

    public User getUser(String username) {
        Optional<User> userO = users.stream().filter(user -> Objects.equals(user.getName(), username)).findFirst();
        Validate.isTrue(userO.isPresent(), "User %s not found", username);

        return userO.get();
    }

    public void updateUser(User user) {
        getUser(user.getName()).copyFrom(user);
    }

    public String hashPassword(String username, String password) {
        return passwordEntryFormat != null ? RemoteApiSecurityUtils.hashPassword(username, password, passwordEntryFormat)
                : RemoteApiSecurityUtils.hashPassword(username, password);
    }

    /**
     * Returns {@code null} if the user doesn't exist or the password is incorrect.
     *
     * @param username the username.
     * @param password the password
     * @return the user.
     */
    protected User verifyInMemory(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        // Hash the password.
        String hashedPassword = hashPassword(username, password);

        return users.stream().filter(user -> Objects.equals(user.getName(), username))
                .filter(user -> Objects.equals(user.getPassword(), hashedPassword)).findAny().orElse(null);
    }

    public String getPasswordEntryFormat() {
        return passwordEntryFormat;
    }

    public void setPasswordEntryFormat(String passwordEntryFormat) {
        this.passwordEntryFormat = passwordEntryFormat;
    }

    public void loadUsers() {
        loadUsers(RemoteApiSecurityUtils.getPasswordFile(getRemoteApiService().getEngine()));
    }

    public void loadUsers(String filename) {
        loadUsers(filename, StandardCharsets.UTF_8);
    }

    public void loadUsers(String filename, Charset charset) {
        RemoteApiSecurityUtils.readUsers(filename, charset).forEach(this::addUser);
    }
}
