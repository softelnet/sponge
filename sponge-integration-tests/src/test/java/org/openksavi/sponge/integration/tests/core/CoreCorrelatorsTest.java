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

public class CoreCorrelatorsTest {

    @Test
    public void testCorrelatorsEventPattern() {
        Engine engine = DefaultEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/correlators_event_pattern.py").build();
        engine.startup();

        try {
            await().atMost(20, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(Number.class, "nameCount").intValue() >= 3
                    && engine.getOperations().getVariable(Number.class, "patternCount").intValue() >= 7);

            assertEquals(3, engine.getOperations().getVariable(Number.class, "nameCount").intValue());
            assertEquals(7, engine.getOperations().getVariable(Number.class, "patternCount").intValue());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
