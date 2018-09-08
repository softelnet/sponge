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

package org.openksavi.sponge.examples.project.demoservice;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.SocketUtils;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestApiClient;
import org.openksavi.sponge.restapi.client.RestApiClientConfiguration;
import org.openksavi.sponge.restapi.client.SpongeRestApiClient;

@net.jcip.annotations.NotThreadSafe
public class DemoServiceTest {

    protected static final int PORT = SocketUtils.findAvailableTcpPort(RestApiConstants.DEFAULT_PORT);

    protected static final DemoServiceTestEnvironment environment = new DemoServiceTestEnvironment();

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

    protected SpongeRestApiClient createRestApiClient() {
        return new DefaultSpongeRestApiClient(RestApiClientConfiguration.builder()
                .url(String.format("http://localhost:%d/%s", PORT, RestApiConstants.DEFAULT_PATH)).build());
    }

    @Test
    public void testRestCallPredict() {
        byte[] imageData = SpongeUtils.readFileToByteArray(
                Paths.get(System.getProperty(DemoServiceTestEnvironment.PROPERTY_MNIST_HOME), "data/5_0.png").toString());

        assertEquals(5, createRestApiClient().call(Number.class, "MnistPredict", imageData));
    }
}
