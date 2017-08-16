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
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.standalone.StandaloneEngineMain;

public class StandaloneTest {

    private static final long TIMEOUT = 20;

    @Test
    public void testPythonRss() {
        StandaloneEngineMain engineMain = null;
        try {
            engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-c", "examples/standalone/python_rss/sponge_python_rss.xml");
            Engine engine = engineMain.getEngine();

            await().atMost(TIMEOUT, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "receivedRssCount").intValue() > 1);
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }

    @Test
    public void testNews() throws InterruptedException {
        StandaloneEngineMain engineMain = null;
        try {
            engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-c", "examples/standalone/news/config/config.xml");
            Engine engine = engineMain.getEngine();

            await().atMost(TIMEOUT, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("alarmSounded", null) != null
                    && engine.getOperations().getVariable(AtomicBoolean.class, "alarmSounded").get());
            TimeUnit.SECONDS.sleep(1);
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }

    @Test
    public void testCamelRssNews() throws InterruptedException {
        StandaloneEngineMain engineMain = null;
        try {
            engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-c", "examples/standalone/camel_rss_news/config/config.xml");
            Engine engine = engineMain.getEngine();

            await().atMost(TIMEOUT, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("stoppedSources", null) != null);

            TimeUnit.SECONDS.sleep(1);
            assertTrue(engine.getOperations().getVariable(AtomicBoolean.class, "stoppedSources").get());
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }

    @Test
    public void testCamelTemplate() throws InterruptedException {
        StandaloneEngineMain engineMain = null;
        try {
            engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-c", "examples/standalone/camel_template/config.xml");
            Engine engine = engineMain.getEngine();

            await().atMost(TIMEOUT, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("alarmSounded", null) != null
                    && engine.getOperations().getVariable(AtomicBoolean.class, "alarmSounded").get());
            TimeUnit.SECONDS.sleep(1);
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }
}
