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

package org.openksavi.sponge.restapi.server.test;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.ResponseErrorSpongeException;
import org.openksavi.sponge.restapi.client.RestApiClientConfiguration;
import org.openksavi.sponge.restapi.client.SpongeRestApiClient;
import org.openksavi.sponge.restapi.client.spring.SpringSpongeRestApiClient;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
import org.openksavi.sponge.restapi.server.security.RestApiSecurityService;
import org.openksavi.sponge.restapi.server.security.spring.SimpleSpringInMemorySecurityService;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@net.jcip.annotations.NotThreadSafe
@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { RestApiSimpleSpringSecurityTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
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
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin())
                    .knowledgeBase("admin", "classpath:org/openksavi/sponge/restapi/server/administration_library.py")
                    .knowledgeBase("example", "examples/rest-api-server/rest_api.py")
                    .knowledgeBase("security", "examples/rest-api-server/rest_api_security.py").build();
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
        public RestApiSecurityService restApiSecurityService() {
            return new SimpleSpringInMemorySecurityService();
        }
    }

    protected SpongeRestApiClient createRestApiClient(String username, String password) {
        return new SpringSpongeRestApiClient(
                RestApiClientConfiguration.builder().url(String.format("http://localhost:%d/%s", port, RestApiConstants.DEFAULT_PATH))
                        .username(username).password(password).build());
    }

    protected void doTestRestActions(String username, String password, int actionCount) {
        List<RestActionMeta> actions = createRestApiClient(username, password).getActions();

        assertNotNull(actions);
        assertEquals(actionCount, actions.size());
    }

    @Test
    public void testRestActionsUser1() {
        doTestRestActions("john", "password", RestApiTestConstants.ALL_ACTION_COUNT);
    }

    @Test
    public void testRestActionsUser2() {
        doTestRestActions("joe", "password", RestApiTestConstants.ANONYMOUS_ACTION_COUNT);
    }

    @Test
    public void testLogin() {
        // Tests auth token authentication.
        SpongeRestApiClient client = createRestApiClient("john", "password");
        assertNotNull(client.login());
        assertEquals(RestApiTestConstants.ALL_ACTION_COUNT, client.getActions().size());

        client.getConfiguration().setUsername(null);
        client.getConfiguration().setPassword(null);
        assertEquals(RestApiTestConstants.ALL_ACTION_COUNT, client.getActions().size());

        client.logout();

        try {
            client.getActions();
            fail("Exception expected");
        } catch (ResponseErrorSpongeException e) {
            // This is OK.
        }
    }

    @Test
    public void testLogout() {
        // Auth token disabled.
        createRestApiClient("john", "password").logout();
    }

    @Test
    public void testKnowledgeBasesUser1() {
        assertEquals(3, createRestApiClient("john", "password").getKnowledgeBases().size());
    }

    @Test
    public void testKnowledgeBasesUser2() {
        assertEquals(1, createRestApiClient("joe", "password").getKnowledgeBases().size());
    }

    @Test
    public void testReloadUser1() {
        createRestApiClient("john", "password").reload();

        await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "reloaded").get());
    }

    @Test(expected = ResponseErrorSpongeException.class)
    public void testReloadUser2() {
        createRestApiClient("joe", "password").reload();
    }
}
