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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.SocketUtils;
import org.springframework.web.client.RestTemplate;

import org.openksavi.sponge.camel.SpongeCamelConfiguration;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.RestApiPlugin;
import org.openksavi.sponge.restapi.model.request.RestActionCallRequest;
import org.openksavi.sponge.restapi.model.response.RestActionCallResponse;
import org.openksavi.sponge.restapi.model.util.RestApiUtils;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { MnistRestTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class MnistRestTest {

    protected static final int PORT = SocketUtils.findAvailableTcpPort(RestApiConstants.DEFAULT_PORT);

    @Inject
    protected SpongeEngine engine;

    @Configuration
    public static class TestConfig extends SpongeCamelConfiguration {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin())
                    .config("examples/tensorflow/mnist/mnist_rest_server_test.xml").build();
        }

        @Bean
        public RestApiPlugin spongeRestApiPlugin() {
            RestApiPlugin plugin = new RestApiPlugin();
            plugin.getSettings().setPort(PORT);

            return plugin;
        }
    }

    protected RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter(RestApiUtils.createObjectMapper())));

        return restTemplate;
    }

    protected final String getUrl() {
        return "http://localhost:" + PORT + RestApiConstants.BASE_URL + "/";
    }

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRestCallPredict() {
        String actionName = "MnistPredict";

        byte[] imageData = MnistUtils.getImageBytes("examples/tensorflow/mnist/data/5_0.png");

        ResponseEntity<RestActionCallResponse> response = createRestTemplate().exchange(getUrl() + "call", HttpMethod.POST,
                new HttpEntity<>(new RestActionCallRequest(actionName, Arrays.asList(imageData)), createHeaders()),
                RestActionCallResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getResult() instanceof List);
        List<Double> predictions =
                ((List<Number>) response.getBody().getResult()).stream().map(e -> e.doubleValue()).collect(Collectors.toList());

        int prediction = IntStream.range(0, predictions.size()).boxed().max(Comparator.comparingDouble(predictions::get)).get();
        assertEquals(5, prediction);
        assertFalse(engine.isError());
    }
}
