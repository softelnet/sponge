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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.test.util.ScriptTestUtils;

public class UnorderedRulesTestTemplate {

    @Test
    public static void testUnorderedRules(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithKnowledgeBase(type, "unordered_rules");

        try {
            await().atMost(60, TimeUnit.SECONDS).pollDelay(5, TimeUnit.SECONDS)
                    .until(() -> ((Number) engine.getOperations().getVariable("hardwareFailureScriptCount")).intValue() >= 6);
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> ((Number) engine.getOperations().getVariable("sameSourceFirstFireCount")).intValue() >= 5);
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> ((Number) engine.getOperations().getVariable("hardwareFailureJavaCount")).intValue() >= 6);

            assertEquals(6, ((Number) engine.getOperations().getVariable("hardwareFailureScriptCount")).intValue());
            assertEquals(5, ((Number) engine.getOperations().getVariable("sameSourceFirstFireCount")).intValue());
            assertEquals(6, ((Number) engine.getOperations().getVariable("hardwareFailureJavaCount")).intValue());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
