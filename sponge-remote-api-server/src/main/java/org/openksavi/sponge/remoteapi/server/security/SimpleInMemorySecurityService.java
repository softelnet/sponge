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

import org.openksavi.sponge.remoteapi.server.InvalidUsernamePasswordServerException;

public class SimpleInMemorySecurityService extends BaseInMemorySecurityService {

    public SimpleInMemorySecurityService() {
        //
    }

    @Override
    public UserAuthentication authenticateUser(UserAuthenticationQuery query) {
        String username = query.getUsername() != null ? query.getUsername().toLowerCase() : null;

        Validate.isTrue(username != null && query.getPassword() != null, "The %s requires username and password", getClass());

        User user = verifyInMemory(username, query.getPassword());
        if (user == null) {
            throw new InvalidUsernamePasswordServerException("Invalid username or password");
        }

        return new UserAuthentication(new UserContext(user.getName(), user.getRoles()));
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
}
