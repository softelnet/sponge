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

package org.openksavi.sponge.integration.tests.core;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.test.TestUtils;
import org.openksavi.sponge.test.util.CorrelationEventsLog;

public class CoreRulesTest {

    @Test
    public void testRulesImmediateNoDuration() throws InterruptedException {
        Engine engine = DefaultEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/rules_immediate_no_duration.py").build();
        engine.getConfigurationManager().setAutoEnable(false);
        engine.startup();

        try {
            doTestRulesImmediate(engine);

            ScriptKnowledgeBaseInterpreter interpreter = Utils.getScriptInterpreter(engine, TestUtils.DEFAULT_KB);
            evalEnableRuleWithException(interpreter, "RuleFFL");
            evalEnableRuleWithException(interpreter, "RuleFFN");
            evalEnableRuleWithException(interpreter, "RuleFLL");
            evalEnableRuleWithException(interpreter, "RuleFLN");
            evalEnableRuleWithException(interpreter, "RuleFAL");
            evalEnableRuleWithException(interpreter, "RuleFNL");
            evalEnableRuleWithException(interpreter, "RuleFAN");
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testRulesImmediateDuration() throws InterruptedException {
        Engine engine = DefaultEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/rules_immediate_duration.py").build();
        engine.startup();

        try {
            doTestRulesImmediate(engine);

            CorrelationEventsLog eventsLog =
                    engine.getOperations().getVariable(CorrelationEventsLog.class, CorrelationEventsLog.VARIABLE_NAME);
            TestUtils.assertEventSequences(eventsLog, "RuleFFL", "1", new String[][] { { "1", "2", "7" } });
            TestUtils.assertEventSequences(eventsLog, "RuleFFN", "1", new String[][] { { "1", "2", null } });
            TestUtils.assertEventSequences(eventsLog, "RuleFLL", "1", new String[][] { { "1", "5", "7" } });
            TestUtils.assertEventSequences(eventsLog, "RuleFLN", "1", new String[][] { { "1", "5", null } });
            TestUtils.assertEventSequences(eventsLog, "RuleFAL", "1",
                    new String[][] { { "1", "2", "7" }, { "1", "3", "7" }, { "1", "5", "7" } });
            TestUtils.assertEventSequences(eventsLog, "RuleFNL", "1", new String[][] { { "1", null, "7" } });
            TestUtils.assertEventSequences(eventsLog, "RuleFAN", "1",
                    new String[][] { { "1", "2", null }, { "1", "3", null }, { "1", "5", null } });

            System.out.println(eventsLog);
        } finally {
            engine.shutdown();
        }
    }

    private void doTestRulesImmediate(Engine engine) throws InterruptedException {
        CorrelationEventsLog eventsLog = engine.getOperations().getVariable(CorrelationEventsLog.class, CorrelationEventsLog.VARIABLE_NAME);

        await().pollDelay(1, TimeUnit.SECONDS).atMost(10, TimeUnit.SECONDS).until(() -> eventsLog.getEvents("RuleFAA", "1").size() == 8);
        TimeUnit.SECONDS.sleep(1);

        TestUtils.assertEventSequences(eventsLog, "RuleFFF", "1", new String[][] { { "1", "2", "4" } });
        TestUtils.assertEventSequences(eventsLog, "RuleFFA", "1",
                new String[][] { { "1", "2", "4" }, { "1", "2", "6" }, { "1", "2", "7" } });
        TestUtils.assertEventSequences(eventsLog, "RuleFLF", "1", new String[][] { { "1", "3", "4" } });
        TestUtils.assertEventSequences(eventsLog, "RuleFLA", "1",
                new String[][] { { "1", "3", "4" }, { "1", "5", "6" }, { "1", "5", "7" } });
        TestUtils.assertEventSequences(eventsLog, "RuleFAF", "1", new String[][] { { "1", "2", "4" }, { "1", "3", "4" } });
        // @formatter:off
        TestUtils.assertEventSequences(eventsLog, "RuleFAA", "1", new String[][] {
                { "1", "2", "4" }, { "1", "3", "4" },
                { "1", "2", "6" }, { "1", "3", "6" }, { "1", "5", "6" },
                { "1", "2", "7" }, { "1", "3", "7" }, { "1", "5", "7" }});
        // @formatter:on
        TestUtils.assertEventSequences(eventsLog, "RuleFNF", "1", new String[][] { { "1", null, "4" } });
        TestUtils.assertEventSequences(eventsLog, "RuleFNFReject", "1", new String[][] {});
        TestUtils.assertEventSequences(eventsLog, "RuleFNA", "1",
                new String[][] { { "1", null, "4" }, { "1", null, "6" }, { "1", null, "7" } });

    }

    private static void evalEnableRuleWithException(ScriptKnowledgeBaseInterpreter interpreter, String ruleName) {
        SpongeException exception = null;

        try {
            interpreter.eval("EPS.enable(" + ruleName + ")");
        } catch (SpongeException e) {
            exception = e;
        }

        assertNotNull(exception);
    }
}
