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
import org.openksavi.sponge.remoteapi.model.request.SpongeRequest;
import org.openksavi.sponge.remoteapi.server.RemoteApiService;
import org.openksavi.sponge.remoteapi.server.util.RemoteApiServerUtils;

public class DefaultRequestAuthenticationService extends BaseRequestAuthenticationService {

    @Override
    public UserAuthentication authenticateRequest(SpongeRequest request) {
        RemoteApiService apiService = getRemoteApiService();

        if (request.getHeader().getAuthToken() != null) {
            Validate.isTrue(request.getHeader().getUsername() == null, "Username is not allowed when using a token-based auhentication");
            Validate.isTrue(request.getHeader().getPassword() == null, "Password is not allowed when using a token-based auhentication");

            return Validate.notNull(apiService.getAuthTokenService(), "Auth token service not configured")
                    .validateAuthToken(request.getHeader().getAuthToken());
        } else {
            if (request.getHeader().getUsername() == null) {
                if (apiService.getSettings().isAllowAnonymous()) {
                    return apiService.getSecurityService()
                            .authenticateAnonymous(RemoteApiServerUtils.createAnonymousUser(apiService.getSettings().getAnonymousRole()));
                } else {
                    throw new SpongeException("Anonymous access is not allowed");
                }
            } else {
                return apiService.getSecurityService().authenticateUser(new UserAuthenticationQuery(request.getHeader().getUsername(),
                        request.getHeader().getPassword(), request.getHeader().getAuthToken(), apiService.getSession()));
            }
        }
    }
}
