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

import org.openksavi.sponge.core.util.LocalCache;
import org.openksavi.sponge.core.util.LocalCacheBuilder;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.remoteapi.server.RemoteApiInvalidAuthTokenServerException;

/**
 * AN auth token service that uses JSON Web Token (JWT).
 */
public class JwtRemoteApiAuthTokenService extends BaseRemoteApiAuthTokenService {

    protected static final String CLAIM_AUTH_SESSION_ID = "authSessionId";

    protected static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    private Key key = MacProvider.generateKey(SIGNATURE_ALGORITHM);

    private LocalCache<Long, AuthTokenSession> authTokenSessions;

    private AtomicLong currentAuthSessionId = new AtomicLong(0);

    protected static class AuthTokenSession {

        private UserAuthentication userAuthentication;

        private Instant creationTime;

        public AuthTokenSession(UserAuthentication userAuthentication) {
            this.userAuthentication = userAuthentication;
            creationTime = Instant.now();
        }

        public UserAuthentication getUserAuthentication() {
            return userAuthentication;
        }

        public Instant getCreationTime() {
            return creationTime;
        }
    }

    @Override
    public void init() {
        super.init();

        Duration expirationDuration = getRemoteApiService().getSettings().getAuthTokenExpirationDuration();
        if (expirationDuration != null && (expirationDuration.isZero() || expirationDuration.isNegative())) {
            expirationDuration = null;
        }

        LocalCacheBuilder cacheBuilder = SpongeUtils.cacheBuilder();
        if (expirationDuration != null) {
            cacheBuilder.expireAfterAccess(expirationDuration);
        }
        authTokenSessions = cacheBuilder.build();
    }

    @Override
    public String createAuthToken(UserAuthentication userAuthentication) {
        Long authSessionId = currentAuthSessionId.incrementAndGet();

        JwtBuilder builder = Jwts.builder();
        builder.claim(CLAIM_AUTH_SESSION_ID, authSessionId).signWith(SIGNATURE_ALGORITHM, key).compressWith(CompressionCodecs.DEFLATE);
        String token = builder.compact();

        authTokenSessions.put(authSessionId, new AuthTokenSession(userAuthentication));

        return token;
    }

    @Override
    public UserAuthentication validateAuthToken(String authToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
            Long authSessionId = claims.getBody().get(CLAIM_AUTH_SESSION_ID, Long.class);

            if (authSessionId == null) {
                throw new RemoteApiInvalidAuthTokenServerException("Invalid or expired authentication token");
            }

            AuthTokenSession authSession = authTokenSessions.getIfPresent(authSessionId);
            if (authSession == null) {
                throw new RemoteApiInvalidAuthTokenServerException("Invalid or expired authentication token");
            }

            return authSession.getUserAuthentication();
        } catch (JwtException e) {
            throw new RemoteApiInvalidAuthTokenServerException(e.getMessage(), e);
        }
    }

    @Override
    public void removeAuthToken(String authToken) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
        Long authSessionId = claims.getBody().get(CLAIM_AUTH_SESSION_ID, Long.class);

        if (authSessionId != null) {
            authTokenSessions.invalidate(authSessionId);
        }
    }
}
