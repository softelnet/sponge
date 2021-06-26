/*
 * Copyright 2016-2020 The Sponge authors.
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

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.remoteapi.model.request.RequestHeader;
import org.openksavi.sponge.remoteapi.model.request.SpongeRequest;
import org.openksavi.sponge.remoteapi.server.RemoteApiService;
import org.openksavi.sponge.remoteapi.server.util.RemoteApiServerUtils;

@SuppressWarnings("rawtypes")
public class DefaultRequestAuthenticationService extends BaseRequestAuthenticationService {

    @Override
    public UserAuthentication authenticateRequest(SpongeRequest request) {
        RemoteApiService apiService = getRemoteApiService();

        RequestHeader header = request.getHeader();

        if (header.getAuthToken() != null) {
            Validate.isTrue(header.getUsername() == null, "Username is not allowed when using a token-based auhentication");
            Validate.isTrue(header.getPassword() == null, "Password is not allowed when using a token-based auhentication");

            return Validate.notNull(apiService.getAuthTokenService(), "Auth token service not configured")
                    .validateAuthToken(header.getAuthToken());
        }

        if (header.getUsername() != null) {
            return apiService.getSecurityService().authenticateUser(new UserAuthenticationQuery(header.getUsername(), header.getPassword(),
                    header.getAuthToken(), apiService.getSession()));
        }

        if (apiService.getSettings().isAllowAnonymous()) {
            return apiService.getSecurityService()
                    .authenticateAnonymous(RemoteApiServerUtils.createAnonymousUser(apiService.getSettings().getAnonymousRole()));
        } else {
            throw new SpongeException("Anonymous access is not allowed");
        }
    }
}
