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

package org.openksavi.sponge.integration.tests.core;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.test.util.TestUtils;

public class CoreFiltersTest {

    @Test
    public void testFiltersEventPattern() {
        Engine engine = DefaultEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/filters_event_pattern.py").build();
        engine.startup();

        try {
            await().atMost(20, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "nameCount").intValue() >= 2
                            && engine.getOperations().getVariable(Number.class, "patternCount").intValue() >= 3
                            && engine.getOperations().getVariable(Number.class, "acceptedCount").intValue() >= 5);

            assertEquals(2, engine.getOperations().getVariable(Number.class, "nameCount").intValue());
            assertEquals(3, engine.getOperations().getVariable(Number.class, "patternCount").intValue());
            assertEquals(5, engine.getOperations().getVariable(Number.class, "acceptedCount").intValue());
            assertEquals(0, engine.getOperations().getVariable(Number.class, "notAcceptedCount").intValue());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
