/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.examples.project.usermanagement;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.SocketUtils;

import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.model.RestActionMeta;

@net.jcip.annotations.NotThreadSafe
public class UserManagementTest {

    protected static final int PORT = SocketUtils.findAvailableTcpPort(RestApiConstants.DEFAULT_PORT);

    protected static final UserManagementTestEnvironment environment = new UserManagementTestEnvironment();

    @BeforeClass
    public static void beforeClass() {
        environment.init();
    }

    @AfterClass
    public static void afterClass() {
        environment.clear();
    }

    @Before
    public void start() {
        environment.start(PORT);
    }

    @After
    public void stop() {
        environment.stop();
    }

    protected SpongeRestClient createRestClient() {
        return new DefaultSpongeRestClient(SpongeRestClientConfiguration.builder()
                .url(String.format("http://localhost:%d/%s", PORT, RestApiConstants.DEFAULT_PATH)).build());
    }

    @Test
    public void testUserManagement() {
        try (SpongeRestClient client = createRestClient()) {
            // Anonymous.
            List<RestActionMeta> anonymousActions = client.getActions();
            assertEquals(3, anonymousActions.size());

            RestActionMeta loginAction = anonymousActions.stream()
                    .filter(action -> Objects.equals(action.getFeatures().get("intent"), "login")).findFirst().get();
            assertEquals("Login", loginAction.getName());

            RestActionMeta signUpAction = anonymousActions.stream()
                    .filter(action -> Objects.equals(action.getFeatures().get("intent"), "signUp")).findFirst().get();
            assertEquals("SignUp", signUpAction.getName());

            client.getConfiguration().setUsername("user1@openksavi.org");
            client.getConfiguration().setPassword("password");
            client.call("Login", Arrays.asList(client.getConfiguration().getUsername(), client.getConfiguration().getPassword()));

            // Logged.
            List<RestActionMeta> loggedActions = client.getActions();
            assertEquals(5, loggedActions.size());
            RestActionMeta logoutAction =
                    loggedActions.stream().filter(action -> Objects.equals(action.getFeatures().get("intent"), "logout")).findFirst().get();
            assertEquals("Logout", logoutAction.getName());

            assertEquals("TEXT", client.call(String.class, "UpperCase", Arrays.asList("text")));

            client.call("Logout");
            client.getConfiguration().setUsername(null);
            client.getConfiguration().setPassword(null);

            // Anonymous.
            anonymousActions = client.getActions();
            assertEquals(3, anonymousActions.size());

            loginAction = anonymousActions.stream().filter(action -> Objects.equals(action.getFeatures().get("intent"), "login"))
                    .findFirst().get();
            assertEquals("Login", loginAction.getName());

            signUpAction = anonymousActions.stream().filter(action -> Objects.equals(action.getFeatures().get("intent"), "signUp"))
                    .findFirst().get();
            assertEquals("SignUp", signUpAction.getName());

            // Sign up.
            client.call("SignUp", Arrays.asList("user2@openksavi.org", "John", "Smith", "password", "password"));

            // Login as a new user.
            client.getConfiguration().setUsername("user2@openksavi.org");
            client.getConfiguration().setPassword("password");
            client.call("Login", Arrays.asList(client.getConfiguration().getUsername(), client.getConfiguration().getPassword()));

            assertEquals(5, client.getActions().size());
        }
    }
}
