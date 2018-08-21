/*
 * Copyright 2016-2018 The Sponge authors.
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.examples.TestCompoundComplexObject;
import org.openksavi.sponge.test.util.TestUtils;
import org.openksavi.sponge.type.ActionType;
import org.openksavi.sponge.type.AnyType;
import org.openksavi.sponge.type.IntegerType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.ObjectType;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.Type;
import org.openksavi.sponge.type.TypeKind;

public class CoreActionsTest {

    @Test
    public void testActionsMetadata() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata.py").build();
        engine.startup();

        try {
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("scriptActionResult") != null);
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("javaActionResult") != null);

            String scriptResult = engine.getOperations().getVariable(String.class, "scriptActionResult");
            assertEquals("TEST", scriptResult);

            Object[] javaResult = (Object[]) engine.getOperations().getVariable("javaActionResult");
            assertEquals(2, javaResult.length);
            // Note, that different scripting engines may map numbers to different types.
            assertEquals(2, ((Number) javaResult[0]).intValue());
            assertEquals("TEST", javaResult[1]);

            ActionAdapter upperActionAdapter = engine.getActionManager().getActionAdapter("UpperEchoAction");
            assertEquals("Echo Action", upperActionAdapter.getDisplayName());
            assertEquals("Returns the upper case string", upperActionAdapter.getDescription());

            ArgMeta<?>[] argMeta = upperActionAdapter.getArgsMeta();
            assertEquals(1, argMeta.length);
            assertEquals("text", argMeta[0].getName());
            assertEquals(TypeKind.STRING, argMeta[0].getType().getKind());
            assertEquals(true, argMeta[0].isRequired());
            assertEquals("Argument 1", argMeta[0].getDisplayName());
            assertEquals("Argument 1 description", argMeta[0].getDescription());

            assertEquals(TypeKind.STRING, upperActionAdapter.getResultMeta().getType().getKind());
            assertEquals("Upper case string", upperActionAdapter.getResultMeta().getDisplayName());
            assertEquals("Result description", upperActionAdapter.getResultMeta().getDescription());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsCallError() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_call_error.py").build();
        engine.startup();

        try {
            engine.getOperations().call("ErrorAction");

            assertFalse(engine.isError());
        } catch (SpongeException e) {
            // Jython-specific error message copying.
            assertTrue(e.getMessage().contains("global name 'Nooone' is not defined"));
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsMetadataTypes() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_types.py").build();
        engine.startup();

        try {
            ActionAdapter adapter = engine.getActionManager().getActionAdapter("MultipleArgumentsAction");
            assertEquals("Multiple arguments action", adapter.getDisplayName());
            assertEquals("Multiple arguments action.", adapter.getDescription());

            ArgMeta<?>[] argMeta = adapter.getArgsMeta();
            assertEquals(9, argMeta.length);

            assertEquals("stringArg", argMeta[0].getName());
            assertEquals(TypeKind.STRING, argMeta[0].getType().getKind());
            assertEquals(10, ((StringType) argMeta[0].getType()).getMaxLength().intValue());
            assertEquals("ipAddress", argMeta[0].getType().getFormat());
            assertEquals(true, argMeta[0].isRequired());
            assertEquals(null, argMeta[0].getDisplayName());
            assertEquals(null, argMeta[0].getDescription());

            assertEquals("integerArg", argMeta[1].getName());
            assertEquals(TypeKind.INTEGER, argMeta[1].getType().getKind());
            assertEquals(1, ((IntegerType) argMeta[1].getType()).getMinValue().intValue());
            assertEquals(100, ((IntegerType) argMeta[1].getType()).getMaxValue().intValue());

            assertEquals("anyArg", argMeta[2].getName());
            assertEquals(TypeKind.ANY, argMeta[2].getType().getKind());
            assertTrue(argMeta[2].getType() instanceof AnyType);

            assertEquals("stringListArg", argMeta[3].getName());
            assertEquals(TypeKind.LIST, argMeta[3].getType().getKind());
            assertEquals(TypeKind.STRING, ((ListType) argMeta[3].getType()).getElementType().getKind());

            assertEquals("decimalListArg", argMeta[4].getName());
            assertEquals(TypeKind.LIST, argMeta[4].getType().getKind());
            Type elementType4 = ((ListType) argMeta[4].getType()).getElementType();
            assertEquals(TypeKind.OBJECT, elementType4.getKind());
            assertEquals(BigDecimal.class.getName(), ((ObjectType) elementType4).getClassName());

            assertEquals("stringArrayArg", argMeta[5].getName());
            assertEquals(TypeKind.OBJECT, argMeta[5].getType().getKind());
            assertEquals(String[].class, SpongeUtils.getClass(((ObjectType) argMeta[5].getType()).getClassName()));

            assertEquals("javaClassArg", argMeta[6].getName());
            assertEquals(TypeKind.OBJECT, argMeta[6].getType().getKind());
            assertEquals(TestCompoundComplexObject.class.getName(), ((ObjectType) argMeta[6].getType()).getClassName());

            assertEquals("javaClassListArg", argMeta[7].getName());
            assertEquals(TypeKind.LIST, argMeta[7].getType().getKind());
            Type elementType7 = ((ListType) argMeta[7].getType()).getElementType();
            assertEquals(TypeKind.OBJECT, elementType7.getKind());
            assertEquals(TestCompoundComplexObject.class.getName(), ((ObjectType) elementType7).getClassName());

            assertEquals("binaryArg", argMeta[8].getName());
            assertEquals(TypeKind.BINARY, argMeta[8].getType().getKind());
            assertNull(argMeta[8].getType().getFormat());
            assertEquals(4, argMeta[8].getType().getFeatures().size());
            assertEquals(28, ((Number) argMeta[8].getType().getFeatures().get("width")).intValue());
            assertEquals(28, ((Number) argMeta[8].getType().getFeatures().get("height")).intValue());
            assertEquals("black", argMeta[8].getType().getFeatures().get("background"));
            assertEquals("white", argMeta[8].getType().getFeatures().get("color"));
            assertEquals(Arrays.asList("drawing", "handwritten"), argMeta[8].getType().getTags());

            assertEquals(TypeKind.BOOLEAN, adapter.getResultMeta().getType().getKind());
            assertEquals("Boolean result", adapter.getResultMeta().getDisplayName());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testActionsActionType() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_action_type.py").build();
        engine.startup();

        try {
            ActionAdapter actionTypeAction = engine.getActionManager().getActionAdapter("ActionTypeAction");
            assertEquals(1, actionTypeAction.getArgsMeta().length);
            assertTrue(actionTypeAction.getArgsMeta()[0].getType() instanceof ActionType);
            ArgMeta<ActionType> sensorNameArgMeta = (ArgMeta<ActionType>) actionTypeAction.getArgsMeta()[0];
            ActionAdapter getAvailableSensorNamesAction =
                    engine.getActionManager().getActionAdapter(sensorNameArgMeta.getType().getActionName());
            assertTrue(getAvailableSensorNamesAction.getResultMeta().getType() instanceof ListType);
            assertTrue(((ListType) getAvailableSensorNamesAction.getResultMeta().getType()).getElementType() instanceof StringType);

            List<String> availableSensors = engine.getOperations().call(List.class, sensorNameArgMeta.getType().getActionName());

            assertTrue(engine.getOperations().call(Boolean.class, "ActionTypeAction", availableSensors.get(0)));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
