/*
 * Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.tensorflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openksavi.sponge.camel.SpongeCamelConfiguration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.remoteapi.client.DefaultSpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClientConfiguration;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;
import org.openksavi.sponge.test.util.TestUtils;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { DigitsServerTest.TestConfig.class })
@DirtiesContext
public class DigitsServerTest {

    protected static final int PORT = TestUtils.findAvailablePairOfNeighbouringTcpPorts();

    @Inject
    protected SpongeEngine engine;

    @Configuration
    public static class TestConfig extends SpongeCamelConfiguration {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRemoteApiPlugin())
                    .config("examples/tensorflow/digits/digits_remote_server_test.xml").build();
        }

        @Bean
        public RemoteApiServerPlugin spongeRemoteApiPlugin() {
            RemoteApiServerPlugin plugin = new RemoteApiServerPlugin();
            plugin.getSettings().setPort(PORT);

            return plugin;
        }
    }

    protected SpongeClient createClient() {
        return new DefaultSpongeClient(SpongeClientConfiguration.builder().url(String.format("http://localhost:%d", PORT)).build());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoteCallPredict() {
        byte[] imageData = SpongeUtils.readFileToByteArray("examples/tensorflow/digits/data/5_0.png");

        try (SpongeClient client = createClient()) {
            Map<String, Number> result = client.call(Map.class, "DigitsPredictProbabilities", Arrays.asList(imageData));
            int prediction = Integer.parseInt(result.entrySet().stream()
                    .reduce((i, j) -> i.getValue().doubleValue() >= j.getValue().doubleValue() ? i : j).map(Map.Entry::getKey).get());

            assertEquals(5, prediction);
            assertFalse(engine.isError());
        }
    }
}
