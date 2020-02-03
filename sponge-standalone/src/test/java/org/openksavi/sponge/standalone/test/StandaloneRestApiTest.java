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

package org.openksavi.sponge.standalone.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.standalone.StandaloneEngineMain;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
public class StandaloneRestApiTest {

    private static final int PORT = 11836;

    @Test
    public void testRestApi() {
        StandaloneEngineMain engineMain = null;
        try {
            engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-c", "examples/standalone/rest_api/standalone_rest_api.xml");
            SpongeEngine engine = engineMain.getEngine();

            String arg1 = "test1";

            try (SpongeRestClient client = new DefaultSpongeRestClient(
                    SpongeRestClientConfiguration.builder().url(String.format("http://localhost:%d", PORT)).build())) {
                Object result = client.call("UpperCase", Arrays.asList(arg1));

                assertTrue(result instanceof String);
                assertEquals(arg1.toUpperCase(), result);
            }

            assertFalse(engine.isError());
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }
}
