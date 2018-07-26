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
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import org.apache.camel.Exchange;

import org.openksavi.sponge.core.util.LocalCache;
import org.openksavi.sponge.core.util.LocalCacheBuilder;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.restapi.server.RestApiInvalidAuthTokenServerException;

/**
 * AN auth token service that uses JSON Web Token (JWT).
 */
public class JwtRestApiAuthTokenService extends BaseRestApiAuthTokenService {

    protected static final String CLAIM_AUTH_SESSION_ID = "authSessionId";

    private Key key = MacProvider.generateKey();

    private LocalCache<Long, AuthTokenSession> authTokenSessions;

    private AtomicLong currentAuthSessionId = new AtomicLong(0);

    protected static class AuthTokenSession {

        private String username;

        private Instant creationTime;

        public AuthTokenSession(String username) {
            this.username = username;
            creationTime = Instant.now();
        }

        public String getUsername() {
            return username;
        }

        public Instant getCreationTime() {
            return creationTime;
        }
    }

    @Override
    public void init() {
        super.init();

        Duration expirationDuration = getRestApiService().getSettings().getAuthTokenExpirationDuration();

        LocalCacheBuilder cacheBuilder = SpongeUtils.cacheBuilder();
        if (expirationDuration != null) {
            cacheBuilder.expireAfterAccess(expirationDuration);
        }
        authTokenSessions = cacheBuilder.build();
    }

    @Override
    public String createAuthToken(User user, Exchange exchange) {
        Long authSessionId = currentAuthSessionId.incrementAndGet();

        JwtBuilder builder = Jwts.builder();
        builder.setSubject(user.getName()).claim(CLAIM_AUTH_SESSION_ID, authSessionId).signWith(SignatureAlgorithm.HS512, key)
                .compressWith(CompressionCodecs.DEFLATE);
        String token = builder.compact();

        authTokenSessions.put(authSessionId, new AuthTokenSession(user.getName()));

        return token;
    }

    @Override
    public String validateAuthToken(String authToken, Exchange exchange) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
            String username = claims.getBody().getSubject();
            Long authSessionId = claims.getBody().get(CLAIM_AUTH_SESSION_ID, Long.class);

            if (authSessionId == null || authTokenSessions.getIfPresent(authSessionId) == null) {
                throw new RestApiInvalidAuthTokenServerException("Invalid or expired authentication token");
            }

            return username;
        } catch (JwtException e) {
            throw new RestApiInvalidAuthTokenServerException(e.getMessage(), e);
        }
    }

    @Override
    public void removeAuthToken(String authToken, Exchange exchange) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
        Long authSessionId = claims.getBody().get(CLAIM_AUTH_SESSION_ID, Long.class);

        if (authSessionId != null) {
            authTokenSessions.invalidate(authSessionId);
        }
    }
}
