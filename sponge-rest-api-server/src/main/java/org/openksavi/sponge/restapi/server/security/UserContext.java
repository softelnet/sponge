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

package org.openksavi.sponge.restapi.server.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class UserContext {

    private String name;

    private Set<String> roles = Collections.synchronizedSet(new LinkedHashSet<>());

    public UserContext(String name) {
        this.name = name;
    }

    public UserContext(String name, String... newRoles) {
        this(name);

        addRoles(newRoles);
    }

    public UserContext(String name, Collection<String> newRoles) {
        this(name);

        addRoles(newRoles);
    }

    public UserContext(UserContext user) {
        copyFrom(user);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void addRoles(String... newRoles) {
        Arrays.stream(newRoles).forEach(role -> addRole(role));
    }

    public void addRoles(Collection<String> newRoles) {
        newRoles.forEach(role -> addRole(role));
    }

    public void addRole(String role) {
        roles.add(role);
    }

    public void removeRole(String role) {
        roles.remove(role);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public void clearRoles() {
        roles.clear();
    }

    public void copyFrom(UserContext user) {
        name = user.name;
        roles = user.roles;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserContext other = (UserContext) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
