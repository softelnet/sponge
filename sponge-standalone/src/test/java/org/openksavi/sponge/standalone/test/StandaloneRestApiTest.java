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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestApiClient;
import org.openksavi.sponge.restapi.client.RestApiClientConfiguration;
import org.openksavi.sponge.restapi.client.SpongeRestApiClient;
import org.openksavi.sponge.standalone.StandaloneEngineMain;

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

            try (SpongeRestApiClient client = new DefaultSpongeRestApiClient(RestApiClientConfiguration.builder()
                    .url(String.format("http://localhost:%d/%s", PORT, RestApiConstants.DEFAULT_PATH)).build())) {
                Object result = client.call("UpperCase", arg1);

                assertTrue(result instanceof String);
                assertEquals(arg1.toUpperCase(), result);
            }

            assertFalse(engine.isError());
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }
}
