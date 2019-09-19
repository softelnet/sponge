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

package org.openksavi.sponge.integration.tests.core;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.test.util.TestUtils;

public class CoreProcessInstanceTest {

    @Test
    public void testProcessInstance() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/process_instance.py").build();
        engine.startup();

        try {
            assertEquals("TEST", engine.getOperations().call(String.class, "ProcessEcho"));
            assertEquals("TEST", engine.getOperations().call(String.class, "ProcessEnv"));

            await().atMost(15, TimeUnit.SECONDS).until(() -> engine.getOperations().call("ProcessWaitForOutput") != null);

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
