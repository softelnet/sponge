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

package org.openksavi.sponge.examples.script.template;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.examples.TestUtils;
import org.openksavi.sponge.examples.script.ScriptTestUtils;
import org.openksavi.sponge.kb.KnowledgeBaseType;

public class ScriptTestTemplate {

    public static void testScriptOverriding(KnowledgeBaseType type) {
        testScriptOverriding(type, DefaultEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, ScriptTestUtils.getScriptKnowledgeBaseFileName(type, "script_overriding")).build());
    }

    public static void testScriptOverriding(KnowledgeBaseType type, Engine engine) {
        engine.getConfigurationManager().setAutoEnable(false);
        engine.startup();

        try {
            await().atMost(5, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "receivedEventA2").intValue() == 2);

            assertEquals(0, engine.getOperations().getVariable(Number.class, "receivedEventA1").intValue());

            assertEquals(0, engine.getOperations().getVariable(Number.class, "functionA1").intValue());
            assertEquals(2, engine.getOperations().getVariable(Number.class, "functionA2").intValue());
        } finally {
            engine.shutdown();
        }
    }
}
