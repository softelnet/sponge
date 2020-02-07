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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.CategoryMeta;
import org.openksavi.sponge.ProcessorQualifiedVersion;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.action.ProvideArgsParameters;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.WrappedException;
import org.openksavi.sponge.examples.CustomObject;
import org.openksavi.sponge.features.Features;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.test.util.TestUtils;
import org.openksavi.sponge.type.AnyType;
import org.openksavi.sponge.type.BooleanType;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.DateTimeKind;
import org.openksavi.sponge.type.DateTimeType;
import org.openksavi.sponge.type.IntegerType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.ObjectType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.type.value.AnnotatedValue;
import org.openksavi.sponge.type.value.DynamicValue;
import org.openksavi.sponge.util.SpongeApiUtils;
import org.openksavi.sponge.util.ValueHolder;

@SuppressWarnings("rawtypes")
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
            assertEquals("Echo Action", upperActionAdapter.getMeta().getLabel());
            assertEquals("Returns the upper case string", upperActionAdapter.getMeta().getDescription());

            List<DataType> argTypes = upperActionAdapter.getMeta().getArgs();
            assertEquals(1, argTypes.size());
            assertEquals("text", argTypes.get(0).getName());
            assertEquals(DataTypeKind.STRING, argTypes.get(0).getKind());
            assertEquals(false, argTypes.get(0).isNullable());
            assertEquals("Argument 1", argTypes.get(0).getLabel());
            assertEquals("Argument 1 description", argTypes.get(0).getDescription());

            assertEquals(DataTypeKind.STRING, upperActionAdapter.getMeta().getResult().getKind());
            assertEquals("Upper case string", upperActionAdapter.getMeta().getResult().getLabel());
            assertEquals("Result description", upperActionAdapter.getMeta().getResult().getDescription());

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
            ActionMeta actionMeta = engine.getActionMeta("MultipleArgumentsAction");
            assertEquals("Multiple arguments action", actionMeta.getLabel());
            assertEquals("Multiple arguments action.", actionMeta.getDescription());

            List<DataType> argTypes = actionMeta.getArgs();
            assertEquals(11, argTypes.size());

            assertEquals("stringArg", argTypes.get(0).getName());
            assertEquals(DataTypeKind.STRING, argTypes.get(0).getKind());
            assertEquals(10, ((StringType) argTypes.get(0)).getMaxLength().intValue());
            assertEquals("ipAddress", argTypes.get(0).getFormat());
            assertEquals(false, argTypes.get(0).isNullable());
            assertEquals(null, argTypes.get(0).getLabel());
            assertEquals(null, argTypes.get(0).getDescription());
            assertNull(argTypes.get(0).getDefaultValue());

            assertEquals("integerArg", argTypes.get(1).getName());
            assertEquals(DataTypeKind.INTEGER, argTypes.get(1).getKind());
            assertEquals(1, ((IntegerType) argTypes.get(1)).getMinValue().intValue());
            assertEquals(100, ((IntegerType) argTypes.get(1)).getMaxValue().intValue());
            assertEquals(50, argTypes.get(1).getDefaultValue());

            assertEquals("anyArg", argTypes.get(2).getName());
            assertEquals(DataTypeKind.ANY, argTypes.get(2).getKind());
            assertTrue(argTypes.get(2) instanceof AnyType);
            assertEquals(true, argTypes.get(2).isNullable());

            assertEquals("stringListArg", argTypes.get(3).getName());
            assertEquals(DataTypeKind.LIST, argTypes.get(3).getKind());
            assertEquals(DataTypeKind.STRING, ((ListType) argTypes.get(3)).getElementType().getKind());

            assertEquals("decimalListArg", argTypes.get(4).getName());
            assertEquals(DataTypeKind.LIST, argTypes.get(4).getKind());
            DataType elementType4 = ((ListType) argTypes.get(4)).getElementType();
            assertEquals(DataTypeKind.OBJECT, elementType4.getKind());
            assertEquals(BigDecimal.class.getName(), ((ObjectType) elementType4).getClassName());

            assertEquals("stringArrayArg", argTypes.get(5).getName());
            assertEquals(DataTypeKind.OBJECT, argTypes.get(5).getKind());
            assertEquals(String[].class, SpongeUtils.getClass(((ObjectType) argTypes.get(5)).getClassName()));

            assertEquals("javaClassArg", argTypes.get(6).getName());
            assertEquals(DataTypeKind.OBJECT, argTypes.get(6).getKind());
            assertEquals(CustomObject.class.getName(), ((ObjectType) argTypes.get(6)).getClassName());

            assertEquals("javaClassListArg", argTypes.get(7).getName());
            assertEquals(DataTypeKind.LIST, argTypes.get(7).getKind());
            DataType elementType7 = ((ListType) argTypes.get(7)).getElementType();
            assertEquals(DataTypeKind.OBJECT, elementType7.getKind());
            assertEquals(CustomObject.class.getName(), ((ObjectType) elementType7).getClassName());

            assertEquals("binaryArg", argTypes.get(8).getName());
            assertEquals(DataTypeKind.BINARY, argTypes.get(8).getKind());
            assertNull(argTypes.get(8).getFormat());
            assertEquals(4, argTypes.get(8).getFeatures().size());
            assertEquals(28, ((Number) argTypes.get(8).getFeatures().get("width")).intValue());
            assertEquals(28, ((Number) argTypes.get(8).getFeatures().get("height")).intValue());
            assertEquals("black", argTypes.get(8).getFeatures().get("background"));
            assertEquals("white", argTypes.get(8).getFeatures().get("color"));

            assertEquals("typeArg", argTypes.get(9).getName());
            assertEquals(DataTypeKind.TYPE, argTypes.get(9).getKind());

            assertEquals("dynamicArg", argTypes.get(10).getName());
            assertEquals(DataTypeKind.DYNAMIC, argTypes.get(10).getKind());

            assertEquals(DataTypeKind.BOOLEAN, actionMeta.getResult().getKind());
            assertEquals("Boolean result", actionMeta.getResult().getLabel());

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
            String expectedMessage = "Traceback (most recent call last):\n"
                    + "  File \"examples/core/actions_on_configure_error.py\", line 8, in onConfigure\n"
                    + "    self.withNoArgs().withResult(StringType().label_error(\"Test action\"))\n"
                    + "AttributeError: 'org.openksavi.sponge.type.StringType' object has no attribute 'label_error'\n" + " in "
                    + sourceName;
//            String expectedMessage =
//                    "AttributeError: 'org.openksavi.sponge.type.StringType' object has no attribute 'label_error' in " + sourceName;
            String expectedToString = WrappedException.class.getName() + ": " + expectedMessage;

            assertEquals(sourceName, e.getSourceName());
            assertEquals(expectedToString, e.toString());
            assertEquals(expectedMessage, e.getMessage());
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
            String expectedMessage =
                    "NameError: global name 'error_here' is not defined in examples/core/actions_on_call_error.py at line number 8"
                            + " in kb.ErrorCauseAction.onCall in examples/core/actions_on_call_error.py at line number 12 in " + sourceName;
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
            String expectedMessage =
                    "NameError: global name 'error_here' is not defined in examples/core/actions_on_call_error.py at line number 8"
                            + " in kb.ErrorCauseAction.onCall in examples/core/actions_on_call_error.py at line number 12 in kb.TestAction.onCall"
                            + " in examples/core/actions_on_call_error.py" + " at line number 16 in " + sourceName;
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

            List<DataType> argTypes = actionAdapter.getMeta().getArgs();
            assertEquals(2, argTypes.size());
            assertEquals("mandatoryText", argTypes.get(0).getName());
            assertEquals(DataTypeKind.STRING, argTypes.get(0).getKind());
            assertFalse(argTypes.get(0).isOptional());

            assertEquals("optionalText", argTypes.get(1).getName());
            assertEquals(DataTypeKind.STRING, argTypes.get(1).getKind());
            assertTrue(argTypes.get(1).isOptional());

            assertEquals("text1", engine.getOperations().call(String.class, actionName, Arrays.asList("text1")));
            assertEquals("text1text2", engine.getOperations().call(String.class, actionName, Arrays.asList("text1", "text2")));

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
            ActionMeta actionMeta = engine.getActionMeta("SetActuator");
            List<DataType> argTypes = actionMeta.getArgs();
            Map<String, ProvidedValue<?>> providedArgs;

            assertNotNull(argTypes.get(0).getProvided());
            assertTrue(argTypes.get(0).getProvided().isValue());
            assertTrue(argTypes.get(0).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(0).getProvided().getDependencies().size());
            assertFalse(argTypes.get(0).getProvided().isReadOnly());
            assertNotNull(argTypes.get(1).getProvided());
            assertTrue(argTypes.get(1).getProvided().isValue());
            assertFalse(argTypes.get(1).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(1).getProvided().getDependencies().size());
            assertFalse(argTypes.get(1).getProvided().isReadOnly());
            assertNotNull(argTypes.get(2).getProvided());
            assertTrue(argTypes.get(2).getProvided().isValue());
            assertFalse(argTypes.get(2).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(2).getProvided().getDependencies().size());
            assertTrue(argTypes.get(2).getProvided().isReadOnly());
            assertNull(argTypes.get(3).getProvided());

            providedArgs = engine.getOperations().provideActionArgs(actionMeta.getName(),
                    new ProvideArgsParameters().withProvide(Arrays.asList("actuator1", "actuator2", "actuator3")));
            assertEquals(3, providedArgs.size());

            assertNotNull(providedArgs.get("actuator1"));
            assertEquals("A", providedArgs.get("actuator1").getValue());
            assertEquals(Arrays.asList("A", "B", "C"),
                    SpongeApiUtils.unwrapAnnotatedValueList(providedArgs.get("actuator1").getAnnotatedValueSet()));
            assertTrue(providedArgs.get("actuator1").isValuePresent());

            assertNotNull(providedArgs.get("actuator2"));
            assertEquals(false, providedArgs.get("actuator2").getValue());
            assertNull(providedArgs.get("actuator2").getAnnotatedValueSet());
            assertTrue(providedArgs.get("actuator2").isValuePresent());

            assertNotNull(providedArgs.get("actuator3"));
            assertEquals(1, providedArgs.get("actuator3").getValue());
            assertNull(providedArgs.get("actuator3").getAnnotatedValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNull(providedArgs.get("actuator4"));

            engine.getOperations().call(actionMeta.getName(), Arrays.asList("B", true, null, 10));

            providedArgs = engine.getOperations().provideActionArgs(actionMeta.getName(),
                    new ProvideArgsParameters().withProvide(Arrays.asList("actuator1", "actuator2", "actuator3")));
            assertEquals(3, providedArgs.size());
            assertNotNull(providedArgs.get("actuator1"));
            assertEquals("B", providedArgs.get("actuator1").getValue());
            assertEquals(Arrays.asList("A", "B", "C"),
                    SpongeApiUtils.unwrapAnnotatedValueList(providedArgs.get("actuator1").getAnnotatedValueSet()));

            assertNotNull(providedArgs.get("actuator2"));
            assertEquals(true, providedArgs.get("actuator2").getValue());
            assertNull(providedArgs.get("actuator2").getAnnotatedValueSet());

            assertNotNull(providedArgs.get("actuator3"));
            assertEquals(1, providedArgs.get("actuator3").getValue());
            assertNull(providedArgs.get("actuator3").getAnnotatedValueSet());

            assertNull(providedArgs.get("actuator4"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsProvideArgsAnnotatedValueSet() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("SetActuatorAnnotatedValueSet");
            List<DataType> argTypes = actionMeta.getArgs();
            Map<String, ProvidedValue<?>> providedArgs;

            assertNotNull(argTypes.get(0).getProvided());
            assertTrue(argTypes.get(0).getProvided().isValue());
            assertTrue(argTypes.get(0).getProvided().hasValueSet());
            assertTrue(argTypes.get(0).getProvided().getValueSet().isLimited());
            assertEquals(0, argTypes.get(0).getProvided().getDependencies().size());
            assertFalse(argTypes.get(0).getProvided().isReadOnly());

            providedArgs = engine.getOperations().provideActionArgs(actionMeta.getName(),
                    new ProvideArgsParameters().withProvide(Arrays.asList("actuatorType")));
            assertEquals(1, providedArgs.size());

            ProvidedValue<?> argValue = providedArgs.get("actuatorType");
            assertNotNull(argValue);
            assertEquals("auto", argValue.getValue());
            assertEquals(2, argValue.getAnnotatedValueSet().size());
            assertEquals("auto", argValue.getAnnotatedValueSet().get(0).getValue());
            assertEquals("Auto", argValue.getAnnotatedValueSet().get(0).getValueLabel());
            assertEquals("manual", argValue.getAnnotatedValueSet().get(1).getValue());
            assertEquals("Manual", argValue.getAnnotatedValueSet().get(1).getValueLabel());
            assertTrue(argValue.isValuePresent());

            engine.getOperations().call(actionMeta.getName(), Arrays.asList("manual"));

            providedArgs = engine.getOperations().provideActionArgs(actionMeta.getName(),
                    new ProvideArgsParameters().withProvide(Arrays.asList("actuatorType")));
            assertEquals(1, providedArgs.size());

            argValue = providedArgs.get("actuatorType");
            assertNotNull(argValue);
            assertEquals("manual", argValue.getValue());
            assertEquals(2, argValue.getAnnotatedValueSet().size());
            assertEquals("auto", argValue.getAnnotatedValueSet().get(0).getValue());
            assertEquals("Auto", argValue.getAnnotatedValueSet().get(0).getValueLabel());
            assertEquals("manual", argValue.getAnnotatedValueSet().get(1).getValue());
            assertEquals("Manual", argValue.getAnnotatedValueSet().get(1).getValueLabel());
            assertTrue(argValue.isValuePresent());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsProvideArgsValueSetNotLimited() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("SetActuatorNotLimitedValueSet");
            List<DataType> argTypes = actionMeta.getArgs();

            assertNotNull(argTypes.get(0).getProvided());
            assertTrue(argTypes.get(0).getProvided().isValue());
            assertTrue(argTypes.get(0).getProvided().hasValueSet());
            assertFalse(argTypes.get(0).getProvided().getValueSet().isLimited());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testActionsProvideArgsDepends() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args_depends.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("SetActuator");
            List<DataType> argTypes = actionMeta.getArgs();
            Map<String, ProvidedValue<?>> providedArgs;

            assertNotNull(argTypes.get(0).getProvided());
            assertTrue(argTypes.get(0).getProvided().isValue());
            assertTrue(argTypes.get(0).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(0).getProvided().getDependencies().size());
            assertNotNull(argTypes.get(1).getProvided());
            assertTrue(argTypes.get(1).getProvided().isValue());
            assertFalse(argTypes.get(1).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(1).getProvided().getDependencies().size());
            assertNotNull(argTypes.get(2).getProvided());
            assertTrue(argTypes.get(2).getProvided().isValue());
            assertFalse(argTypes.get(2).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(2).getProvided().getDependencies().size());
            assertTrue(argTypes.get(2).isNullable());
            assertNull(argTypes.get(3).getProvided());
            assertNotNull(argTypes.get(4).getProvided());
            assertTrue(argTypes.get(4).getProvided().isValue());
            assertTrue(argTypes.get(4).getProvided().hasValueSet());
            assertEquals(1, argTypes.get(4).getProvided().getDependencies().size());
            assertEquals("actuator1", argTypes.get(4).getProvided().getDependencies().get(0));

            providedArgs = engine.getOperations().provideActionArgs(actionMeta.getName(),
                    new ProvideArgsParameters().withProvide(Arrays.asList("actuator1")));
            assertEquals(1, providedArgs.size());
            assertNotNull(providedArgs.get("actuator1"));
            Object actuator1value = providedArgs.get("actuator1").getValue();
            assertEquals("A", actuator1value);
            assertEquals(Arrays.asList("A", "B", "C"),
                    SpongeApiUtils.unwrapAnnotatedValueList(providedArgs.get("actuator1").getAnnotatedValueSet()));
            List<AnnotatedValue<?>> actuator1AnnotatedValueSet = ((ProvidedValue) providedArgs.get("actuator1")).getAnnotatedValueSet();
            assertEquals(3, actuator1AnnotatedValueSet.size());
            assertEquals("A", actuator1AnnotatedValueSet.get(0).getValue());
            assertEquals("Value A", actuator1AnnotatedValueSet.get(0).getValueLabel());
            assertEquals("B", actuator1AnnotatedValueSet.get(1).getValue());
            assertEquals("Value B", actuator1AnnotatedValueSet.get(1).getValueLabel());
            assertEquals("C", actuator1AnnotatedValueSet.get(2).getValue());
            assertEquals("Value C", actuator1AnnotatedValueSet.get(2).getValueLabel());

            assertTrue(providedArgs.get("actuator1").isValuePresent());

            providedArgs = engine.getOperations().provideActionArgs(actionMeta.getName(),
                    new ProvideArgsParameters().withProvide(Arrays.asList("actuator2", "actuator3", "actuator5"))
                            .withCurrent(SpongeUtils.immutableMapOf("actuator1", actuator1value)));
            assertEquals(3, providedArgs.size());

            assertNotNull(providedArgs.get("actuator2"));
            assertEquals(false, providedArgs.get("actuator2").getValue());
            assertNull(providedArgs.get("actuator2").getAnnotatedValueSet());
            assertTrue(providedArgs.get("actuator2").isValuePresent());

            assertNotNull(providedArgs.get("actuator3"));
            assertEquals(1, providedArgs.get("actuator3").getValue());
            assertNull(providedArgs.get("actuator3").getAnnotatedValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNull(providedArgs.get("actuator4"));

            assertNotNull(providedArgs.get("actuator5"));
            assertEquals("X", providedArgs.get("actuator5").getValue());
            assertEquals(Arrays.asList("X", "Y", "Z", "A"),
                    SpongeApiUtils.unwrapAnnotatedValueList(providedArgs.get("actuator5").getAnnotatedValueSet()));
            assertTrue(providedArgs.get("actuator5").isValuePresent());

            engine.getOperations().call(actionMeta.getName(), Arrays.asList("B", true, null, 10, "Y"));

            providedArgs = engine.getOperations().provideActionArgs(actionMeta.getName(),
                    new ProvideArgsParameters().withProvide(Arrays.asList("actuator1")));
            assertEquals(1, providedArgs.size());
            assertNotNull(providedArgs.get("actuator1"));
            actuator1value = providedArgs.get("actuator1").getValue();
            assertEquals("B", actuator1value);
            assertEquals(Arrays.asList("A", "B", "C"),
                    SpongeApiUtils.unwrapAnnotatedValueList(providedArgs.get("actuator1").getAnnotatedValueSet()));
            assertTrue(providedArgs.get("actuator1").isValuePresent());

            providedArgs = engine.getOperations().provideActionArgs(actionMeta.getName(),
                    new ProvideArgsParameters().withProvide(Arrays.asList("actuator2", "actuator3", "actuator5"))
                            .withCurrent(SpongeUtils.immutableMapOf("actuator1", actuator1value)));

            assertEquals(3, providedArgs.size());

            assertNotNull(providedArgs.get("actuator2"));
            assertEquals(true, providedArgs.get("actuator2").getValue());
            assertNull(providedArgs.get("actuator2").getAnnotatedValueSet());
            assertTrue(providedArgs.get("actuator2").isValuePresent());

            assertNotNull(providedArgs.get("actuator3"));
            assertEquals(1, providedArgs.get("actuator3").getValue());
            assertNull(providedArgs.get("actuator3").getAnnotatedValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNull(providedArgs.get("actuator4"));

            assertNotNull(providedArgs.get("actuator5"));
            assertEquals("Y", providedArgs.get("actuator5").getValue());
            assertEquals(Arrays.asList("X", "Y", "Z", "B"),
                    SpongeApiUtils.unwrapAnnotatedValueList(providedArgs.get("actuator5").getAnnotatedValueSet()));
            assertTrue(providedArgs.get("actuator5").isValuePresent());

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
            ActionMeta actionMeta = engine.getActionMeta("ProvideByAction");
            assertEquals(1, actionMeta.getArgs().size());
            DataType sensorNameArgType = actionMeta.getArgs().get(0);
            assertTrue(sensorNameArgType instanceof StringType);
            List<String> availableSensors = (List<String>) SpongeApiUtils.unwrapAnnotatedValueList(engine.getOperations()
                    .provideActionArgs(actionMeta.getName(), new ProvideArgsParameters().withProvide(Arrays.asList("sensorName")))
                    .get("sensorName").getAnnotatedValueSet());

            assertTrue(engine.getOperations().call(Boolean.class, "ProvideByAction", Arrays.asList(availableSensors.get(0))));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsProvideArgsElementValueSet() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args_element_value_set.py").build();
        engine.startup();

        try {
            String actionName = "FruitsElementValueSetAction";

            ListType fruitsType = (ListType) engine.getOperations().getActionMeta(actionName).getArgs().get(0);
            assertTrue(fruitsType.isUnique());
            assertNotNull(fruitsType.getProvided());
            assertFalse(fruitsType.getProvided().isValue());
            assertFalse(fruitsType.getProvided().hasValueSet());
            assertTrue(fruitsType.getProvided().isElementValueSet());

            Map<String, ProvidedValue<?>> provided =
                    engine.getOperations().provideActionArgs(actionName, new ProvideArgsParameters().withProvide(Arrays.asList("fruits")));
            List<AnnotatedValue> elementValueSet = provided.get("fruits").getAnnotatedElementValueSet();
            assertEquals(3, elementValueSet.size());
            assertEquals("apple", elementValueSet.get(0).getValue());
            assertEquals("Apple", elementValueSet.get(0).getValueLabel());
            assertEquals("banana", elementValueSet.get(1).getValue());
            assertEquals("Banana", elementValueSet.get(1).getValueLabel());
            assertEquals("lemon", elementValueSet.get(2).getValue());
            assertEquals("Lemon", elementValueSet.get(2).getValueLabel());

            assertEquals(2, engine.getOperations().call(actionName, Arrays.asList(Arrays.asList("apple", "lemon"))));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsCallListArg() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_call_list_arg.py").build();
        engine.startup();

        try {
            List<Object> listArg = Arrays.asList("aa", "bb", "cc", 1, 2, 3);
            Object result = engine.getOperations().call("ListArgAction", Arrays.asList("A", 1, listArg));
            assertTrue(result instanceof List);
            assertEquals(listArg, result);

            ActionAdapter actionAdapter = engine.getActionManager().getActionAdapter("ListArgAction");
            result = ((ScriptKnowledgeBaseInterpreter) actionAdapter.getKnowledgeBase().getInterpreter())
                    .eval("sponge.call(\"ListArgAction\", [\"A\", 1, [\"aa\", \"bb\", \"cc\", 1, 2, 3]])");
            assertTrue(result instanceof List);
            assertEquals(listArg, result);

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsProvideArgNoOverwrite() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args_overwrite.py").build();
        engine.startup();

        String value = "CLIENT_VALUE";

        try {
            ActionMeta actionMeta = engine.getActionMeta("ProvideArgNoOverwrite");
            assertEquals(1, actionMeta.getArgs().size());
            DataType argType = actionMeta.getArgs().get(0);
            assertFalse(argType.getProvided().isOverwrite());
            ProvidedValue<?> argValue = engine.getOperations()
                    .provideActionArgs(actionMeta.getName(), new ProvideArgsParameters().withProvide(Arrays.asList("value"))).get("value");
            String providedValue = (String) argValue.getValue();
            assertEquals("PROVIDED", providedValue);

            if (argValue.isValuePresent() && argType.getProvided().isOverwrite()) {
                value = providedValue;
            }

            assertEquals("CLIENT_VALUE", value);

            engine.getOperations().call(actionMeta.getName(), Arrays.asList(value));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsProvideArgOverwrite() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args_overwrite.py").build();
        engine.startup();

        String value = "CLIENT_VALUE";

        try {
            ActionMeta actionMeta = engine.getActionMeta("ProvideArgOverwrite");
            assertEquals(1, actionMeta.getArgs().size());
            DataType argType = actionMeta.getArgs().get(0);
            assertTrue(argType.getProvided().isOverwrite());
            ProvidedValue<?> argValue = engine.getOperations()
                    .provideActionArgs(actionMeta.getName(), new ProvideArgsParameters().withProvide(Arrays.asList("value"))).get("value");
            String providedValue = (String) argValue.getValue();
            assertEquals("PROVIDED", providedValue);

            if (argValue.isValuePresent() && argType.getProvided().isOverwrite()) {
                value = providedValue;
            }

            assertEquals("PROVIDED", value);

            engine.getOperations().call(actionMeta.getName(), Arrays.asList(value));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testActionsMetadataAnnotatedType() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_annotated_type.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("AnnotatedTypeAction");
            assertEquals(0, actionMeta.getArgs().size());

            DataType resultType = actionMeta.getResult();
            assertEquals(DataTypeKind.STRING, resultType.getKind());
            assertTrue(resultType.isAnnotated());

            AnnotatedValue<String> result = engine.getOperations().call(AnnotatedValue.class, actionMeta.getName());
            assertEquals("RESULT", result.getValue());
            assertEquals("Result value", result.getValueLabel());
            assertEquals("Result value description", result.getValueDescription());

            assertEquals(2, result.getFeatures().size());
            assertEquals("value1", result.getFeatures().get("feature1"));
            assertEquals("value2", result.getFeatures().get("feature2"));

            assertEquals("Result type", result.getTypeLabel());
            assertEquals("Result type description", result.getTypeDescription());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testCallIfExists() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata.py").build();
        engine.startup();

        try {
            ValueHolder<String> stringHolder = engine.getOperations().callIfExists(String.class, "UpperEchoAction", Arrays.asList("text"));
            assertNotNull(stringHolder);
            assertEquals("TEXT", stringHolder.getValue());

            assertNull(engine.getOperations().callIfExists(String.class, "UpperEchoAction_Nonexisting", Arrays.asList("text")));

            ValueHolder<Object> objectHolder = engine.getOperations().callIfExists("UpperEchoAction", Arrays.asList("text"));
            assertNotNull(objectHolder);
            assertEquals("TEXT", objectHolder.getValue());

            assertNull(engine.getOperations().callIfExists("UpperEchoAction_Nonexisting", Arrays.asList("text")));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionVersion() {
        SpongeEngine engine = DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_version.py").build();
        engine.startup();

        try {
            assertEquals(new Integer(12), engine.getActionMeta("VersionedAction").getVersion());
            assertNull(engine.getActionMeta("NonVersionedAction").getVersion());

            assertEquals(new ProcessorQualifiedVersion(null, 12),
                    engine.getActionManager().getActionAdapter("VersionedAction").getQualifiedVersion());
            assertEquals(new ProcessorQualifiedVersion(null, null),
                    engine.getActionManager().getActionAdapter("NonVersionedAction").getQualifiedVersion());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionVersionKnowledgeBaseVersion() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_version_kb_version.py").build();
        engine.startup();

        try {
            assertEquals(new Integer(12), engine.getActionMeta("VersionedAction").getVersion());
            assertNull(engine.getActionMeta("NonVersionedAction").getVersion());

            assertEquals(new ProcessorQualifiedVersion(2, 12),
                    engine.getActionManager().getActionAdapter("VersionedAction").getQualifiedVersion());
            assertEquals(new ProcessorQualifiedVersion(2, null),
                    engine.getActionManager().getActionAdapter("NonVersionedAction").getQualifiedVersion());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionCategory() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_category.py").build();
        engine.startup();

        try {
            assertEquals("myActions", engine.getActionMeta("MyAction1").getCategory());
            assertEquals("myActions", engine.getActionMeta("MyAction2").getCategory());
            assertEquals("yourActions", engine.getActionMeta("YourAction1").getCategory());
            assertNull(engine.getActionMeta("OtherAction").getCategory());

            assertEquals(3, engine.getCategories().size());

            CategoryMeta myActionsCategory = engine.getCategory("myActions");
            assertEquals("myActions", myActionsCategory.getName());
            assertEquals("My actions", myActionsCategory.getLabel());
            assertEquals("My actions description", myActionsCategory.getDescription());

            CategoryMeta yourActionsCategory = engine.getCategory("yourActions");
            assertEquals("yourActions", yourActionsCategory.getName());
            assertEquals("Your actions", yourActionsCategory.getLabel());
            assertEquals("Your actions description", yourActionsCategory.getDescription());

            CategoryMeta notUsedCategory = engine.getCategory("notUsedCategory");
            assertEquals("notUsedCategory", notUsedCategory.getName());
            assertNull(notUsedCategory.getLabel());
            assertNull(notUsedCategory.getDescription());

            try {
                engine.removeCategory("myActions");
                fail("Exception expected");
            } catch (Throwable e) {
                assertTrue(e instanceof IllegalArgumentException);
            }

            engine.removeCategory("notUsedCategory");

            assertEquals(2, engine.getCategories().size());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionCategorySelection() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_category_selection.py").build();
        engine.startup();

        try {
            assertEquals("myActions", engine.getActionMeta("MyAction1").getCategory());
            assertEquals("myActions", engine.getActionMeta("MyAction2").getCategory());
            assertEquals("yourActions", engine.getActionMeta("YourAction1").getCategory());
            assertNull(engine.getActionMeta("OtherAction").getCategory());

            assertEquals(3, engine.getCategories().size());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionCategoryInvalid() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_category_invalid.py").build();
        try {
            engine.startup();
            fail("Exception expected");
        } catch (Throwable e) {
            int exceptionIndex = ExceptionUtils.indexOfThrowable(e, NullPointerException.class);
            assertTrue(exceptionIndex > -1);
            assertEquals("Category yourActions is not registered", ExceptionUtils.getThrowableList(e).get(exceptionIndex).getMessage());
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testActionsMetadataDynamicTypes() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_types_dynamic.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("DynamicResultAction");
            DataType resultType = actionMeta.getResult();
            assertEquals(DataTypeKind.DYNAMIC, resultType.getKind());

            DynamicValue<String> resultForString =
                    engine.getOperations().call(DynamicValue.class, actionMeta.getName(), Arrays.asList("string"));
            assertEquals("text", resultForString.getValue());
            assertEquals(DataTypeKind.STRING, resultForString.getType().getKind());

            DynamicValue<String> resultForBoolean =
                    engine.getOperations().call(DynamicValue.class, actionMeta.getName(), Arrays.asList("boolean"));
            assertEquals(true, resultForBoolean.getValue());
            assertEquals(DataTypeKind.BOOLEAN, resultForBoolean.getType().getKind());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsMetadataTypeTypes() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_types_dynamic.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("TypeResultAction");
            DataType resultType = actionMeta.getResult();
            assertEquals(DataTypeKind.TYPE, resultType.getKind());

            assertTrue(engine.getOperations().call(DataType.class, actionMeta.getName(), Arrays.asList("string")) instanceof StringType);
            assertTrue(engine.getOperations().call(DataType.class, actionMeta.getName(), Arrays.asList("boolean")) instanceof BooleanType);

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsMetadataDateTimeType() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_types_datetime.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("DateTimeAction");
            assertEquals(DateTimeKind.DATE_TIME, ((DateTimeType) actionMeta.getArgs().get(0)).getDateTimeKind());
            assertEquals(DateTimeKind.DATE_TIME_ZONE, ((DateTimeType) actionMeta.getArgs().get(1)).getDateTimeKind());
            assertEquals(DateTimeKind.DATE, ((DateTimeType) actionMeta.getArgs().get(2)).getDateTimeKind());
            assertEquals(DateTimeKind.TIME, ((DateTimeType) actionMeta.getArgs().get(3)).getDateTimeKind());
            assertEquals(DateTimeKind.INSTANT, ((DateTimeType) actionMeta.getArgs().get(4)).getDateTimeKind());

            LocalDateTime dateTime = LocalDateTime.now();
            ZonedDateTime dateTimeZone = ZonedDateTime.now();
            LocalDate date = LocalDate.now();
            LocalTime time = LocalTime.now();
            Instant instant = Instant.now();

            List dates = engine.getOperations().call(List.class, actionMeta.getName(),
                    Arrays.asList(dateTime, dateTimeZone, date, time, instant));
            assertTrue(dates.get(0) instanceof LocalDateTime);
            assertEquals(dateTime, dates.get(0));
            assertTrue(dates.get(1) instanceof ZonedDateTime);
            assertEquals(dateTimeZone, dates.get(1));
            assertTrue(dates.get(2) instanceof LocalDate);
            assertEquals(date, dates.get(2));
            assertTrue(dates.get(3) instanceof LocalTime);
            assertEquals(time, dates.get(3));
            assertTrue(dates.get(4) instanceof Instant);
            assertEquals(instant, dates.get(4));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsConfigureBuilderStyle() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_configure_builder_style.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("UpperEchoAction");
            assertEquals("UpperEchoAction", actionMeta.getName());
            assertEquals("Echo Action", actionMeta.getLabel());
            assertEquals("Returns the upper case string", actionMeta.getDescription());
            DataType argType = actionMeta.getArgs().get(0);
            assertEquals(DataTypeKind.STRING, argType.getKind());

            assertEquals("Echo Action returns: TEXT",
                    engine.getOperations().call(String.class, actionMeta.getName(), Arrays.asList("Text")));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsConfigureBuilderStyleChangedName() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_configure_builder_style.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("UpperAction");
            assertEquals("UpperAction", actionMeta.getName());
            assertEquals("Echo Action", actionMeta.getLabel());
            assertEquals("Returns the upper case string", actionMeta.getDescription());
            DataType argType = actionMeta.getArgs().get(0);
            assertEquals(DataTypeKind.STRING, argType.getKind());

            assertEquals("Echo Action returns: TEXT",
                    engine.getOperations().call(String.class, actionMeta.getName(), Arrays.asList("Text")));

            assertNull(engine.getActionManager().getActionAdapter("UpperEchoChangedNameAction"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testActionsMetadataRecordType() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_types_record.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("RecordAsResultAction");
            RecordType recordType = (RecordType) actionMeta.getResult();
            assertEquals(DataTypeKind.RECORD, recordType.getKind());
            assertEquals("book", recordType.getName());
            assertEquals(3, recordType.getFields().size());
            assertEquals("id", recordType.getFields().get(0).getName());
            assertEquals(DataTypeKind.INTEGER, recordType.getFields().get(0).getKind());
            assertEquals(DataTypeKind.STRING, recordType.getFields().get(1).getKind());
            assertEquals(DataTypeKind.STRING, recordType.getFields().get(2).getKind());

            Map<String, Object> book1 = engine.getOperations().call(Map.class, actionMeta.getName(), Arrays.asList(1));
            assertEquals(3, book1.size());
            assertEquals(1, book1.get("id"));
            assertEquals("James Joyce", book1.get("author"));
            assertEquals("Ulysses", book1.get("title"));

            actionMeta = engine.getActionMeta("RecordAsArgAction");
            recordType = (RecordType) actionMeta.getArgs().get(0);
            assertEquals(DataTypeKind.RECORD, recordType.getKind());
            assertEquals(3, recordType.getFields().size());
            assertEquals("id", recordType.getFields().get(0).getName());
            assertEquals(DataTypeKind.INTEGER, recordType.getFields().get(0).getKind());
            assertEquals(DataTypeKind.STRING, recordType.getFields().get(1).getKind());
            assertEquals(DataTypeKind.STRING, recordType.getFields().get(2).getKind());

            Map<String, Object> book2 =
                    SpongeUtils.immutableMapOf("author", "Arthur Conan Doyle", "title", "Adventures of Sherlock Holmes");

            engine.getOperations().call("RecordAsArgAction", Arrays.asList(book2));

            Map<String, Object> book3 = engine.getOperations().call(Map.class, "RecordAsResultAction", Arrays.asList(1));
            assertEquals(3, book3.size());
            assertEquals(1, book3.get("id"));
            assertEquals(book2.get("author"), book3.get("author"));
            assertEquals(book2.get("title"), book3.get("title"));

            assertNull(engine.getOperations().call(Map.class, "RecordAsResultAction", Arrays.asList(5)));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testActionsMetadataRecordTypeSubArgs() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_types_record_subargs.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("UpdateBook");
            RecordType argType = (RecordType) actionMeta.getArgs().get(0);

            assertEquals(3, argType.getFields().size());

            DataType bookIdType = argType.getFields().get(0);
            assertEquals(DataTypeKind.INTEGER, bookIdType.getKind());
            assertEquals("id", bookIdType.getName());

            DataType bookAuthorType = argType.getFields().get(1);
            assertEquals(DataTypeKind.STRING, bookAuthorType.getKind());
            assertEquals("author", bookAuthorType.getName());

            DataType bookTitleType = argType.getFields().get(2);
            assertEquals(DataTypeKind.STRING, bookTitleType.getKind());
            assertEquals("title", bookTitleType.getName());

            Map<String, Object> book =
                    new LinkedHashMap<>(SpongeUtils.immutableMapOf("id", 1, "author", "James Joyce", "title", "Ulysses"));
            Map<String, ProvidedValue<?>> provideActionArgs =
                    engine.getOperations().provideActionArgs("UpdateBook", new ProvideArgsParameters()
                            .withProvide(Arrays.asList("book", "book.author")).withCurrent(SpongeUtils.immutableMapOf("book.id", 5)));
            List authorValueSet = SpongeApiUtils.unwrapAnnotatedValueList(provideActionArgs.get("book.author").getAnnotatedValueSet());
            assertEquals("James Joyce", authorValueSet.get(0));
            assertEquals("Arthur Conan Doyle", authorValueSet.get(1));

            Map<String, Object> providedBook = ((AnnotatedValue<Map<String, Object>>) provideActionArgs.get("book").getValue()).getValue();
            assertEquals(5, providedBook.get("id"));
            assertEquals("James Joyce", providedBook.get("author"));
            assertEquals("Ulysses", providedBook.get("title"));

            book.put("author", "Arthur Conan Doyle");
            engine.getOperations().call("UpdateBook", Arrays.asList(book));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    protected void assertObjectTypeWithRecord(ObjectType type) {
        assertEquals(CustomObject.class.getName(), type.getClassName());
        RecordType argRecordType = (RecordType) type.getCompanionType();
        assertEquals(2, argRecordType.getFields().size());

        assertTrue(argRecordType.getFields().get(0) instanceof IntegerType);
        assertEquals("id", argRecordType.getFields().get(0).getName());
        assertEquals("ID", argRecordType.getFields().get(0).getLabel());

        assertTrue(argRecordType.getFields().get(1) instanceof StringType);
        assertEquals("name", argRecordType.getFields().get(1).getName());
        assertEquals("Name", argRecordType.getFields().get(1).getLabel());
    }

    @Test
    public void testActionsMetadataObjectTypeWithRecord() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_types_object.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("ObjectTypeWithCompanionTypeAction");
            assertEquals(1, actionMeta.getArgs().size());
            assertObjectTypeWithRecord((ObjectType) actionMeta.getArgs().get(0));
            assertObjectTypeWithRecord((ObjectType) actionMeta.getResult());

            CustomObject arg = new CustomObject();
            arg.setId(1L);
            arg.setName("Name 1");
            CustomObject result = engine.getOperations().call(CustomObject.class, actionMeta.getName(), Arrays.asList(arg));
            assertEquals(arg.getId(), result.getId());
            assertEquals(arg.getName().toUpperCase(), result.getName());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testProvidedArgNoCallAction() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args.py").build();
        engine.startup();

        ActionMeta actionMeta = engine.getActionMeta("ProvidedArgNoCallAction");

        try {
            assertFalse(actionMeta.isCallable());

            try {
                engine.getOperations().call(actionMeta.getName(), Arrays.asList("value1"));
            } catch (SpongeException e) {
                assertTrue(ExceptionUtils.indexOfThrowable(e, NoSuchMethodException.class) > -1);
            }

            assertTrue(engine.getActionMeta("SetActuator").isCallable());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsMetadataTypeLoop() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_types_loop.py").build();

        try {
            engine.startup();
            fail("Execption expected");
        } catch (SpongeException e) {
            assertEquals(e.getMessage(), "A loop in the type specification has been found in the argument 'listArg' in kb.TypeLoopAction");
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsMetadataRegisteredType() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata_registered_type.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("GetBookAuthorSurname");
            assertEquals(1, actionMeta.getArgs().size());

            RecordType bookType = (RecordType) actionMeta.getArgs().get(0);

            assertEquals(DataTypeKind.RECORD, bookType.getKind());
            assertEquals("Book", bookType.getRegisteredType());
            assertEquals("book", bookType.getName());
            assertEquals(2, bookType.getFields().size());
            assertEquals(DataTypeKind.RECORD, bookType.getFields().get(0).getKind());
            assertEquals(DataTypeKind.STRING, bookType.getFields().get(1).getKind());

            RecordType authorType = (RecordType) bookType.getFields().get(0);
            assertEquals("Author", authorType.getRegisteredType());
            assertEquals("author", authorType.getName());
            assertEquals(2, authorType.getFields().size());

            StringType firstNameType = (StringType) authorType.getFields().get(0);
            assertEquals("firstName", firstNameType.getName());
            assertEquals("First name", firstNameType.getLabel());

            StringType surnameType = (StringType) authorType.getFields().get(1);
            assertEquals("surname", surnameType.getName());
            assertEquals("Surname", surnameType.getLabel());

            RecordType registeredBookType = engine.getType("Book");
            assertEquals("Book", bookType.getRegisteredType());
            assertNull(registeredBookType.getName());
            assertEquals(2, registeredBookType.getFields().size());
            assertEquals(DataTypeKind.RECORD, registeredBookType.getFields().get(0).getKind());
            assertEquals(DataTypeKind.STRING, registeredBookType.getFields().get(1).getKind());

            RecordType registeredAuthorType = engine.getType("Author");
            assertEquals("Author", authorType.getRegisteredType());
            assertNull(registeredAuthorType.getName());
            assertEquals(2, registeredAuthorType.getFields().size());

            // Test different instances.
            assertTrue(registeredBookType != bookType);
            assertTrue(registeredAuthorType != authorType);

            String authorSurname = engine.getOperations().call(String.class, actionMeta.getName(), Arrays.asList(SpongeUtils
                    .immutableMapOf("author", SpongeUtils.immutableMapOf("firstName", "James", "surname", "Joyce"), "title", "Ulysses")));
            assertEquals("Joyce", authorSurname);

            assertEquals(2, engine.getTypes().size());
            assertEquals(DataTypeKind.RECORD, engine.getTypes().get("Book").getKind());
            assertEquals(DataTypeKind.RECORD, engine.getTypes().get("Author").getKind());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsProvideArgsSubmit() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args_submit.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("SetActuator");
            List<DataType> argTypes = actionMeta.getArgs();
            Map<String, ProvidedValue<?>> providedArgs;

            assertNotNull(argTypes.get(0).getProvided());
            assertTrue(argTypes.get(0).getProvided().isValue());
            assertTrue(argTypes.get(0).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(0).getProvided().getDependencies().size());
            assertFalse(argTypes.get(0).getProvided().isReadOnly());
            assertNotNull(argTypes.get(0).getProvided().getSubmittable());
            assertNotNull(argTypes.get(1).getProvided());
            assertTrue(argTypes.get(1).getProvided().isValue());
            assertFalse(argTypes.get(1).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(1).getProvided().getDependencies().size());
            assertFalse(argTypes.get(1).getProvided().isReadOnly());

            providedArgs = engine.getOperations().provideActionArgs(actionMeta.getName(),
                    new ProvideArgsParameters().withProvide(Arrays.asList("actuator1", "actuator2")));
            assertEquals(2, providedArgs.size());

            assertNotNull(providedArgs.get("actuator1"));
            assertEquals("A", providedArgs.get("actuator1").getValue());
            assertEquals(Arrays.asList("A", "B", "C"),
                    SpongeApiUtils.unwrapAnnotatedValueList(providedArgs.get("actuator1").getAnnotatedValueSet()));
            assertTrue(providedArgs.get("actuator1").isValuePresent());

            assertNotNull(providedArgs.get("actuator2"));
            assertEquals(false, providedArgs.get("actuator2").getValue());
            assertNull(providedArgs.get("actuator2").getAnnotatedValueSet());
            assertTrue(providedArgs.get("actuator2").isValuePresent());

            engine.getOperations().provideActionArgs(actionMeta.getName(), new ProvideArgsParameters()
                    .withSubmit(Arrays.asList("actuator1")).withCurrent(SpongeUtils.immutableMapOf("actuator1", "B")));
            assertEquals("B",
                    engine.getOperations()
                            .provideActionArgs(actionMeta.getName(), new ProvideArgsParameters().withProvide(Arrays.asList("actuator1")))
                            .get("actuator1").getValue());

            engine.getOperations().call(actionMeta.getName(), Arrays.asList("C", true));
            assertEquals("C",
                    engine.getOperations()
                            .provideActionArgs(actionMeta.getName(), new ProvideArgsParameters().withProvide(Arrays.asList("actuator1")))
                            .get("actuator1").getValue());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsProvideArgsPagingMeta() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args_paging.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("ViewFruits");
            List<DataType> argTypes = actionMeta.getArgs();

            assertNotNull(argTypes.get(0).getProvided());
            assertTrue(argTypes.get(0).getProvided().isValue());
            assertFalse(argTypes.get(0).getProvided().hasValueSet());
            assertTrue((Boolean) argTypes.get(0).getFeatures().get(Features.PROVIDE_VALUE_PAGEABLE));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testActionsProvideArgsPagingValue() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_provide_args_paging.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("ViewFruits");

            int valueLimit = 5;
            int fruitsSize = engine.getOperations().getVariable(Collection.class, "fruits").size();

            ProvidedValue<?> providedFruits = engine.getOperations().provideActionArgs(actionMeta.getName(),
                    new ProvideArgsParameters().withProvide(Arrays.asList("fruits")).withFeatures(SpongeUtils.immutableMapOf("fruits",
                            SpongeUtils.immutableMapOf(Features.PROVIDE_VALUE_OFFSET, 0, Features.PROVIDE_VALUE_LIMIT, valueLimit))))
                    .get("fruits");

            AnnotatedValue<List<String>> fruits = (AnnotatedValue<List<String>>) providedFruits.getValue();
            assertEquals(valueLimit, fruits.getValue().size());
            assertEquals(Arrays.asList("apple", "orange", "lemon", "banana", "cherry"), fruits.getValue());
            assertEquals(0, fruits.getFeatures().get(Features.PROVIDE_VALUE_OFFSET));
            assertEquals(valueLimit, fruits.getFeatures().get(Features.PROVIDE_VALUE_LIMIT));
            assertEquals(fruitsSize, fruits.getFeatures().get(Features.PROVIDE_VALUE_COUNT));

            providedFruits =
                    engine.getOperations()
                            .provideActionArgs(actionMeta.getName(),
                                    new ProvideArgsParameters().withProvide(Arrays.asList("fruits"))
                                            .withFeatures(SpongeUtils.immutableMapOf("fruits", SpongeUtils.immutableMapOf(
                                                    Features.PROVIDE_VALUE_OFFSET, valueLimit, Features.PROVIDE_VALUE_LIMIT, valueLimit))))
                            .get("fruits");

            fruits = (AnnotatedValue<List<String>>) providedFruits.getValue();
            assertEquals(valueLimit, fruits.getValue().size());
            assertEquals(Arrays.asList("grapes", "peach", "mango", "grapefruit", "kiwi"), fruits.getValue());
            assertEquals(valueLimit, fruits.getFeatures().get(Features.PROVIDE_VALUE_OFFSET));
            assertEquals(valueLimit, fruits.getFeatures().get(Features.PROVIDE_VALUE_LIMIT));
            assertEquals(fruitsSize, fruits.getFeatures().get(Features.PROVIDE_VALUE_COUNT));

            providedFruits = engine.getOperations()
                    .provideActionArgs(actionMeta.getName(),
                            new ProvideArgsParameters().withProvide(Arrays.asList("fruits")).withFeatures(
                                    SpongeUtils.immutableMapOf("fruits", SpongeUtils.immutableMapOf(Features.PROVIDE_VALUE_OFFSET,
                                            2 * valueLimit, Features.PROVIDE_VALUE_LIMIT, valueLimit))))
                    .get("fruits");

            fruits = (AnnotatedValue<List<String>>) providedFruits.getValue();
            assertEquals(1, fruits.getValue().size());
            assertEquals(Arrays.asList("plum"), fruits.getValue());
            assertEquals(2 * valueLimit, fruits.getFeatures().get(Features.PROVIDE_VALUE_OFFSET));
            assertEquals(valueLimit, fruits.getFeatures().get(Features.PROVIDE_VALUE_LIMIT));
            assertEquals(fruitsSize, fruits.getFeatures().get(Features.PROVIDE_VALUE_COUNT));

            // Without paging.
            Assertions
                    .assertThrows(SpongeException.class,
                            () -> engine.getOperations()
                                    .provideActionArgs(actionMeta.getName(),
                                            new ProvideArgsParameters().withProvide(Arrays.asList("fruits")))
                                    .get("fruits"),
                            "The are are no features for argument fruits in kb.ViewFruits");

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
