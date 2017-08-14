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

package org.openksavi.sponge.examples.standalone;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.standalone.StandaloneEngineMain;

public class StandaloneCamelXmlTest {

    @Test
    public void testCamelRouteXml() {
        StandaloneEngineMain engineMain = new StandaloneEngineMain(true);

        try {
            engineMain.startup("-c", "config/config.xml");
            Engine engine = engineMain.getEngine();

            await().atMost(20, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("message") != null);

            assertTrue(engine.getOperations().getVariable(String.class, "message").contains("Send me to Camel"));
            assertTrue(engine.getOperations().getVariable(Boolean.class, "sent"));
        } finally {
            engineMain.shutdown();
        }
    }
}
