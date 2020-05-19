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

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.remoteapi.server.RemoteApiInvalidUsernamePasswordServerException;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerConstants;
import org.openksavi.sponge.remoteapi.server.util.RemoteApiServerUtils;

public class NoSecuritySecurityService extends BaseRemoteApiSecurityService {

    private UserContext createAnonymousUserContext() {
        return RemoteApiServerUtils.createAnonymousUserContext(RemoteApiServerConstants.DEFAULT_ROLE_ANONYMOUS);
    }

    @Override
    public UserAuthentication authenticateUser(String username, String password) throws RemoteApiInvalidUsernamePasswordServerException {
        Validate.isTrue(username == null && password == null, "Only anonymous access is allowed with the no-security configuration");
        return new UserAuthentication(createAnonymousUserContext());
    }

    @Override
    public UserAuthentication authenticateAnonymous(User anonymous) {
        return new UserAuthentication(new UserContext(anonymous.getName(), anonymous.getRoles()));
    }

    @Override
    public void openSecurityContext(UserAuthentication userAuthentication) {
    }

    @Override
    public void closeSecurityContext() {
    }

    @Override
    public boolean canCallAction(UserContext userContext, ActionAdapter actionAdapter) {
        return true;
    }

    @Override
    public boolean canSendEvent(UserContext userContext, String eventName) {
        return true;
    }

    @Override
    public boolean canSubscribeEvent(UserContext userContext, String eventName) {
        return true;
    }

    @Override
    public boolean canUseKnowledgeBase(UserContext userContext, KnowledgeBase knowledgeBase) {
        return true;
    }
}
