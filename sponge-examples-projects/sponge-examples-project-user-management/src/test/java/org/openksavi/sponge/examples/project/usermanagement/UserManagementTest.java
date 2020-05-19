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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import org.openksavi.sponge.remoteapi.client.DefaultSpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClientConfiguration;
import org.openksavi.sponge.remoteapi.model.RemoteActionMeta;
import org.openksavi.sponge.remoteapi.test.base.RemoteApiTestEnvironment;
import org.openksavi.sponge.test.util.TestUtils;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
public class UserManagementTest {

    protected static final int PORT = TestUtils.findAvailablePairOfNeighbouringTcpPorts();

    protected static final RemoteApiTestEnvironment environment = new RemoteApiTestEnvironment();

    @BeforeAll
    public static void beforeClass() {
        environment.init();
    }

    @AfterAll
    public static void afterClass() {
        environment.clear();
    }

    @BeforeEach
    public void start() {
        environment.start(PORT);
    }

    @AfterEach
    public void stop() {
        environment.stop();
    }

    protected SpongeClient createClient() {
        return new DefaultSpongeClient(SpongeClientConfiguration.builder().url(String.format("http://localhost:%d", PORT)).build());
    }

    @Test
    public void testUserManagement() {
        String signUpActionName = "SignUpWithEmail";

        try (SpongeClient client = createClient()) {
            // Anonymous.
            List<RemoteActionMeta> anonymousActions = client.getActions();
            assertEquals(2, anonymousActions.size());

            RemoteActionMeta loginAction = anonymousActions.stream()
                    .filter(action -> Objects.equals(action.getFeatures().get("intent"), "login")).findFirst().get();
            assertEquals("Login", loginAction.getName());

            RemoteActionMeta signUpAction = anonymousActions.stream()
                    .filter(action -> Objects.equals(action.getFeatures().get("intent"), "signUp")).findFirst().get();
            assertEquals(signUpActionName, signUpAction.getName());

            client.getConfiguration().setUsername("user1@openksavi.org");
            client.getConfiguration().setPassword("password");
            client.call("Login", Arrays.asList(client.getConfiguration().getUsername(), client.getConfiguration().getPassword()));

            // Logged.
            List<RemoteActionMeta> loggedActions = client.getActions();
            assertEquals(5, loggedActions.size());
            RemoteActionMeta logoutAction =
                    loggedActions.stream().filter(action -> Objects.equals(action.getFeatures().get("intent"), "logout")).findFirst().get();
            assertEquals("Logout", logoutAction.getName());

            assertEquals("TEXT", client.call(String.class, "UpperCase", Arrays.asList("text")));

            client.call("Logout");
            client.getConfiguration().setUsername(null);
            client.getConfiguration().setPassword(null);

            // Anonymous.
            anonymousActions = client.getActions();
            assertEquals(2, anonymousActions.size());

            loginAction = anonymousActions.stream().filter(action -> Objects.equals(action.getFeatures().get("intent"), "login"))
                    .findFirst().get();
            assertEquals("Login", loginAction.getName());

            signUpAction = anonymousActions.stream().filter(action -> Objects.equals(action.getFeatures().get("intent"), "signUp"))
                    .findFirst().get();
            assertEquals(signUpActionName, signUpAction.getName());

            // Sign up.
            client.call(signUpActionName, Arrays.asList("user2@openksavi.org", "John", "Smith", "password", "password"));

            // Login as a new user.
            client.getConfiguration().setUsername("user2@openksavi.org");
            client.getConfiguration().setPassword("password");
            client.call("Login", Arrays.asList(client.getConfiguration().getUsername(), client.getConfiguration().getPassword()));

            assertEquals(5, client.getActions().size());
        }
    }
}
