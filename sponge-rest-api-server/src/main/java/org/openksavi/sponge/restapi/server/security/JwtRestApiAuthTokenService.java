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

import java.security.Key;
import java.util.HashSet;
import java.util.Set;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import org.apache.camel.Exchange;

import org.openksavi.sponge.Experimental;
import org.openksavi.sponge.restapi.RestApiInvalidAuthTokenException;

/**
 * AN auth token service that uses JSON Web Token (JWT).
 */
@Experimental
public class JwtRestApiAuthTokenService extends BaseRestApiAuthTokenService {

    protected static final String CLAIM_USERNAME = "username";

    // TODO cache keys for each session?
    private Key key = MacProvider.generateKey();

    private Set<String> authTokens = new HashSet<>();

    @Override
    public String createAuthToken(User user, Exchange exchange) {
        String token = Jwts.builder().setSubject(getEngine().getInfo()).claim(CLAIM_USERNAME, user.getName())
                .signWith(SignatureAlgorithm.HS512, key).compressWith(CompressionCodecs.DEFLATE).compact();
        authTokens.add(token);

        return token;
    }

    @Override
    public String validateAuthToken(String authToken, Exchange exchange) {
        if (!authTokens.contains(authToken)) {
            throw new RestApiInvalidAuthTokenException("Unknown auth token");
        }

        Jws<Claims> claims = Jwts.parser().setSigningKey(key).requireSubject(getEngine().getInfo()).parseClaimsJws(authToken);
        String username = claims.getBody().get(CLAIM_USERNAME, String.class);

        // TODO Verify expiration claims.getBody().getExpiration()

        return username;
    }

    @Override
    public void removeAuthToken(String authToken, Exchange exchange) {
        authTokens.remove(authToken);
    }
}
