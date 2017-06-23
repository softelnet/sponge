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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.util.CorrelationEventsLog;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.examples.TestUtils;
import org.openksavi.sponge.examples.script.ScriptTestUtils;
import org.openksavi.sponge.kb.KnowledgeBaseType;

public class RulesTestTemplate {

    private static final Logger logger = LoggerFactory.getLogger(RulesTestTemplate.class);

    public static void testRules(KnowledgeBaseType type) {
        testRules(type, ScriptTestUtils.startWithKnowledgeBase(type, "rules"));
    }

    public static void testRules(KnowledgeBaseType type, Engine engine) {
        try {
            await().atMost(20, TimeUnit.SECONDS).pollDelay(5, TimeUnit.SECONDS)
                    .until(() -> ((Number) engine.getOperations().getVariable("hardwareFailureScriptCount")).intValue() >= 3);
            await().atMost(5, TimeUnit.SECONDS)
                    .until(() -> ((Number) engine.getOperations().getVariable("sameSourceFirstFireCount")).intValue() >= 1);
            await().atMost(5, TimeUnit.SECONDS)
                    .until(() -> ((Number) engine.getOperations().getVariable("hardwareFailureJavaCount")).intValue() >= 2);

            assertEquals(3, ((Number) engine.getOperations().getVariable("hardwareFailureScriptCount")).intValue());
            assertEquals(1, ((Number) engine.getOperations().getVariable("sameSourceFirstFireCount")).intValue());
            assertEquals(3, ((Number) engine.getOperations().getVariable("hardwareFailureJavaCount")).intValue());
        } finally {
            engine.shutdown();
        }
    }

    public static void testManyRulesEvents(KnowledgeBaseType type) throws InterruptedException {
        int counter = 1;
        while (true) {
            logger.debug("Iteration {}", counter++);
            doTestRulesEvents(type, ScriptTestUtils.startWithConfig(type, "rules_events"));
        }
    }

    public static void testRulesEvents(KnowledgeBaseType type) throws InterruptedException {
        doTestRulesEvents(type, ScriptTestUtils.startWithKnowledgeBase(type, "rules_events"));
    }

    private static void doTestRulesEvents(KnowledgeBaseType type, Engine engine) throws InterruptedException {
        try {
            CorrelationEventsLog eventsLog =
                    engine.getOperations().getVariable(CorrelationEventsLog.class, CorrelationEventsLog.VARIABLE_NAME);

            Map<String, String[][]> expected = new LinkedHashMap<>();
            expected.put("RuleF", new String[][] { { "1" } });
            expected.put("RuleFFF", new String[][] { { "1", "2", "5" } });
            expected.put("RuleFFFDuration", new String[][] { { "1", "2", "5" } });
            expected.put("RuleFFL", new String[][] { { "1", "2", "7" } });
            expected.put("RuleFFA", new String[][] { { "1", "2", "5" }, { "1", "2", "6" }, { "1", "2", "7" } });
            expected.put("RuleFFN", new String[][] { { "1", "2", null } });
            expected.put("RuleFLF", new String[][] { { "1", "4", "5" } });
            expected.put("RuleFLL", new String[][] { { "1", "4", "7" } });
            expected.put("RuleFLA", new String[][] { { "1", "4", "5" }, { "1", "4", "6" }, { "1", "4", "7" } });
            expected.put("RuleFLN", new String[][] { { "1", "4", null } });
            expected.put("RuleFAF", new String[][] { { "1", "2", "5" }, { "1", "3", "5" }, { "1", "4", "5" } });
            expected.put("RuleFAL", new String[][] { { "1", "2", "7" }, { "1", "3", "7" }, { "1", "4", "7" } });
            // @formatter:off
            expected.put("RuleFAA", new String[][] {
                    { "1", "2", "5" }, { "1", "3", "5" }, { "1", "4", "5" },
                    { "1", "2", "6" }, { "1", "3", "6" }, { "1", "4", "6" },
                    { "1", "2", "7" }, { "1", "3", "7" }, { "1", "4", "7" } });
            // @formatter:on
            expected.put("RuleFNF", new String[][] { { "1", null, "5" } });
            expected.put("RuleFNFReject", new String[][] {});
            expected.put("RuleFNL", new String[][] { { "1", null, "7" } });
            expected.put("RuleFNA", new String[][] { { "1", null, "5" }, { "1", null, "6" }, { "1", null, "7" } });
            expected.put("RuleFAN", new String[][] { { "1", "2", null }, { "1", "3", null }, { "1", "4", null } });

            expected.forEach((rule, sequences) -> await().atMost(60, TimeUnit.SECONDS)
                    .until(() -> eventsLog.getEvents(rule, "1").size() == sequences.length));

            expected.forEach((rule, sequences) -> TestUtils.assertEventSequences(eventsLog, rule, "1", sequences));
        } finally {
            engine.shutdown();
        }
    }

    public static void testRulesNoneModeEvents(KnowledgeBaseType type) throws InterruptedException {
        Engine engine = ScriptTestUtils.startWithKnowledgeBase(type, "rules_none_mode_events");

        try {
            CorrelationEventsLog eventsLog =
                    engine.getOperations().getVariable(CorrelationEventsLog.class, CorrelationEventsLog.VARIABLE_NAME);

            await().pollDelay(2, TimeUnit.SECONDS).atMost(10, TimeUnit.SECONDS)
                    .until(() -> eventsLog.getEvents("RuleFNNF", "1").size() >= 1);
            TimeUnit.SECONDS.sleep(2);

            TestUtils.assertEventSequences(eventsLog, "RuleFNNF", "1", new String[][] { { "1", null, null, "5" } });
            TestUtils.assertEventSequences(eventsLog, "RuleFNNNL", "1", new String[][] { { "1", null, null, null, "7" } });
            TestUtils.assertEventSequences(eventsLog, "RuleFNNNLReject", "1", new String[][] {});
            TestUtils.assertEventSequences(eventsLog, "RuleFNFNL", "1", new String[][] { { "1", null, "2", null, "7" } });
        } finally {
            engine.shutdown();
        }
    }

    public static void testRulesNoneModeEventsConditions(KnowledgeBaseType type) throws InterruptedException {
        Engine engine = ScriptTestUtils.startWithKnowledgeBase(type, "rules_none_mode_events_conditions");

        try {
            CorrelationEventsLog eventsLog =
                    engine.getOperations().getVariable(CorrelationEventsLog.class, CorrelationEventsLog.VARIABLE_NAME);

            await().pollDelay(2, TimeUnit.SECONDS).atMost(10, TimeUnit.SECONDS)
                    .until(() -> eventsLog.getEvents("RuleFNF", "1").size() >= 1);
            TimeUnit.SECONDS.sleep(2);

            TestUtils.assertEventSequences(eventsLog, "RuleFNF", "1", new String[][] { { "1", null, "5" } });
            TestUtils.assertEventSequences(eventsLog, "RuleFNNFReject", "1", new String[][] {});
        } finally {
            engine.shutdown();
        }
    }

    public static void testRulesSyncAsync(KnowledgeBaseType type) throws InterruptedException {
        testRulesSyncAsync(type, ScriptTestUtils.startWithKnowledgeBase(type, "rules_sync_async"));
    }

    public static void testRulesSyncAsync(KnowledgeBaseType type, Engine engine) throws InterruptedException {
        try {
            CorrelationEventsLog eventsLog =
                    engine.getOperations().getVariable(CorrelationEventsLog.class, CorrelationEventsLog.VARIABLE_NAME);

            await().pollDelay(1, TimeUnit.SECONDS).atMost(10, TimeUnit.SECONDS)
                    .until(() -> eventsLog.getEvents("RuleFFF", "1").size() >= 1 && eventsLog.getEvents("RuleFFL", "1").size() >= 1);

            TestUtils.assertEventSequences(eventsLog, "RuleFFF", "1", new String[][] { { "1", "2", "5" } });
            TestUtils.assertEventSequences(eventsLog, "RuleFFL", "1", new String[][] { { "1", "2", "7" } });
        } finally {
            engine.shutdown();
        }
    }
}
