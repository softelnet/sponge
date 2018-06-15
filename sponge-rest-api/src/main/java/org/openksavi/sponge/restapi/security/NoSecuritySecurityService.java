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

package org.openksavi.sponge.restapi.security;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.kb.KnowledgeBase;

public class NoSecuritySecurityService extends BaseRestApiSecurityService {

    private User createGuestUser() {
        User user = new User("guest", null);
        user.addRoles(Role.ADMIN, Role.STANDARD, Role.GUEST);

        return user;
    }

    @Override
    public User authenticateUser(String username, String password) {
        return createGuestUser();
    }

    @Override
    public boolean canCallAction(User user, ActionAdapter actionAdapter) {
        return true;
    }

    @Override
    public boolean canSendEvent(User user, String eventName) {
        return true;
    }

    @Override
    public boolean canUseKnowledgeBase(User user, KnowledgeBase knowledgeBase) {
        return true;
    }
}
