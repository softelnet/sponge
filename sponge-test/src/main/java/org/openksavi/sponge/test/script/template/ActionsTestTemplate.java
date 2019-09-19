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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.examples.PowerEchoAction;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.test.util.ScriptTestUtils;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.StringType;

@SuppressWarnings("rawtypes")
public class ActionsTestTemplate {

    public static void testActions(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "actions");

        try {
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("scriptActionResult") != null);
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("javaActionResult") != null);

            Object scriptResultObject = engine.getOperations().getVariable("scriptActionResult");
            List scriptResult = scriptResultObject instanceof List ? (List) scriptResultObject
                    : Arrays.stream((Object[]) scriptResultObject).collect(Collectors.toList());
            assertEquals(2, scriptResult.size());
            // Note, that different scripting engines may map numbers to different types.
            assertEquals(1, ((Number) scriptResult.get(0)).intValue());
            assertEquals("test", scriptResult.get(1));

            List javaResult = engine.getOperations().getVariable(List.class, "javaActionResult");
            assertEquals(2, javaResult.size());
            assertEquals(2, ((Number) javaResult.get(0)).intValue());
            assertEquals("TEST", javaResult.get(1));

            assertEquals(3, engine.getOperations()
                    .call(Number.class, "ArrayArgumentAction", Arrays.asList((Object) new Object[] { 1, 2, "text" })).intValue());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    public static void testHelloWorldAction(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "actions_hello_world");

        try {
            String actionName = "HelloWorldAction";
            ActionMeta actionMeta = engine.getActionMeta(actionName);
            assertEquals(actionName, actionMeta.getName());
            assertEquals("Hello world", actionMeta.getLabel());
            assertEquals("Returns a greeting text.", actionMeta.getDescription());
            assertEquals(1, actionMeta.getArgs().size());
            assertTrue(actionMeta.getFeatures().isEmpty());

            DataType argType = actionMeta.getArgs().get(0);
            assertEquals("name", argType.getName());
            assertEquals("Your name", argType.getLabel());
            assertEquals("Type your name.", argType.getDescription());
            assertTrue(argType instanceof StringType);

            assertEquals("Greeting", actionMeta.getResult().getLabel());
            assertEquals("The greeting text.", actionMeta.getResult().getDescription());
            assertTrue(actionMeta.getResult() instanceof StringType);

            String name = "Sponge user";
            assertEquals(String.format("Hello World! Hello %s!", name), engine.getOperations().call(actionName, Arrays.asList(name)));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    public static void testActionJavaInheritance(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "actions_java_inheritance");

        try {
            assertEquals(1, engine.getActions().size());
            List result = engine.getOperations().call(List.class, "ExtendedFromAction", Arrays.asList(1, "Text"));

            assertEquals(2, result.size());
            assertEquals(11, ((Number) result.get(0)).intValue());
            assertEquals("text", result.get(1));

            engine.getOperations().enableJava(PowerEchoAction.class);
            assertEquals(2, engine.getActions().size());

            List result2 = engine.getOperations().call(List.class, "PowerEchoAction", Arrays.asList(1, "Text"));

            assertEquals(2, result2.size());
            assertEquals(2, ((Number) result2.get(0)).intValue());
            assertEquals("TEXT", result2.get(1));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
