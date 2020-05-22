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

package org.openksavi.sponge.remoteapi.server.test.remote;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.remoteapi.client.DefaultSpongeClient;
import org.openksavi.sponge.remoteapi.client.ErrorResponseException;
import org.openksavi.sponge.remoteapi.client.SpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClientConfiguration;
import org.openksavi.sponge.remoteapi.model.RemoteActionMeta;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.security.spring.SimpleSpringInMemorySecurityProvider;
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RemoteApiSimpleSpringSecurityTest.TestConfig.class })
@DirtiesContext
public class RemoteApiSimpleSpringSecurityTest {

    @Inject
    protected SpongeEngine engine;

    @Inject
    @Named(PortTestConfig.PORT_BEAN_NAME)
    protected Integer port;

    @Configuration
    public static class TestConfig extends PortTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRemoteApiPlugin(), spongeGrpcApiPlugin())
                    .config("examples/remote-api-server/remote_api_security.xml").build();
        }

        @Bean
        public RemoteApiServerPlugin spongeRemoteApiPlugin() {
            RemoteApiServerPlugin plugin = new RemoteApiServerPlugin();

            plugin.getSettings().setPort(spongeRemoteApiPort());
            plugin.getSettings().setAllowAnonymous(false);
            plugin.getSettings().setPublishReload(true);

            plugin.setSecurityProvider(new SimpleSpringInMemorySecurityProvider());

            return plugin;
        }

        @Bean
        public GrpcApiServerPlugin spongeGrpcApiPlugin() {
            return new GrpcApiServerPlugin();
        }

    }

    protected SpongeClient createClient(String username, String password) {
        return new DefaultSpongeClient(SpongeClientConfiguration.builder().url(String.format("http://localhost:%d", port))
                .username(username).password(password).build());
    }

    protected void doTestRemoteActions(String username, String password, int actionCount) {
        try (SpongeClient client = createClient(username, password)) {
            List<RemoteActionMeta> actions = client.getActions();

            assertNotNull(actions);
            assertEquals(actionCount, actions.size());
        }
    }

    @Test
    public void testRemoteActionsUser1() {
        doTestRemoteActions("john", "password", RemoteApiTestConstants.ADMIN_ACTIONS_COUNT);
    }

    @Test
    public void testRemoteActionsUser2() {
        doTestRemoteActions("joe", "password", RemoteApiTestConstants.ANONYMOUS_ACTIONS_COUNT);
    }

    @Test
    public void testLogin() {
        // Tests auth token authentication.
        try (SpongeClient client = createClient("john", "password")) {
            assertNotNull(client.login());
            assertEquals(RemoteApiTestConstants.ADMIN_ACTIONS_COUNT, client.getActions().size());

            client.getConfiguration().setUsername(null);
            client.getConfiguration().setPassword(null);
            client.logout();

            try {
                // Try to get actions as anonymous (which is not allowed in the server configuration).
                client.getActions();
                fail("Exception expected");
            } catch (ErrorResponseException e) {
                // This is OK.
            }
        }
    }

    @Test
    public void testLogout() {
        // Auth token disabled.
        try (SpongeClient client = createClient("john", "password")) {
            client.logout();
        }
    }

    @Test
    public void testKnowledgeBasesUser1() {
        try (SpongeClient client = createClient("john", "password")) {
            assertEquals(4, client.getKnowledgeBases().size());
        }
    }

    @Test
    public void testKnowledgeBasesUser2() {
        try (SpongeClient client = createClient("joe", "password")) {
            assertEquals(1, client.getKnowledgeBases().size());
        }
    }

    @Test
    public void testReloadUser1() {
        try (SpongeClient client = createClient("john", "password")) {
            client.reload();

            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "reloaded").get());
        }
    }

    @Test
    public void testReloadUser2() {
        try (SpongeClient client = createClient("joe", "password")) {
            assertThrows(ErrorResponseException.class, () -> client.reload());
        }
    }

    @Test
    public void testAutoUseAuthTokenTrue() {
        try (SpongeClient client = createClient("joe", "password")) {
            client.getConfiguration().setAutoUseAuthToken(true);

            assertNull(client.getCurrentAuthToken());
            client.getActions();
            assertNotNull(client.getCurrentAuthToken());
        }
    }

    @Test
    public void testAutoUseAuthTokenFalse() {
        try (SpongeClient client = createClient("joe", "password")) {
            client.getConfiguration().setAutoUseAuthToken(false);

            assertNull(client.getCurrentAuthToken());
            client.getActions();
            assertNull(client.getCurrentAuthToken());
        }
    }
}
