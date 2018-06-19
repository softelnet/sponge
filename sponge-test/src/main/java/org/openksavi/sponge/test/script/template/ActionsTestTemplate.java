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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.test.util.ScriptTestUtils;

public class ActionsTestTemplate {

    public static void testActions(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "actions");

        try {
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("scriptActionResult") != null);
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("javaActionResult") != null);

            Object scriptResultObject = engine.getOperations().getVariable("scriptActionResult");
            @SuppressWarnings("rawtypes")
            List scriptResult = scriptResultObject instanceof List ? (List) scriptResultObject
                    : Arrays.stream((Object[]) scriptResultObject).collect(Collectors.toList());
            assertEquals(2, scriptResult.size());
            // Note, that different scripting engines may map numbers to different types.
            assertEquals(1, ((Number) scriptResult.get(0)).intValue());
            assertEquals("test", scriptResult.get(1));

            Object[] javaResult = (Object[]) engine.getOperations().getVariable("javaActionResult");
            assertEquals(2, javaResult.length);
            // Note, that different scripting engines may map numbers to different types.
            assertEquals(2, ((Number) javaResult[0]).intValue());
            assertEquals("TEST", javaResult[1]);

            assertEquals(3,
                    ((Number) engine.getOperations().call("ArrayArgumentAction", (Object) new Object[] { 1, 2, "text" })).intValue());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
