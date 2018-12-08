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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.action.ArgValue;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.WrappedException;
import org.openksavi.sponge.examples.CustomObject;
import org.openksavi.sponge.test.util.TestUtils;
import org.openksavi.sponge.type.AnyType;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.IntegerType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.ObjectType;
import org.openksavi.sponge.type.StringType;

public class CoreActionsTest {

    private static final Logger logger = LoggerFactory.getLogger(CoreActionsTest.class);

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

            List<ArgMeta<?>> argMeta = upperActionAdapter.getArgsMeta();
            assertEquals(1, argMeta.size());
            assertEquals("text", argMeta.get(0).getName());
            assertEquals(DataTypeKind.STRING, argMeta.get(0).getType().getKind());
            assertEquals(false, argMeta.get(0).getType().isNullable());
            assertEquals("Argument 1", argMeta.get(0).getDisplayName());
            assertEquals("Argument 1 description", argMeta.get(0).getDescription());

            assertEquals(DataTypeKind.STRING, upperActionAdapter.getResultMeta().getType().getKind());
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

    @SuppressWarnings("rawtypes")
    @Test
    public void testActionsMetadataTypes() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_types.py").build();
        engine.startup();

        try {
            ActionAdapter adapter = engine.getActionManager().getActionAdapter("MultipleArgumentsAction");
            assertEquals("Multiple arguments action", adapter.getDisplayName());
            assertEquals("Multiple arguments action.", adapter.getDescription());

            List<ArgMeta<?>> argMeta = adapter.getArgsMeta();
            assertEquals(9, argMeta.size());

            assertEquals("stringArg", argMeta.get(0).getName());
            assertEquals(DataTypeKind.STRING, argMeta.get(0).getType().getKind());
            assertEquals(10, ((StringType) argMeta.get(0).getType()).getMaxLength().intValue());
            assertEquals("ipAddress", argMeta.get(0).getType().getFormat());
            assertEquals(false, argMeta.get(0).getType().isNullable());
            assertEquals(null, argMeta.get(0).getDisplayName());
            assertEquals(null, argMeta.get(0).getDescription());
            assertNull(argMeta.get(0).getType().getDefaultValue());

            assertEquals("integerArg", argMeta.get(1).getName());
            assertEquals(DataTypeKind.INTEGER, argMeta.get(1).getType().getKind());
            assertEquals(1, ((IntegerType) argMeta.get(1).getType()).getMinValue().intValue());
            assertEquals(100, ((IntegerType) argMeta.get(1).getType()).getMaxValue().intValue());
            assertEquals(50, argMeta.get(1).getType().getDefaultValue());

            assertEquals("anyArg", argMeta.get(2).getName());
            assertEquals(DataTypeKind.ANY, argMeta.get(2).getType().getKind());
            assertTrue(argMeta.get(2).getType() instanceof AnyType);
            assertEquals(true, argMeta.get(2).getType().isNullable());

            assertEquals("stringListArg", argMeta.get(3).getName());
            assertEquals(DataTypeKind.LIST, argMeta.get(3).getType().getKind());
            assertEquals(DataTypeKind.STRING, ((ListType) argMeta.get(3).getType()).getElementType().getKind());

            assertEquals("decimalListArg", argMeta.get(4).getName());
            assertEquals(DataTypeKind.LIST, argMeta.get(4).getType().getKind());
            DataType elementType4 = ((ListType) argMeta.get(4).getType()).getElementType();
            assertEquals(DataTypeKind.OBJECT, elementType4.getKind());
            assertEquals(BigDecimal.class.getName(), ((ObjectType) elementType4).getClassName());

            assertEquals("stringArrayArg", argMeta.get(5).getName());
            assertEquals(DataTypeKind.OBJECT, argMeta.get(5).getType().getKind());
            assertEquals(String[].class, SpongeUtils.getClass(((ObjectType) argMeta.get(5).getType()).getClassName()));

            assertEquals("javaClassArg", argMeta.get(6).getName());
            assertEquals(DataTypeKind.OBJECT, argMeta.get(6).getType().getKind());
            assertEquals(CustomObject.class.getName(), ((ObjectType) argMeta.get(6).getType()).getClassName());

            assertEquals("javaClassListArg", argMeta.get(7).getName());
            assertEquals(DataTypeKind.LIST, argMeta.get(7).getType().getKind());
            DataType elementType7 = ((ListType) argMeta.get(7).getType()).getElementType();
            assertEquals(DataTypeKind.OBJECT, elementType7.getKind());
            assertEquals(CustomObject.class.getName(), ((ObjectType) elementType7).getClassName());

            assertEquals("binaryArg", argMeta.get(8).getName());
            assertEquals(DataTypeKind.BINARY, argMeta.get(8).getType().getKind());
            assertNull(argMeta.get(8).getType().getFormat());
            assertEquals(4, argMeta.get(8).getType().getFeatures().size());
            assertEquals(28, ((Number) argMeta.get(8).getType().getFeatures().get("width")).intValue());
            assertEquals(28, ((Number) argMeta.get(8).getType().getFeatures().get("height")).intValue());
            assertEquals("black", argMeta.get(8).getType().getFeatures().get("background"));
            assertEquals("white", argMeta.get(8).getType().getFeatures().get("color"));

            assertEquals(DataTypeKind.BOOLEAN, adapter.getResultMeta().getType().getKind());
            assertEquals("Boolean result", adapter.getResultMeta().getDisplayName());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsMetadataTypesMap() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_types.py").build();
        engine.startup();

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = engine.getOperations().call(Map.class, "ActionReturningMap");

            assertEquals(3, map.size());
            assertEquals(1, map.get("a"));
            assertEquals(2, map.get("b"));
            assertEquals(3, map.get("c"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsOnConfigureError() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_on_configure_error.py").build();

        try {
            engine.startup();

            fail("Exception not thrown");
        } catch (WrappedException e) {
            logger.debug("Expected exception", e);
            String sourceName = "kb.TestAction.onConfigure";
            String expectedMessage =
                    "'org.openksavi.sponge.action.ResultMeta' object has no attribute 'displayName_error' in " + sourceName;
            String expectedToString = WrappedException.class.getName() + ": " + expectedMessage;

            assertEquals(sourceName, e.getSourceName());
            assertEquals(expectedToString, e.toString());
            assertEquals(expectedMessage, e.getMessage());

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            try (Scanner scanner = new Scanner(sw.toString())) {
                assertEquals(expectedToString, scanner.nextLine());
            }
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsOnCallError() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_on_call_error.py").build();

        try {
            engine.startup();
            engine.getOperations().call("TestAction");
            fail("Exception not thrown");
        } catch (WrappedException e) {
            logger.debug("Expected exception", e);
            String sourceName = "kb.TestAction.onCall";
            String expectedMessage = "NameError: global name 'error_here' is not defined in <script> at line number 8"
                    + " in kb.ErrorCauseAction.onCall in <script> at line number 12 in " + sourceName;
            String expectedToString = WrappedException.class.getName() + ": " + expectedMessage;

            assertEquals(sourceName, e.getSourceName());
            assertEquals(expectedToString, e.toString());
            assertEquals(expectedMessage, e.getMessage());

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            try (Scanner scanner = new Scanner(sw.toString())) {
                assertEquals(expectedToString, scanner.nextLine());
            }
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsOnCallErrorDeepNested() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_on_call_error.py").build();

        try {
            engine.startup();
            engine.getOperations().call("DeepNestedTestAction");
            fail("Exception not thrown");
        } catch (WrappedException e) {
            logger.debug("Expected exception", e);
            String sourceName = "kb.DeepNestedTestAction.onCall";
            String expectedMessage = "NameError: global name 'error_here' is not defined in <script> at line number 8"
                    + " in kb.ErrorCauseAction.onCall in <script> at line number 12 in kb.TestAction.onCall in <script>"
                    + " at line number 16 in " + sourceName;
            String expectedToString = WrappedException.class.getName() + ": " + expectedMessage;

            assertEquals(sourceName, e.getSourceName());
            assertEquals(expectedToString, e.toString());
            assertEquals(expectedMessage, e.getMessage());

            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            try (Scanner scanner = new Scanner(sw.toString())) {
                assertEquals(expectedToString, scanner.nextLine());
            }
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsMetadataOptionalArg() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_optional_arg.py").build();
        engine.startup();

        try {
            String actionName = "OptionalArgAction";
            ActionAdapter actionAdapter = engine.getActionManager().getActionAdapter(actionName);

            List<ArgMeta<?>> argMeta = actionAdapter.getArgsMeta();
            assertEquals(2, argMeta.size());
            assertEquals("mandatoryText", argMeta.get(0).getName());
            assertEquals(DataTypeKind.STRING, argMeta.get(0).getType().getKind());
            assertFalse(argMeta.get(0).isOptional());

            assertEquals("optionalText", argMeta.get(1).getName());
            assertEquals(DataTypeKind.STRING, argMeta.get(1).getType().getKind());
            assertTrue(argMeta.get(1).isOptional());

            assertEquals("text1", engine.getOperations().call(String.class, actionName, "text1"));
            assertEquals("text1text2", engine.getOperations().call(String.class, actionName, "text1", "text2"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsProvideArgs() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args.py").build();
        engine.startup();

        try {
            ActionAdapter actionAdapter = engine.getActionManager().getActionAdapter("SetActuator");
            List<ArgMeta<?>> argsMeta = actionAdapter.getArgsMeta();
            Map<String, ArgValue<?>> providedArgs;

            assertTrue(argsMeta.get(0).isProvided());
            assertEquals(0, argsMeta.get(0).getDepends().size());
            assertTrue(argsMeta.get(1).isProvided());
            assertEquals(0, argsMeta.get(1).getDepends().size());
            assertTrue(argsMeta.get(2).isProvided());
            assertEquals(0, argsMeta.get(2).getDepends().size());
            assertFalse(argsMeta.get(3).isProvided());
            assertEquals(0, argsMeta.get(3).getDepends().size());

            providedArgs = engine.getOperations().provideActionArgs(actionAdapter.getName(), null, null);
            assertEquals(3, providedArgs.size());

            assertNotNull(providedArgs.get("actuator1"));
            assertEquals("A", providedArgs.get("actuator1").getValue());
            assertEquals(Arrays.asList("A", "B", "C"), providedArgs.get("actuator1").getValueSet());
            assertTrue(providedArgs.get("actuator1").isValuePresent());

            assertNotNull(providedArgs.get("actuator2"));
            assertEquals(false, providedArgs.get("actuator2").getValue());
            assertNull(providedArgs.get("actuator2").getValueSet());
            assertTrue(providedArgs.get("actuator2").isValuePresent());

            assertNotNull(providedArgs.get("actuator3"));
            assertEquals(1, providedArgs.get("actuator3").getValue());
            assertNull(providedArgs.get("actuator3").getValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNull(providedArgs.get("actuator4"));

            engine.getOperations().call(actionAdapter.getName(), "B", true, 5, 10);

            providedArgs = engine.getOperations().provideActionArgs(actionAdapter.getName(), null, null);
            assertEquals(3, providedArgs.size());
            assertNotNull(providedArgs.get("actuator1"));
            assertEquals("B", providedArgs.get("actuator1").getValue());
            assertEquals(Arrays.asList("A", "B", "C"), providedArgs.get("actuator1").getValueSet());

            assertNotNull(providedArgs.get("actuator2"));
            assertEquals(true, providedArgs.get("actuator2").getValue());
            assertNull(providedArgs.get("actuator2").getValueSet());

            assertNotNull(providedArgs.get("actuator3"));
            assertEquals(5, providedArgs.get("actuator3").getValue());
            assertNull(providedArgs.get("actuator3").getValueSet());

            assertNull(providedArgs.get("actuator4"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsProvideArgsDepends() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args_depends.py").build();
        engine.startup();

        try {
            ActionAdapter actionAdapter = engine.getActionManager().getActionAdapter("SetActuator");
            List<ArgMeta<?>> argsMeta = actionAdapter.getArgsMeta();
            Map<String, ArgValue<?>> providedArgs;

            assertTrue(argsMeta.get(0).isProvided());
            assertEquals(0, argsMeta.get(0).getDepends().size());
            assertTrue(argsMeta.get(1).isProvided());
            assertEquals(1, argsMeta.get(1).getDepends().size());
            assertEquals("actuator1", argsMeta.get(1).getDepends().get(0));
            assertTrue(argsMeta.get(2).isProvided());
            assertEquals(0, argsMeta.get(2).getDepends().size());
            assertTrue(argsMeta.get(3).isProvided());
            assertEquals(0, argsMeta.get(3).getDepends().size());
            assertFalse(argsMeta.get(4).isProvided());
            assertEquals(0, argsMeta.get(4).getDepends().size());

            providedArgs = engine.getOperations().provideActionArgs(actionAdapter.getName(), SpongeUtils.immutableSetOf("actuator1"),
                    Collections.emptyMap());
            assertEquals(1, providedArgs.size());
            assertNotNull(providedArgs.get("actuator1"));
            Object actuator1value = providedArgs.get("actuator1").getValue();
            assertEquals("X", actuator1value);
            assertEquals(Arrays.asList("X", "Y", "Z"), providedArgs.get("actuator1").getValueSet());
            assertTrue(providedArgs.get("actuator1").isValuePresent());

            providedArgs = engine.getOperations().provideActionArgs(actionAdapter.getName(),
                    SpongeUtils.immutableSetOf("actuator2", "actuator3", "actuator4"),
                    SpongeUtils.immutableMapOf("actuator1", actuator1value));

            assertEquals(3, providedArgs.size());
            assertNotNull(providedArgs.get("actuator2"));
            assertEquals("A", providedArgs.get("actuator2").getValue());
            assertEquals(Arrays.asList("A", "B", "C", "X"), providedArgs.get("actuator2").getValueSet());
            assertTrue(providedArgs.get("actuator2").isValuePresent());

            assertNotNull(providedArgs.get("actuator3"));
            assertEquals(false, providedArgs.get("actuator3").getValue());
            assertNull(providedArgs.get("actuator3").getValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNotNull(providedArgs.get("actuator4"));
            assertNull(providedArgs.get("actuator4").getValue());
            assertEquals(Arrays.asList("a", "b", "c"), providedArgs.get("actuator4").getValueSet());
            assertFalse(providedArgs.get("actuator4").isValuePresent());

            assertNull(providedArgs.get("actuator5"));

            engine.getOperations().call(actionAdapter.getName(), "Y", "B", true, 5, 10);

            providedArgs = engine.getOperations().provideActionArgs(actionAdapter.getName(), SpongeUtils.immutableSetOf("actuator1"),
                    Collections.emptyMap());
            assertEquals(1, providedArgs.size());
            assertNotNull(providedArgs.get("actuator1"));
            actuator1value = providedArgs.get("actuator1").getValue();
            assertEquals("Y", actuator1value);
            assertEquals(Arrays.asList("X", "Y", "Z"), providedArgs.get("actuator1").getValueSet());
            assertTrue(providedArgs.get("actuator1").isValuePresent());

            providedArgs = engine.getOperations().provideActionArgs(actionAdapter.getName(),
                    SpongeUtils.immutableSetOf("actuator2", "actuator3", "actuator4"),
                    SpongeUtils.immutableMapOf("actuator1", actuator1value));

            assertEquals(3, providedArgs.size());
            assertNotNull(providedArgs.get("actuator2"));
            assertEquals("B", providedArgs.get("actuator2").getValue());
            assertEquals(Arrays.asList("A", "B", "C", "Y"), providedArgs.get("actuator2").getValueSet());
            assertTrue(providedArgs.get("actuator2").isValuePresent());

            assertNotNull(providedArgs.get("actuator3"));
            assertEquals(true, providedArgs.get("actuator3").getValue());
            assertNull(providedArgs.get("actuator3").getValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNotNull(providedArgs.get("actuator4"));
            assertNull(providedArgs.get("actuator4").getValue());
            assertEquals(Arrays.asList("a", "b", "c"), providedArgs.get("actuator4").getValueSet());
            assertFalse(providedArgs.get("actuator4").isValuePresent());

            assertNull(providedArgs.get("actuator5"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testActionsProvideArgsByAction() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args_by_action.py").build();
        engine.startup();

        try {
            ActionAdapter actionAdapter = engine.getActionManager().getActionAdapter("ProvideByAction");
            assertEquals(1, actionAdapter.getArgsMeta().size());
            ArgMeta<StringType> sensorNameArgMeta = (ArgMeta<StringType>) actionAdapter.getArgsMeta().get(0);
            assertTrue(sensorNameArgMeta.getType() instanceof StringType);
            List<String> availableSensors =
                    (List<String>) engine.getOperations().provideActionArgs(actionAdapter.getName()).get("sensorName").getValueSet();

            assertTrue(engine.getOperations().call(Boolean.class, "ProvideByAction", availableSensors.get(0)));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
