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
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.engine.Engine;

public class ReloadDurationTest {

    @Test
    public void testReloadDuration() throws InterruptedException {
        Engine engine = DefaultEngine.builder().knowledgeBase("kb", "examples/core/reload_duration.py").build();
        engine.startup();

        try {
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "ruleCFired").get()
                            && engine.getOperations().getVariable(AtomicBoolean.class, "ruleAFired").get()
                            && engine.getOperations().getVariable(AtomicBoolean.class, "ruleBFired").get());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
