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

package org.openksavi.sponge.app.demoservice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.test.util.TestUtils;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
public class DemoServiceTest {

    protected static final int PORT = TestUtils.findAvailablePairOfNeighbouringTcpPorts();

    protected static final DemoServiceTestEnvironment environment = new DemoServiceTestEnvironment();

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

    protected SpongeRestClient createRestClient() {
        return new DefaultSpongeRestClient(SpongeRestClientConfiguration.builder().url(String.format("http://localhost:%d", PORT)).build());
    }

    @Test
    public void testRestCallPredict() {
        byte[] imageData = SpongeUtils.readFileToByteArray(
                Paths.get(System.getProperty(DemoServiceTestEnvironment.PROPERTY_DIGITS_HOME), "data/5_0.png").toString());

        try (SpongeRestClient client = createRestClient()) {
            assertEquals(5, client.call(Number.class, "DigitsPredict", Arrays.asList(imageData)));
        }
    }
}
