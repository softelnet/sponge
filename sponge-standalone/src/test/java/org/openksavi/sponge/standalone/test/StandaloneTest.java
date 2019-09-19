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

package org.openksavi.sponge.standalone.test;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.standalone.StandaloneEngineMain;

@net.jcip.annotations.NotThreadSafe
public class StandaloneTest {

    private static final long TIMEOUT = 180;

    private static final long SLEEP = 1;

    @Test
    public void testPythonRss() {
        StandaloneEngineMain engineMain = null;
        try {
            engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-c", "examples/standalone/python_rss/sponge_python_rss.xml");
            SpongeEngine engine = engineMain.getEngine();

            await().atMost(TIMEOUT, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "receivedRssCount").intValue() > 1);
            assertFalse(engine.isError());
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }

    @Test
    public void testNews() throws InterruptedException {
        StandaloneEngineMain engineMain = null;
        try {
            engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-c", "examples/standalone/news/config/config.xml");
            SpongeEngine engine = engineMain.getEngine();

            await().atMost(TIMEOUT, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("alarmSounded", null) != null
                    && engine.getOperations().getVariable(AtomicBoolean.class, "alarmSounded").get());
            TimeUnit.SECONDS.sleep(SLEEP);
            assertFalse(engine.isError());
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }

    @Test
    public void testCamelRssNews() throws InterruptedException {
        StandaloneEngineMain engineMain = null;
        try {
            engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-c", "examples/standalone/camel_rss_news/config/config.xml");
            SpongeEngine engine = engineMain.getEngine();

            await().atMost(TIMEOUT, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable("stoppedSources", null) != null
                            && engine.getOperations().getVariable(AtomicBoolean.class, "stoppedSources").get()
                            && engine.getOperations().getVariable(AtomicBoolean.class, "alarmSounded").get());

            TimeUnit.SECONDS.sleep(SLEEP);
            assertFalse(engine.isError());
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }

    @Test
    public void testCamelTemplate() throws InterruptedException {
        StandaloneEngineMain engineMain = null;
        try {
            engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-c", "examples/standalone/camel_template/config.xml");
            SpongeEngine engine = engineMain.getEngine();

            await().atMost(TIMEOUT, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("alarmSounded", null) != null
                    && engine.getOperations().getVariable(AtomicBoolean.class, "alarmSounded").get());
            TimeUnit.SECONDS.sleep(SLEEP);
            assertFalse(engine.isError());
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }

    @Test
    public void testExecuteScript() {
        StandaloneEngineMain engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-k", "examples/standalone/execute_script.py");
        assertFalse(engineMain.getEngine().isError());
    }

    @Test
    public void testOptionSystemProperty() {
        StandaloneEngineMain engineMain = null;
        try {
            String name1 = "PROPERTY_NAME_1";
            String value1 = "PROPERTY_VALUE_1";
            String name2 = "PROPERTY_NAME_2";
            String value2 = "PROPERTY_VALUE_2";
            engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-k", "examples/standalone/trigger_simple.py",
                    "-D" + name1 + "=" + value1, "-D", name2 + "=" + value2);
            SpongeEngine engine = engineMain.getEngine();

            assertEquals(value1, System.getProperty(name1));
            assertEquals(value2, System.getProperty(name2));
            assertFalse(engine.isError());
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }
}
