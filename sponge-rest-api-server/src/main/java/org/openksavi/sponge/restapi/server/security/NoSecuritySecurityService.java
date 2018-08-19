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

package org.openksavi.sponge.restapi.server.security;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.restapi.server.RestApiServerConstants;
import org.openksavi.sponge.restapi.server.util.RestApiServerUtils;

public class NoSecuritySecurityService extends BaseRestApiSecurityService {

    private User createAnonymousUser() {
        return RestApiServerUtils.createAnonymousUser(RestApiServerConstants.DEFAULT_ROLE_GUEST);
    }

    @Override
    public User authenticateUser(String username, String password, Exchange exchange) {
        Validate.isTrue(username == null && password == null, "Only anonymous access is allowed in the no-security configuration");
        return createAnonymousUser();
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

    @Override
    public User getUser(String username) {
        return createAnonymousUser();
    }
}
