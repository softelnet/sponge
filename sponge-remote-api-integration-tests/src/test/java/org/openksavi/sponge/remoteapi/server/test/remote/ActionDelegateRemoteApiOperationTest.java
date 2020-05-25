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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.Arrays;

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
import org.openksavi.sponge.remoteapi.client.SpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClientConfiguration;
import org.openksavi.sponge.remoteapi.server.ActionDelegateOperation;
import org.openksavi.sponge.remoteapi.server.RemoteApiRouteBuilder;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.security.spring.SimpleSpringInMemorySecurityProvider;
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.remoteapi.server.test.remote.delegate.UpperCaseRequest;
import org.openksavi.sponge.remoteapi.server.test.remote.delegate.UpperCaseRequest.UpperCaseParams;
import org.openksavi.sponge.remoteapi.server.test.remote.delegate.UpperCaseResponse;
import org.openksavi.sponge.remoteapi.server.test.remote.delegate.UpperCaseResponse.UpperCaseResult;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ActionDelegateRemoteApiOperationTest.TestConfig.class })
@DirtiesContext
public class ActionDelegateRemoteApiOperationTest {

    public static final String METHOD = "upperCase";

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
            plugin.getSettings().setAllowAnonymous(true);
            plugin.getSettings().setAuthTokenExpirationDuration(Duration.ofSeconds(2));

            plugin.setSecurityProvider(new SimpleSpringInMemorySecurityProvider());

            plugin.setRouteBuilder(new ActionDelegateRemoteApiOperationTestRouteBuilder());

            return plugin;
        }

        @Bean
        public GrpcApiServerPlugin spongeGrpcApiPlugin() {
            return new GrpcApiServerPlugin();
        }
    }

    protected SpongeClient createClient() {
        return new DefaultSpongeClient(SpongeClientConfiguration.builder().url(String.format("http://localhost:%d", port)).build());
    }

    @Test
    public void testActionDelegateOperation() throws InterruptedException {
        String text = "TeXt";

        try (SpongeClient client = createClient()) {
            UpperCaseResponse response = client.execute(new UpperCaseRequest(new UpperCaseParams(text)), UpperCaseResponse.class);

            assertEquals(text.toUpperCase(), response.getResult().getValue());
        }
    }

    static class ActionDelegateRemoteApiOperationTestRouteBuilder extends RemoteApiRouteBuilder {

        @Override
        protected void createCustomOperations() {
            addOperation(ActionDelegateOperation.<UpperCaseRequest, UpperCaseParams, UpperCaseResponse, String>builder().method(METHOD)
                    .requestClass(UpperCaseRequest.class).requestParamsClass(UpperCaseParams.class).requestDescription("Text")
                    .responseClass(UpperCaseResponse.class).responseDescription("Result")
                    .argsMapper(request -> Arrays.asList(request.getParams().getText()))
                    .resultMapper((response, result) -> response.setResult(new UpperCaseResult(result))).build());
        }
    }
}
