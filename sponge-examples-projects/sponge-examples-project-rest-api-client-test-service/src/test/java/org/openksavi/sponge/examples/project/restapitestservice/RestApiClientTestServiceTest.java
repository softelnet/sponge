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

package org.openksavi.sponge.examples.project.restapitestservice;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.test.base.RemoteApiTestEnvironment;
import org.openksavi.sponge.test.util.TestUtils;

@net.jcip.annotations.NotThreadSafe
public class RestApiClientTestServiceTest {

    protected static final int PORT = TestUtils.findAvailablePairOfNeighbouringTcpPorts();

    protected static final RemoteApiTestEnvironment environment = new RemoteApiTestEnvironment();

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
    public void testActionUppercase() {
        try (SpongeRestClient client = createRestClient()) {
            assertEquals("TEXT TO UPPERCASE", client.call("UpperCase", Arrays.asList("Text to uppercase")));
        }
    }
}
