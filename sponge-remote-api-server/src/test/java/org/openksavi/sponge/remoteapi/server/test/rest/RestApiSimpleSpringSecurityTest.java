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

package org.openksavi.sponge.remoteapi.server.test.rest;

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
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.ErrorResponseException;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
import org.openksavi.sponge.restapi.server.security.RestApiSecurityService;
import org.openksavi.sponge.restapi.server.security.spring.SimpleSpringInMemorySecurityService;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RestApiSimpleSpringSecurityTest.TestConfig.class })
@DirtiesContext
public class RestApiSimpleSpringSecurityTest {

    @Inject
    protected SpongeEngine engine;

    @Inject
    @Named(PortTestConfig.PORT_BEAN_NAME)
    protected Integer port;

    @Configuration
    public static class TestConfig extends PortTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin(), spongeGrpcApiPlugin())
                    .config("examples/remote-api-server/rest_api_security.xml").build();
        }

        @Bean
        public RestApiServerPlugin spongeRestApiPlugin() {
            RestApiServerPlugin plugin = new RestApiServerPlugin();

            plugin.getSettings().setPort(spongeRestApiPort());
            plugin.getSettings().setAllowAnonymous(false);
            plugin.getSettings().setPublishReload(true);
            plugin.setSecurityService(restApiSecurityService());

            return plugin;
        }

        @Bean
        public GrpcApiServerPlugin spongeGrpcApiPlugin() {
            return new GrpcApiServerPlugin();
        }

        @Bean
        public RestApiSecurityService restApiSecurityService() {
            return new SimpleSpringInMemorySecurityService();
        }
    }

    protected SpongeRestClient createRestClient(String username, String password) {
        return new DefaultSpongeRestClient(SpongeRestClientConfiguration.builder().url(String.format("http://localhost:%d", port))
                .username(username).password(password).build());
    }

    protected void doTestRestActions(String username, String password, int actionCount) {
        try (SpongeRestClient client = createRestClient(username, password)) {
            List<RestActionMeta> actions = client.getActions();

            assertNotNull(actions);
            assertEquals(actionCount, actions.size());
        }
    }

    @Test
    public void testRestActionsUser1() {
        doTestRestActions("john", "password", RestApiTestConstants.ADMIN_ACTIONS_COUNT);
    }

    @Test
    public void testRestActionsUser2() {
        doTestRestActions("joe", "password", RestApiTestConstants.ANONYMOUS_ACTIONS_COUNT);
    }

    @Test
    public void testLogin() {
        // Tests auth token authentication.
        try (SpongeRestClient client = createRestClient("john", "password")) {
            assertNotNull(client.login());
            assertEquals(RestApiTestConstants.ADMIN_ACTIONS_COUNT, client.getActions().size());

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
        try (SpongeRestClient client = createRestClient("john", "password")) {
            client.logout();
        }
    }

    @Test
    public void testKnowledgeBasesUser1() {
        try (SpongeRestClient client = createRestClient("john", "password")) {
            assertEquals(4, client.getKnowledgeBases().size());
        }
    }

    @Test
    public void testKnowledgeBasesUser2() {
        try (SpongeRestClient client = createRestClient("joe", "password")) {
            assertEquals(1, client.getKnowledgeBases().size());
        }
    }

    @Test
    public void testReloadUser1() {
        try (SpongeRestClient client = createRestClient("john", "password")) {
            client.reload();

            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "reloaded").get());
        }
    }

    @Test
    public void testReloadUser2() {
        try (SpongeRestClient client = createRestClient("joe", "password")) {
            assertThrows(ErrorResponseException.class, () -> client.reload());
        }
    }

    @Test
    public void testAutoUseAuthTokenTrue() {
        try (SpongeRestClient client = createRestClient("joe", "password")) {
            client.getConfiguration().setAutoUseAuthToken(true);

            assertNull(client.getCurrentAuthToken());
            client.getActions();
            assertNotNull(client.getCurrentAuthToken());
        }
    }

    @Test
    public void testAutoUseAuthTokenFalse() {
        try (SpongeRestClient client = createRestClient("joe", "password")) {
            client.getConfiguration().setAutoUseAuthToken(false);

            assertNull(client.getCurrentAuthToken());
            client.getActions();
            assertNull(client.getCurrentAuthToken());
        }
    }
}
