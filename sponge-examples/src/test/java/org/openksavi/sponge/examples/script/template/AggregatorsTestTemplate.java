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

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.examples.script.ScriptTestUtils;
import org.openksavi.sponge.kb.KnowledgeBaseType;

public class AggregatorsTestTemplate {

    public static void testAggregators(KnowledgeBaseType type) throws InterruptedException {
        Engine engine = ScriptTestUtils.startWithKnowledgeBase(type, "aggregators");

        try {
            await().atMost(5, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "hardwareFailureScriptCount").intValue() >= 4 &&
                            engine.getOperations().getVariable(Number.class, "hardwareFailureJavaCount").intValue() >= 4);
            assertEquals(4, engine.getOperations().getVariable(Number.class, "hardwareFailureScriptCount").intValue());
            assertEquals(4, engine.getOperations().getVariable(Number.class, "hardwareFailureJavaCount").intValue());
        } finally {
            engine.shutdown();
        }
    }

    // To test global class fields in scripts.
    public static void testAggregatorsRepeated(KnowledgeBaseType type) throws InterruptedException {
        testAggregators(type);
    }

    public static void testAggregatorsDuration(KnowledgeBaseType type) throws InterruptedException {
        Engine engine = ScriptTestUtils.startWithKnowledgeBase(type, "aggregators_duration");

        try {
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "hardwareFailureScriptCount").intValue() >= 4);
        } finally {
            engine.shutdown();
        }
    }
}
