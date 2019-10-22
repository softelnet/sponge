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

package org.openksavi.sponge.test.script.template;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.concurrent.TimeUnit;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.test.util.ScriptTestUtils;

public class CorrelatorsTestTemplate {

    public static void testCorrelators(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "correlators");

        try {
            assertEquals(2, engine.getCorrelatorGroups().size());
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "hardwareFailureScriptFinishCount").intValue() >= 1
                            && engine.getOperations().getVariable(Number.class, "hardwareFailureJavaFinishCount").intValue() >= 1);
            assertEquals(4, engine.getOperations().getVariable(Number.class, "hardwareFailureScriptCount").intValue());
            assertEquals(4, engine.getOperations().getVariable(Number.class, "hardwareFailureJavaCount").intValue());
            assertEquals(1, engine.getOperations().getVariable(Number.class, "hardwareFailureScriptFinishCount").intValue());
            assertEquals(1, engine.getOperations().getVariable(Number.class, "hardwareFailureJavaFinishCount").intValue());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    // To test global class fields in scripts.
    public static void testCorrelatorsRepeated(KnowledgeBaseType type) {
        testCorrelators(type);
    }

    public static void testCorrelatorsDuration(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "correlators_duration");

        try {
            await().pollDelay(5, TimeUnit.SECONDS).atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "hardwareFailureScriptCount").intValue() >= 3);
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    public static void testCorrelatorsBuilder(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "correlators_builder");

        try {
            assertEquals(1, engine.getCorrelatorGroups().size());
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "hardwareFailureScriptFinishCount").intValue() >= 1);
            assertEquals(4, engine.getOperations().getVariable(Number.class, "hardwareFailureScriptCount").intValue());
            assertEquals(1, engine.getOperations().getVariable(Number.class, "hardwareFailureScriptFinishCount").intValue());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
