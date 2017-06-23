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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.examples.script.ScriptTestUtils;
import org.openksavi.sponge.kb.KnowledgeBaseType;

public class LibraryTestTemplate {

    @SuppressWarnings("unchecked")
    public static void testLibrary(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithConfig(type, "library");

        try {
            await().pollDelay(5, TimeUnit.SECONDS).atMost(60, TimeUnit.SECONDS)
                    .until(() -> ((Map<String, String>) engine.getOperations().getVariable("hostStatus")).size() >= 2);

            assertEquals("ERROR",
                    ((Map<String, String>) engine.getOperations().getVariable("hostStatus")).get("www.wikipedia.org.unknown"));
            assertEquals("200", ((Map<String, String>) engine.getOperations().getVariable("hostStatus")).get("www.wikipedia.org"));
        } finally {
            engine.shutdown();
        }
    }
}
