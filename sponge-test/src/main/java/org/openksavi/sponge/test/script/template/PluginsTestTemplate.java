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

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.test.util.ScriptTestUtils;

public class PluginsTestTemplate {

    public static void testJavaPlugin(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithConfig(type, "plugins_java");

        try {
            await().pollDelay(1, TimeUnit.SECONDS).atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable("connectionName") != null);

            assertEquals("Example connection", engine.getOperations().getVariable("connectionName"));
            assertEquals("Echo test! x 2", engine.getOperations().getVariable("echoConfig"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    public static void testKbPlugin(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithConfig(type, "plugins_kb");

        try {
            await().pollDelay(1, TimeUnit.SECONDS).atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable("valueAfter") != null);

            assertEquals("Value A", engine.getOperations().getVariable("valueBefore"));
            assertEquals("Value B", engine.getOperations().getVariable("valueAfter"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
