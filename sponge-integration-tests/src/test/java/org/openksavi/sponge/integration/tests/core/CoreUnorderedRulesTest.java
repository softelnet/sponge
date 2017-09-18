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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.test.util.CorrelationEventsLog;
import org.openksavi.sponge.test.util.TestUtils;

public class CoreUnorderedRulesTest {

    private static final Logger logger = LoggerFactory.getLogger(CoreUnorderedRulesTest.class);

    @Test
    public void testUnorderedRulesEvents() {
        Engine engine = DefaultEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/unordered_rules_events.py").build();
        engine.startup();

        try {
            doTestUnorderedRulesEvents(engine, 180);
        } finally {
            engine.shutdown();
        }
    }

    public static void doTestUnorderedRulesEvents(Engine engine, long timeout) {
        CorrelationEventsLog eventsLog = engine.getOperations().getVariable(CorrelationEventsLog.class, CorrelationEventsLog.VARIABLE_NAME);

        // Real test event sequence: a1, b1, b2, b3, c1, c2, a2, c3
        Map<String, String[][]> expected = new LinkedHashMap<>();
        // @formatter:off
        expected.put("RuleF", new String[][] { { "a1" }, { "a2" } });
        expected.put("RuleFFF", new String[][] {
            { "a1", "b1", "c1" }, { "b1", "c1", "a2" }, { "b2", "c1", "a2" }, { "b3", "c1", "a2" }});
        expected.put("RuleFFFDuration", expected.get("RuleFFF"));
        expected.put("RuleFFL", expected.get("RuleFFF"));
        expected.put("RuleFFA", new String[][] {
            { "a1", "b1", "c1" },
            { "b1", "c1", "a2" }, { "b1", "a2", "c3" },
            { "b2", "c1", "a2" }, { "b2", "a2", "c3" },
            { "b3", "c1", "a2" }, { "b3", "a2", "c3" }});
        expected.put("RuleFLF", new String[][] {
            { "a1", "b3", "c1" },
            { "b1", "c1", "a2" },
            { "b2", "c1", "a2" },
            { "b3", "c1", "a2" }});
        expected.put("RuleFLL", new String[][] { { "a1", "b3", "c1" }, { "b1", "c1", "a2" }, { "b2", "c1", "a2" },
            { "b3", "c1", "a2" }});
        expected.put("RuleFLA", new String[][] { { "a1", "b3", "c1" },
            { "b1", "c1", "a2" }, { "b1", "a2", "c3" },
            { "b2", "c1", "a2" }, { "b2", "a2", "c3" },
            { "b3", "c1", "a2" }, { "b3", "a2", "c3" }});
        expected.put("RuleFAF", new String[][] { { "a1", "b1", "c1" }, { "a1", "b2", "c1" }, { "a1", "b3", "c1" },
            { "b1", "c1", "a2" },
            { "b2", "c1", "a2" },
            { "b3", "c1", "a2" }});
        expected.put("RuleFAL", new String[][] { { "a1", "b1", "c1" }, { "a1", "b2", "c1" }, { "a1", "b3", "c1" },
            { "b1", "c1", "a2" },
            { "b2", "c1", "a2" },
            { "b3", "c1", "a2" }});
        expected.put("RuleFAA", new String[][] { { "a1", "b1", "c1" }, { "a1", "b2", "c1" }, { "a1", "b3", "c1" },
            { "b1", "c1", "a2" }, { "b1", "a2", "c3" },
            { "b2", "c1", "a2" }, { "b2", "a2", "c3" },
            { "b3", "c1", "a2" }, { "b3", "a2", "c3" }});
        expected.put("RuleFFN", new String[][] { { "b1", "c1", null }, { "b2", "c1", null }, { "b3", "c1", null } });
        expected.put("RuleFLN", expected.get("RuleFFN"));
        expected.put("RuleFAN", expected.get("RuleFFN"));
        expected.put("RuleFNF", new String[][] { { "a1", "c1", null }, { "c1", "a2", null }, { "c2", "a2", null }, { "a2", "c3", null } });
        expected.put("RuleFNL", expected.get("RuleFNF"));
        expected.put("RuleFNA", expected.get("RuleFNF"));
        // @formatter:on

        expected.put("RuleFFNReject", new String[][] {});
        expected.put("RuleFLNReject", new String[][] {});
        expected.put("RuleFANReject", new String[][] {});
        expected.put("RuleFNNReject", new String[][] { { "c3", null, null } });
        expected.put("RuleFNFReject", new String[][] { { "c1", "a2", null }, { "c2", "a2", null }, { "a2", "c3", null } });
        expected.put("RuleFNLReject", expected.get("RuleFNFReject"));
        expected.put("RuleFNAReject", expected.get("RuleFNFReject"));

        expected.put("RuleLFNReject", new String[][] {});
        expected.put("RuleLLNReject", new String[][] {});
        expected.put("RuleLANReject", new String[][] {});
        expected.put("RuleLNNReject", new String[][] { { "c3", null, null } });
        expected.put("RuleLNFReject", new String[][] { { "c1", "a2", null }, { "c2", "a2", null }, { "a2", "c3", null } });
        expected.put("RuleLNLReject", expected.get("RuleLNFReject"));
        expected.put("RuleLNAReject", expected.get("RuleLNFReject"));

        expected.put("RuleAFNReject", new String[][] {});
        expected.put("RuleALNReject", new String[][] {});
        expected.put("RuleAANReject", new String[][] {});
        expected.put("RuleANNReject", new String[][] { { "c3", null, null } });
        expected.put("RuleANFReject", new String[][] { { "c1", "a2", null }, { "c2", "a2", null }, { "a2", "c3", null } });
        expected.put("RuleANLReject", expected.get("RuleANFReject"));
        expected.put("RuleANAReject", expected.get("RuleANFReject"));

        expected.put("RuleNFNReject", new String[][] {});
        expected.put("RuleNLNReject", new String[][] {});
        expected.put("RuleNANReject", new String[][] {});
        expected.put("RuleNNNReject", new String[][] {});
        expected.put("RuleNNFReject", new String[][] {});
        expected.put("RuleNNLReject", new String[][] {});
        expected.put("RuleNNAReject", new String[][] {});

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw Utils.wrapException("sleep", e);
        }

        expected.forEach((rule, sequences) -> {
            try {
                logger.info("Checking {}...", rule);
                await().atMost(timeout, TimeUnit.SECONDS).until(() -> eventsLog.getEvents(rule, null).size() >= sequences.length);
                TestUtils.assertEventSequences(eventsLog, rule, null, sequences, true);
                logger.info("Rule {} OK; expected sequences: {}", rule, sequences);
            } catch (Exception e) {
                logger.error("Unsuccessful waiting for rule {}; expected sequences: {}", rule, (Object) sequences);
                throw e;
            }
        });

        assertFalse(engine.isError());
    }
}
