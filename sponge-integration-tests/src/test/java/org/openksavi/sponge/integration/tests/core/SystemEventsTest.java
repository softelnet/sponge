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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.python.google.common.collect.Lists;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;

public class SystemEventsTest {

    @Test
    public void testSystemEvents() {
        SpongeEngine engine = DefaultSpongeEngine.builder().config("examples/core/system_events.xml").build();
        engine.startup();

        try {
            await().atMost(60, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "countA").intValue() >= 5
                            && engine.getOperations().getVariable(Number.class, "countB").intValue() >= 1
                            && engine.getOperations().getVariable(List.class, "listC").size() >= 7);

            assertEquals(5, engine.getOperations().getVariable(Number.class, "countA").intValue());
            assertEquals(1, engine.getOperations().getVariable(Number.class, "countB").intValue());
            assertEquals(Lists.newArrayList("startup", "x", "e", "e", "e", "e", "e"),
                    engine.getOperations().getVariable(List.class, "listC"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
