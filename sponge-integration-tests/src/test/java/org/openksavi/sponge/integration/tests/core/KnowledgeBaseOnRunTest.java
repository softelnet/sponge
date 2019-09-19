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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;

public class KnowledgeBaseOnRunTest {

    @Test
    public void testOnRunReturnsFalse() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase("kb", "examples/core/knowledge_base_on_run_returns_false.py").build();
        engine.startup();

        try {
            await().atMost(20, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "onRun").get());
            await().atMost(20, TimeUnit.SECONDS).until(() -> engine.isTerminated());

            assertFalse(engine.isError());
            assertFalse(engine.isRunning());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testOnRunReturnsTrue() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase("kb", "examples/core/knowledge_base_on_run_returns_true.py").build();
        engine.startup();

        try {
            await().atMost(20, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "trigger").get());

            assertTrue(engine.getOperations().getVariable(AtomicBoolean.class, "onRun").get());
            assertFalse(engine.isError());
            assertTrue(engine.isRunning());
            assertFalse(engine.isTerminated());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testOnRunNotPresent() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase("kb", "examples/core/knowledge_base_on_run_not_present.py").build();
        engine.startup();

        try {
            await().atMost(20, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "trigger").get());

            assertFalse(engine.isError());
            assertTrue(engine.isRunning());
            assertFalse(engine.isTerminated());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testOnRunIncorrectResult() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase("kb", "examples/core/knowledge_base_on_run_incorrect_result.py").build();

        try {
            assertThrows(SpongeException.class, () -> engine.startup());
        } finally {
            engine.shutdown();
        }
    }
}
