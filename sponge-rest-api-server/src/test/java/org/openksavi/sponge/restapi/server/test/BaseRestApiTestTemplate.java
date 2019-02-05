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

package org.openksavi.sponge.restapi.server.test;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import org.openksavi.sponge.ProcessorQualifiedVersion;
import org.openksavi.sponge.action.ArgProvidedValue;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.ErrorResponseException;
import org.openksavi.sponge.restapi.client.IncorrectKnowledgeBaseVersionException;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.model.RestActionArgMeta;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;
import org.openksavi.sponge.restapi.model.response.GetVersionResponse;
import org.openksavi.sponge.type.BooleanType;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.value.AnnotatedValue;
import org.openksavi.sponge.type.value.DynamicValue;

public abstract class BaseRestApiTestTemplate {

    @Inject
    protected SpongeEngine engine;

    @Inject
    @Named(PortTestConfig.PORT_BEAN_NAME)
    protected Integer port;

    protected abstract SpongeRestClient createRestClient();

    @Test
    public void testVersion() {
        try (SpongeRestClient client = createRestClient()) {
            assertEquals(engine.getVersion(), client.getVersion());
        }
    }

    @Test
    public void testVersionWithId() {
        try (SpongeRestClient client = createRestClient()) {
            client.getConfiguration().setUseRequestId(true);

            GetVersionRequest request = new GetVersionRequest();
            GetVersionResponse response = client.getVersion(request);

            assertEquals(null, response.getErrorCode());
            assertEquals(null, response.getErrorMessage());
            assertEquals(null, response.getDetailedErrorMessage());
            assertEquals(engine.getVersion(), response.getVersion());
            assertEquals("1", response.getId());
            assertEquals(response.getId(), request.getId());
        }
    }

    @Test
    public void testActions() {
        try (SpongeRestClient client = createRestClient()) {
            assertEquals(RestApiTestConstants.ANONYMOUS_ACTIONS_COUNT, client.getActions().size());
        }
    }

    @Test
    public void testActionsParamArgMetadataRequiredTrue() {
        try (SpongeRestClient client = createRestClient()) {
            assertEquals(RestApiTestConstants.ANONYMOUS_ACTIONS_WITH_METADATA_COUNT, client.getActions(null, true).size());
        }
    }

    @Test
    public void testActionsParamArgMetadataRequiredFalse() {
        try (SpongeRestClient client = createRestClient()) {
            List<RestActionMeta> actions = client.getActions(null, false);

            assertEquals(RestApiTestConstants.ANONYMOUS_ACTIONS_COUNT, actions.size());
            RestActionMeta meta = actions.stream().filter(action -> action.getName().equals("UpperCase")).findFirst().get();
            assertEquals(DataTypeKind.STRING, meta.getArgsMeta().get(0).getType().getKind());
            assertTrue(meta.getArgsMeta().get(0).getType() instanceof StringType);
        }
    }

    @Test
    public void testActionsNameRegExp() {
        try (SpongeRestClient client = createRestClient()) {
            String nameRegExp = ".*Case";
            List<RestActionMeta> actions = client.getActions(nameRegExp);

            assertEquals(2, actions.size());
            assertTrue(actions.stream().allMatch(action -> action.getName().matches(nameRegExp)));
        }
    }

    @Test
    public void testActionsNameExact() {
        try (SpongeRestClient client = createRestClient()) {
            String name = "UpperCase";
            List<RestActionMeta> actions = client.getActions(name);

            assertEquals(1, actions.size());
            assertEquals(actions.get(0).getName(), name);
        }
    }

    @Test
    public void testGetActionMeta() {
        try (SpongeRestClient client = createRestClient()) {
            String actionName = "UpperCase";
            RestActionMeta actionMeta = client.getActionMeta(actionName);

            assertEquals(actionName, actionMeta.getName());
            assertEquals("category1", actionMeta.getCategory().getName());
            assertEquals("Category 1", actionMeta.getCategory().getLabel());
            assertEquals("Category 1 description", actionMeta.getCategory().getDescription());
            assertEquals(1, actionMeta.getArgsMeta().size());
            assertTrue(actionMeta.getArgsMeta().get(0).getType() instanceof StringType);
            assertTrue(actionMeta.getResultMeta().getType() instanceof StringType);
        }
    }

    @Test
    public void testCall() {
        try (SpongeRestClient client = createRestClient()) {
            String arg1 = "test1";

            Object result = client.call("UpperCase", Arrays.asList(arg1));

            assertTrue(result instanceof String);
            assertEquals(arg1.toUpperCase(), result);

            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "actionCalled").get());
            assertFalse(engine.isError());
        }
    }

    @Test(expected = IncorrectKnowledgeBaseVersionException.class)
    public void testCallWithWrongExpectedKnowledgeBaseVersion() {
        try (SpongeRestClient client = createRestClient()) {
            String arg1 = "test1";

            RestActionMeta actionMeta = client.getActionMeta("UpperCase");
            actionMeta.setQualifiedVersion(new ProcessorQualifiedVersion(1, 1));

            try {
                client.call("UpperCase", Arrays.asList(arg1));
                fail("Exception expected");
            } catch (IncorrectKnowledgeBaseVersionException e) {
                assertEquals("The expected action qualified version (1.1) differs from the actual (2.2)", e.getMessage());
                throw e;
            } finally {
                engine.clearError();
            }
        }
    }

    @Test
    public void testCallBinaryArgAndResult() throws IOException {
        try (SpongeRestClient client = createRestClient()) {
            byte[] image = IOUtils.toByteArray(getClass().getResourceAsStream("/image.png"));
            byte[] resultImage = client.call(byte[].class, "EchoImage", Arrays.asList(image));
            assertEquals(image.length, resultImage.length);
            assertArrayEquals(image, resultImage);
        }
    }

    @Test
    public void testCallLanguageError() {
        try (SpongeRestClient client = createRestClient()) {
            try {
                client.call("LangErrorAction");
                fail("Exception expected");
            } catch (ErrorResponseException e) {
                assertEquals(RestApiConstants.DEFAULT_ERROR_CODE, e.getErrorCode());
                assertTrue(e.getErrorMessage().startsWith("NameError: global name 'throws_error' is not defined in"));
                assertTrue(e.getDetailedErrorMessage().startsWith(
                        "org.openksavi.sponge.engine.WrappedException: NameError: global name 'throws_error' is not defined in"));
            } catch (Throwable e) {
                fail("ResponseErrorSpongeException expected");
            } finally {
                engine.clearError();
            }
        }
    }

    @Test
    public void testCallKnowledgeBaseError() {
        try (SpongeRestClient client = createRestClient()) {
            try {
                client.call("KnowledgeBaseErrorAction");
                fail("Exception expected");
            } catch (ErrorResponseException e) {
                assertEquals(RestApiConstants.DEFAULT_ERROR_CODE, e.getErrorCode());
                assertTrue(e.getErrorMessage().startsWith("Exception: Knowledge base exception in"));
                assertTrue(e.getDetailedErrorMessage()
                        .startsWith("org.openksavi.sponge.engine.WrappedException: Exception: Knowledge base exception in"));
            } catch (Throwable e) {
                fail("ResponseErrorSpongeException expected");
            } finally {
                engine.clearError();
            }
        }
    }

    @Test
    public void testCallContentCharset() {
        try (SpongeRestClient client = createRestClient()) {
            String arg1 = "íñäöüèąśęćżźółń";

            Object result = client.call("UpperCase", Arrays.asList(arg1));

            assertTrue(result instanceof String);
            assertEquals(arg1.toUpperCase(), result);

            assertFalse(engine.isError());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCallAnnotatedType() {
        try (SpongeRestClient client = createRestClient()) {
            AnnotatedValue<Boolean> annotatedArg =
                    new AnnotatedValue<>(true).withFeatures(SpongeUtils.immutableMapOf("argFeature1", "argFeature1Value1"));
            AnnotatedValue<String> result = client.call(AnnotatedValue.class, "AnnotatedTypeAction", Arrays.asList(annotatedArg));

            assertEquals("RESULT", result.getValue());
            assertEquals(2, result.getFeatures().size());
            assertEquals("value1", result.getFeatures().get("feature1"));
            assertEquals("argFeature1Value1", result.getFeatures().get("argFeature1"));

            assertFalse(engine.isError());
        }
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void testCallDynamicType() {
        try (SpongeRestClient client = createRestClient()) {
            RestActionMeta actionMeta = client.getActionMeta("DynamicResultAction");
            DataType resultType = actionMeta.getResultMeta().getType();
            assertEquals(DataTypeKind.DYNAMIC, resultType.getKind());

            DynamicValue resultForString = client.call(DynamicValue.class, actionMeta.getName(), Arrays.asList("string"));
            assertEquals("text", resultForString.getValue());
            assertEquals(DataTypeKind.STRING, resultForString.getType().getKind());

            DynamicValue resultForBoolean = client.call(DynamicValue.class, actionMeta.getName(), Arrays.asList("boolean"));
            assertEquals(true, resultForBoolean.getValue());
            assertEquals(DataTypeKind.BOOLEAN, resultForBoolean.getType().getKind());

            assertFalse(engine.isError());
        }
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void testCallTypeType() {
        try (SpongeRestClient client = createRestClient()) {
            RestActionMeta actionMeta = client.getActionMeta("TypeResultAction");
            DataType resultType = actionMeta.getResultMeta().getType();
            assertEquals(DataTypeKind.TYPE, resultType.getKind());

            assertTrue(client.call(DataType.class, actionMeta.getName(), Arrays.asList("string")) instanceof StringType);
            assertTrue(client.call(DataType.class, actionMeta.getName(), Arrays.asList("boolean")) instanceof BooleanType);

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testProvideActionArgs() {
        try (SpongeRestClient client = createRestClient()) {
            String actionName = "SetActuator";

            List<RestActionArgMeta> argsMeta = client.getActionMeta(actionName).getArgsMeta();

            assertTrue(argsMeta.get(0).getProvided().isValue());
            assertTrue(argsMeta.get(0).getProvided().isValueSet());
            assertEquals(0, argsMeta.get(0).getProvided().getDepends().size());
            assertFalse(argsMeta.get(0).getProvided().isReadOnly());
            assertTrue(argsMeta.get(1).getProvided().isValue());
            assertFalse(argsMeta.get(1).getProvided().isValueSet());
            assertEquals(0, argsMeta.get(1).getProvided().getDepends().size());
            assertFalse(argsMeta.get(1).getProvided().isReadOnly());
            assertTrue(argsMeta.get(2).getProvided().isValue());
            assertFalse(argsMeta.get(2).getProvided().isValueSet());
            assertEquals(0, argsMeta.get(2).getProvided().getDepends().size());
            assertTrue(argsMeta.get(2).getProvided().isReadOnly());
            assertNull(argsMeta.get(3).getProvided());

            // Reset the test state.
            client.call(actionName, Arrays.asList("A", false, null, 1));

            Map<String, ArgProvidedValue<?>> providedArgs = client.provideActionArgs(actionName);
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
            // The value of actuator3 should not be asserted because it is read only in this test. Other tests may change its value.
            assertNull(providedArgs.get("actuator3").getValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNull(providedArgs.get("actuator4"));

            client.call(actionName, Arrays.asList("B", true, null, 10));

            providedArgs = client.provideActionArgs(actionName);
            assertEquals(3, providedArgs.size());
            assertNotNull(providedArgs.get("actuator1"));
            assertEquals("B", providedArgs.get("actuator1").getValue());
            assertEquals(Arrays.asList("A", "B", "C"), providedArgs.get("actuator1").getValueSet());
            assertTrue(providedArgs.get("actuator1").isValuePresent());

            assertNotNull(providedArgs.get("actuator2"));
            assertEquals(true, providedArgs.get("actuator2").getValue());
            assertNull(providedArgs.get("actuator2").getValueSet());
            assertTrue(providedArgs.get("actuator2").isValuePresent());

            assertNotNull(providedArgs.get("actuator3"));
            // The value of actuator3 should not be asserted because it is read only in this test. Other tests may change its value.
            assertNull(providedArgs.get("actuator3").getValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNull(providedArgs.get("actuator4"));

            assertFalse(engine.isError());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testProvideActionArgsDepends() {
        try (SpongeRestClient client = createRestClient()) {
            String actionName = "SetActuatorDepends";

            // Reset the test state.
            client.call(actionName, Arrays.asList("A", false, 1, 1, "X"));

            List<RestActionArgMeta> argsMeta = client.getActionMeta(actionName).getArgsMeta();
            Map<String, ArgProvidedValue<?>> providedArgs;

            assertTrue(argsMeta.get(0).getProvided().isValue());
            assertTrue(argsMeta.get(0).getProvided().isValueSet());
            assertEquals(0, argsMeta.get(0).getProvided().getDepends().size());
            assertTrue(argsMeta.get(1).getProvided().isValue());
            assertFalse(argsMeta.get(1).getProvided().isValueSet());
            assertEquals(0, argsMeta.get(1).getProvided().getDepends().size());
            assertTrue(argsMeta.get(2).getProvided().isValue());
            assertFalse(argsMeta.get(2).getProvided().isValueSet());
            assertEquals(0, argsMeta.get(2).getProvided().getDepends().size());
            assertNull(argsMeta.get(3).getProvided());
            assertTrue(argsMeta.get(4).getProvided().isValue());
            assertTrue(argsMeta.get(4).getProvided().isValueSet());
            assertEquals(1, argsMeta.get(4).getProvided().getDepends().size());
            assertEquals("actuator1", argsMeta.get(4).getProvided().getDepends().get(0));

            providedArgs = client.provideActionArgs(actionName, Arrays.asList("actuator1"), Collections.emptyMap());
            assertEquals(1, providedArgs.size());
            assertNotNull(providedArgs.get("actuator1"));
            Object actuator1value = providedArgs.get("actuator1").getValue();
            assertEquals("A", actuator1value);
            assertEquals(Arrays.asList("A", "B", "C"), providedArgs.get("actuator1").getValueSet());
            List<AnnotatedValue<?>> actuator1AnnotatedValueSet = ((ArgProvidedValue) providedArgs.get("actuator1")).getAnnotatedValueSet();
            assertEquals(3, actuator1AnnotatedValueSet.size());
            assertEquals("A", actuator1AnnotatedValueSet.get(0).getValue());
            assertEquals("Value A", actuator1AnnotatedValueSet.get(0).getLabel());
            assertEquals("B", actuator1AnnotatedValueSet.get(1).getValue());
            assertEquals("Value B", actuator1AnnotatedValueSet.get(1).getLabel());
            assertEquals("C", actuator1AnnotatedValueSet.get(2).getValue());
            assertEquals("Value C", actuator1AnnotatedValueSet.get(2).getLabel());

            assertTrue(providedArgs.get("actuator1").isValuePresent());

            providedArgs = client.provideActionArgs(actionName, Arrays.asList("actuator2", "actuator3", "actuator5"),
                    SpongeUtils.immutableMapOf("actuator1", actuator1value));
            assertEquals(3, providedArgs.size());

            assertNotNull(providedArgs.get("actuator2"));
            assertEquals(false, providedArgs.get("actuator2").getValue());
            assertNull(providedArgs.get("actuator2").getValueSet());
            assertTrue(providedArgs.get("actuator2").isValuePresent());

            assertNotNull(providedArgs.get("actuator3"));
            assertEquals(1, providedArgs.get("actuator3").getValue());
            assertNull(providedArgs.get("actuator3").getValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNull(providedArgs.get("actuator4"));

            assertNotNull(providedArgs.get("actuator5"));
            assertEquals("X", providedArgs.get("actuator5").getValue());
            assertEquals(Arrays.asList("X", "Y", "Z", "A"), providedArgs.get("actuator5").getValueSet());
            assertTrue(providedArgs.get("actuator5").isValuePresent());

            client.call(actionName, Arrays.asList("B", true, 5, 10, "Y"));

            providedArgs = client.provideActionArgs(actionName, Arrays.asList("actuator1"), Collections.emptyMap());
            assertEquals(1, providedArgs.size());
            assertNotNull(providedArgs.get("actuator1"));
            actuator1value = providedArgs.get("actuator1").getValue();
            assertEquals("B", actuator1value);
            assertEquals(Arrays.asList("A", "B", "C"), providedArgs.get("actuator1").getValueSet());
            assertTrue(providedArgs.get("actuator1").isValuePresent());

            providedArgs = client.provideActionArgs(actionName, Arrays.asList("actuator2", "actuator3", "actuator5"),
                    SpongeUtils.immutableMapOf("actuator1", actuator1value));

            assertEquals(3, providedArgs.size());

            assertNotNull(providedArgs.get("actuator2"));
            assertEquals(true, providedArgs.get("actuator2").getValue());
            assertNull(providedArgs.get("actuator2").getValueSet());
            assertTrue(providedArgs.get("actuator2").isValuePresent());

            assertNotNull(providedArgs.get("actuator3"));
            assertEquals(5, providedArgs.get("actuator3").getValue());
            assertNull(providedArgs.get("actuator3").getValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNull(providedArgs.get("actuator4"));

            assertNotNull(providedArgs.get("actuator5"));
            assertEquals("Y", providedArgs.get("actuator5").getValue());
            assertEquals(Arrays.asList("X", "Y", "Z", "B"), providedArgs.get("actuator5").getValueSet());
            assertTrue(providedArgs.get("actuator5").isValuePresent());

            assertFalse(engine.isError());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProvideActionArgByAction() {
        try (SpongeRestClient client = createRestClient()) {
            RestActionMeta actionMeta = client.getActionMeta("ProvideByAction");
            List<String> values = (List<String>) client.provideActionArgs(actionMeta.getName()).get("value").getValueSet();
            assertEquals("value3", client.call(actionMeta.getName(), Arrays.asList(values.get(values.size() - 1))));
        }
    }

    @Test
    public void testSend() {
        try (SpongeRestClient client = createRestClient()) {
            assertNotNull(client.send("alarm", SpongeUtils.immutableMapOf("attr1", "Test")));

            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "eventSent").get());
            assertFalse(engine.isError());
        }
    }

    @Test
    public void testKnowledgeBases() {
        try (SpongeRestClient client = createRestClient()) {
            assertEquals(1, client.getKnowledgeBases().size());
        }
    }
}
