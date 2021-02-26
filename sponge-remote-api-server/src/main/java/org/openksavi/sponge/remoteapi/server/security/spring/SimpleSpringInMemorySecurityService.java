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

package org.openksavi.sponge.remoteapi.server.security.spring;

import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.openksavi.sponge.remoteapi.server.InvalidUsernamePasswordServerException;
import org.openksavi.sponge.remoteapi.server.security.BaseInMemorySecurityService;
import org.openksavi.sponge.remoteapi.server.security.User;
import org.openksavi.sponge.remoteapi.server.security.UserAuthentication;
import org.openksavi.sponge.remoteapi.server.security.UserAuthenticationQuery;
import org.openksavi.sponge.remoteapi.server.security.UserContext;

public class SimpleSpringInMemorySecurityService extends BaseInMemorySecurityService {

    private AuthenticationManager authenticationManager = new SimpleAuthenticationManager();

    public SimpleSpringInMemorySecurityService() {
        //
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserAuthentication authenticateUser(UserAuthenticationQuery query) {
        String username = query.getUsername() != null ? query.getUsername().toLowerCase() : null;

        Validate.isTrue(username != null && query.getPassword() != null, "The %s requires username and password", getClass());

        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, query.getPassword()));
            UserContext userContext = new UserContext(username,
                    authentication.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList()));

            return new SpringUserAuthentication(userContext, authentication);
        } catch (AuthenticationException e) {
            throw new InvalidUsernamePasswordServerException("Incorrect username or password", e);
        }
    }

    @Override
    public UserAuthentication authenticateAnonymous(User anonymous) {
        return new SpringUserAuthentication(new UserContext(anonymous.getName(), anonymous.getRoles()),
                createAuthentication(anonymous));
    }

    @Override
    public void openSecurityContext(UserAuthentication userAuthentication) {
        Validate.isTrue(userAuthentication instanceof SpringUserAuthentication, "The user authentication class should extend %s",
                SpringUserAuthentication.class);
        SpringUserAuthentication customUserAuthentication = (SpringUserAuthentication) userAuthentication;
        if (customUserAuthentication.getAuthentication() != null) {
            SecurityContextHolder.getContext().setAuthentication(customUserAuthentication.getAuthentication());
        }
    }

    @Override
    public void closeSecurityContext() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    protected Authentication createAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList()));
    }

    class SimpleAuthenticationManager implements AuthenticationManager {

        @Override
        public Authentication authenticate(Authentication auth) throws AuthenticationException {
            User user = verifyInMemory(String.valueOf(auth.getPrincipal()), String.valueOf(auth.getCredentials()));

            if (user != null) {
                return createAuthentication(user);
            }

            throw new BadCredentialsException("Incorrect username or password");
        }
    }
}
