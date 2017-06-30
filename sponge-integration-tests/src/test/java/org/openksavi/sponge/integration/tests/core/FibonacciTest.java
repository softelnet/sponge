/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.integration.tests.core;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.engine.Engine;

public class FibonacciTest {

    @Test
    public void testProcessingEventsSynchronizationTest() {
        Engine engine = DefaultEngine.builder().knowledgeBase("kb", "examples/core/fibonacci.py").build();
        engine.startup();

        try {
            await().atMost(20, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("f(maxIndex)") != null);

            Number fmax = engine.getOperations().getVariable(Number.class, "f(maxIndex)");
            assertEquals(new BigInteger("354224848179261915075"), fmax);
        } finally {
            engine.shutdown();
        }
    }
}
