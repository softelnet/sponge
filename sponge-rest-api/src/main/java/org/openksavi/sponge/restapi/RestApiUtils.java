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

package org.openksavi.sponge.restapi;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import org.openksavi.sponge.restapi.security.User;

/**
 * A set of REST API utility methods.
 */
public abstract class RestApiUtils {

    private RestApiUtils() {
        //
    }

    public static User createAnonymousUser() {
        User user = new User(RestApiConstants.DEFAULT_ANONYMOUS_USERNAME, null);
        user.addRoles(RestApiConstants.PREDEFINED_ROLE_GUEST);

        return user;
    }

    public static boolean isActionPrivate(String actionName) {
        return actionName.startsWith(RestApiConstants.PRIVATE_ACTION_NAME_PREFIX);
    }

    /**
     * Verifies if a user can use a knowledge base.
     *
     * @param roleToKnowledgeBases the map of (role name -> knowledge base names regexps).
     * @param user the user.
     * @param kbName the knowledge base name.
     * @return {@code true} if this user can use the knowledge base.
     */
    public static boolean canUseKnowledgeBase(Map<String, Collection<String>> roleToKnowledgeBases, User user, String kbName) {
        if (roleToKnowledgeBases == null) {
            return false;
        }

        return user.getRoles().stream().filter(role -> roleToKnowledgeBases.containsKey(role)).anyMatch(
                role -> roleToKnowledgeBases.get(role).stream().filter(Objects::nonNull).anyMatch(kbRegexp -> kbName.matches(kbRegexp)));
    }
}
