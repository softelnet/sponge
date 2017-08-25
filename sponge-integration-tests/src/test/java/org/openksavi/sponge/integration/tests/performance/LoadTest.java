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

package org.openksavi.sponge.integration.tests.performance;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.jython.PythonConstants;
import org.openksavi.sponge.test.script.template.RulesTestTemplate;
import org.openksavi.sponge.test.util.CorrelationEventsLog;
import org.openksavi.sponge.test.util.TestUtils;

public class LoadTest {

    private static final Logger logger = LoggerFactory.getLogger(LoadTest.class);

    @Test
    public void testRulesLoad() {
        Engine engine = DefaultEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/performance/rules_load.py").build();
        engine.startup();

        CorrelationEventsLog eventsLog = engine.getOperations().getVariable(CorrelationEventsLog.class, CorrelationEventsLog.VARIABLE_NAME);

        try {
            int maxIterations = 50;
            for (int i = 0; i < maxIterations; i++) {
                logger.info("Iteration {}", i + 1);

                eventsLog.clear();
                engine.getOperations().event("sendEvents").send();

                RulesTestTemplate.doTestRulesEvents(PythonConstants.TYPE, engine, 3600);
            }

            logger.info("Event performance is: {}", String.format("%.2f events/s", engine.getStatisticsManager().getEventPerformance()));
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testTriggersLoad() {
        Engine engine = DefaultEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/performance/triggers_load.py").build();
        engine.startup();

        int sleepMinutes = 2;

        try {
            TimeUnit.MINUTES.sleep(sleepMinutes);

            logger.info("Event performance after {} minutes is: {}", sleepMinutes,
                    String.format("%.2f events/s", engine.getStatisticsManager().getEventPerformance()));
        } catch (InterruptedException e) {
            throw Utils.wrapException("testTriggersLoad", e);
        } finally {
            engine.shutdown();
        }
    }
}
