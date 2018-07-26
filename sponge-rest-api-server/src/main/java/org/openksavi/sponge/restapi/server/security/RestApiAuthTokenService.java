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

import org.openksavi.sponge.restapi.server.HasRestApiService;
import org.openksavi.sponge.util.Initializable;

public interface RestApiAuthTokenService extends HasRestApiService, Initializable {

    /**
     * Creates a new auth token.
     *
     * @param user the user.
     * @param exchange the exchange.
     * @return the auth token.
     */
    String createAuthToken(User user, Exchange exchange);

    /**
     * Validates the auth token. Throws exception if it is invalid.
     *
     * @param authToken the auth token,
     * @param exchange the exchange.
     * @return the authenticated user name.
     */
    String validateAuthToken(String authToken, Exchange exchange);

    void removeAuthToken(String authToken, Exchange exchange);
}
