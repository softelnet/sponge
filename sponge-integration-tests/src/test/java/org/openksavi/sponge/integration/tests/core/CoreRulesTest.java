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
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.examples.util.CorrelationEventsLog;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.test.util.TestUtils;

public class CoreRulesTest {

    private static final Logger logger = LoggerFactory.getLogger(CoreRulesTest.class);

    private Map<String, String[][]> createCommonExpectedSequences() {
        Map<String, String[][]> expected = new LinkedHashMap<>();

        expected.put("RuleFFF", new String[][] { { "1", "2", "4" } });
        expected.put("RuleFFA", new String[][] { { "1", "2", "4" }, { "1", "2", "6" }, { "1", "2", "7" } });
        expected.put("RuleFLF", new String[][] { { "1", "3", "4" } });
        expected.put("RuleFLA", new String[][] { { "1", "3", "4" }, { "1", "5", "6" }, { "1", "5", "7" } });
        expected.put("RuleFAF", new String[][] { { "1", "2", "4" }, { "1", "3", "4" } });
        // @formatter:off
        expected.put("RuleFAA", new String[][] {
            { "1", "2", "4" }, { "1", "3", "4" },
            { "1", "2", "6" }, { "1", "3", "6" }, { "1", "5", "6" },
            { "1", "2", "7" }, { "1", "3", "7" }, { "1", "5", "7" }});
        // @formatter:on
        expected.put("RuleFNF", new String[][] { { "1", null, "4" } });
        expected.put("RuleFNFReject", new String[][] {});
        expected.put("RuleFNA", new String[][] { { "1", null, "4" }, { "1", null, "6" }, { "1", null, "7" } });

        return expected;
    }

    @Test
    public void testRulesImmediateNoDuration() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/rules_immediate_no_duration.py").build();
        engine.getConfigurationManager().setAutoEnable(false);
        engine.startup();

        try {
            doTestRulesImmediate(engine, createCommonExpectedSequences());

            ScriptKnowledgeBaseInterpreter interpreter = SpongeUtils.getScriptInterpreter(engine, TestUtils.DEFAULT_KB);
            evalEnableRuleWithException(interpreter, "RuleFFL");
            evalEnableRuleWithException(interpreter, "RuleFFN");
            evalEnableRuleWithException(interpreter, "RuleFLL");
            evalEnableRuleWithException(interpreter, "RuleFLN");
            evalEnableRuleWithException(interpreter, "RuleFAL");
            evalEnableRuleWithException(interpreter, "RuleFNL");
            evalEnableRuleWithException(interpreter, "RuleFAN");
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testRulesImmediateDuration() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/rules_immediate_duration.py").build();
        engine.startup();

        try {
            Map<String, String[][]> expected = new LinkedHashMap<>(createCommonExpectedSequences());

            expected.put("RuleFFL", new String[][] { { "1", "2", "7" } });
            expected.put("RuleFFN", new String[][] { { "1", "2", null } });
            expected.put("RuleFLL", new String[][] { { "1", "5", "7" } });
            expected.put("RuleFLN", new String[][] { { "1", "5", null } });
            expected.put("RuleFAL", new String[][] { { "1", "2", "7" }, { "1", "3", "7" }, { "1", "5", "7" } });
            expected.put("RuleFNL", new String[][] { { "1", null, "7" } });
            expected.put("RuleFAN", new String[][] { { "1", "2", null }, { "1", "3", null }, { "1", "5", null } });

            doTestRulesImmediate(engine, expected);

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    private void doTestRulesImmediate(SpongeEngine engine, Map<String, String[][]> expected) {
        CorrelationEventsLog eventsLog = engine.getOperations().getVariable(CorrelationEventsLog.class, CorrelationEventsLog.VARIABLE_NAME);

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw SpongeUtils.wrapException(e);
        }

        expected.forEach((rule, sequences) -> {
            try {
                await().atMost(180, TimeUnit.SECONDS).until(() -> eventsLog.getEvents(rule, "1").size() >= sequences.length);
            } catch (Exception e) {
                logger.error("Unsuccessful waiting for rule {} sequences {}", rule, (Object) sequences);
                throw e;
            }
        });

        expected.forEach((rule, sequences) -> TestUtils.assertEventSequences(eventsLog, rule, "1", sequences));
    }

    private static void evalEnableRuleWithException(ScriptKnowledgeBaseInterpreter interpreter, String ruleName) {
        SpongeException exception = null;

        try {
            interpreter.eval("sponge.enable(" + ruleName + ")");
        } catch (SpongeException e) {
            exception = e;
        }

        assertNotNull(exception);
    }

    @Test
    public void testRulesEventPattern() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/rules_event_pattern.py").build();
        engine.startup();

        try {
            CorrelationEventsLog eventsLog =
                    engine.getOperations().getVariable(CorrelationEventsLog.class, CorrelationEventsLog.VARIABLE_NAME);

            await().atMost(20, TimeUnit.SECONDS)
                    .until(() -> eventsLog.getAllEvents("NameRule").size() >= 1 && eventsLog.getAllEvents("PatternRule").size() >= 3);

            assertEquals(1, eventsLog.getAllEvents("NameRule").size());
            assertEquals(3, eventsLog.getAllEvents("PatternRule").size());

            TestUtils.assertEventSequences(eventsLog, "NameRule", "a1", new String[][] { { "a1", "b1" } });
            TestUtils.assertEventSequences(eventsLog, "PatternRule", "a1", new String[][] { { "a1", "b1" }, { "a1", "b2" } });
            TestUtils.assertEventSequences(eventsLog, "PatternRule", "a2", new String[][] { { "a2", "b2" } });

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
