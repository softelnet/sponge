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

package org.openksavi.sponge.examples.project.demoservice.test.stability;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.examples.project.demoservice.DemoServiceTestEnvironment;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;

@net.jcip.annotations.NotThreadSafe
public class DemoServiceLoadTest {

    private static final Logger logger = LoggerFactory.getLogger(DemoServiceLoadTest.class);

    protected static final int PORT = SocketUtils.findAvailableTcpPort(RestApiConstants.DEFAULT_PORT);

    protected static final DemoServiceTestEnvironment environment = new DemoServiceTestEnvironment();

    protected static final int THREAD_COUNT = 20;

    protected static final int TEST_COUNT = 2000;

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

    protected byte[] getImageData(int digit) {
        return SpongeUtils.readFileToByteArray(Paths
                .get(System.getProperty(DemoServiceTestEnvironment.PROPERTY_MNIST_HOME), String.format("data/%d_0.png", digit)).toString());
    }

    protected Callable<Void> createTestCallable(int threadNumber, List<Pair<Integer, byte[]>> images, SpongeRestClient client) {
        return () -> {
            for (int i = 0; i < TEST_COUNT; i++) {
                logger.info("Iteration ({}): {}/{}", threadNumber, i + 1, TEST_COUNT);
                images.forEach(digitData -> assertEquals(digitData.getKey(),
                        client.call(Number.class, "MnistPredict", Arrays.asList(digitData.getValue()))));
            }

            return null;
        };
    }

    @Test
    public void testRestCallPredictLoad() throws Exception {
        List<Pair<Integer, byte[]>> images =
                Arrays.asList(1, 5, 7).stream().map(digit -> new ImmutablePair<>(digit, getImageData(digit))).collect(Collectors.toList());

        try (SpongeRestClient client = createRestClient()) {
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            List<? extends Callable<Void>> tasks = IntStream.rangeClosed(1, THREAD_COUNT)
                    .mapToObj(threadNo -> createTestCallable(threadNo, images, client)).collect(Collectors.toList());

            executor.invokeAll(tasks).stream().forEach(future -> {
                try {
                    future.get();
                } catch (Exception e) {
                    throw new SpongeException(e);
                }
            });

            executor.shutdownNow();
        }
    }
}
