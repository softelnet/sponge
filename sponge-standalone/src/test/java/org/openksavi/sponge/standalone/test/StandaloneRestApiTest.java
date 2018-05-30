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

import java.util.Arrays;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.model.RestActionCall;
import org.openksavi.sponge.restapi.model.RestCallResult;
import org.openksavi.sponge.standalone.StandaloneEngineMain;

public class StandaloneRestApiTest {

    private static final int PORT = 11836;

    private static final String REST_API_URL = "http://localhost:" + PORT + RestApiConstants.BASE_URL + "/";

    @Test
    public void testRestApi() {
        StandaloneEngineMain engineMain = null;
        try {
            engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-c", "examples/standalone/rest_api/standalone_rest_api.xml");
            SpongeEngine engine = engineMain.getEngine();

            String actionName = "UpperCase";
            String arg1 = "test1";

            ResponseEntity<RestCallResult> response = new RestTemplate().exchange(REST_API_URL + "call", HttpMethod.POST,
                    new HttpEntity<>(new RestActionCall(actionName, Arrays.asList(arg1)), createHeaders()), RestCallResult.class);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(actionName, response.getBody().getActionName());
            assertTrue(response.getBody().getResult() instanceof String);
            assertEquals(arg1.toUpperCase(), response.getBody().getResult());

            assertFalse(engine.isError());
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
