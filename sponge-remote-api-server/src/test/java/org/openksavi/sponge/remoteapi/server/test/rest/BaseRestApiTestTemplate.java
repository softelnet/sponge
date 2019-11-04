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

package org.openksavi.sponge.remoteapi.server.test.rest;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import org.openksavi.sponge.ProcessorQualifiedVersion;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.features.Features;
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.remoteapi.server.test.RemoteApiTestUtils;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.ErrorResponseException;
import org.openksavi.sponge.restapi.client.InvalidKnowledgeBaseVersionException;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.request.GetActionsRequest;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;
import org.openksavi.sponge.restapi.model.response.GetVersionResponse;
import org.openksavi.sponge.restapi.util.RestApiUtils;
import org.openksavi.sponge.type.BinaryType;
import org.openksavi.sponge.type.BooleanType;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.DateTimeKind;
import org.openksavi.sponge.type.DateTimeType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.QualifiedDataType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.type.value.AnnotatedValue;
import org.openksavi.sponge.type.value.DynamicValue;
import org.openksavi.sponge.util.SpongeApiUtils;

@SuppressWarnings("rawtypes")
public abstract class BaseRestApiTestTemplate {

    @Inject
    protected SpongeEngine engine;

    @Inject
    @Named(PortTestConfig.PORT_BEAN_NAME)
    protected Integer port;

    protected abstract SpongeRestClient createRestClient();

    protected SpongeRestClient createGuestRestClient() {
        SpongeRestClient client = createRestClient();
        client.getConfiguration().setUsername("joe");
        client.getConfiguration().setPassword("password");

        return client;
    }

    @Test
    public void testVersion() {
        try (SpongeRestClient client = createRestClient()) {
            assertEquals(engine.getVersion(), client.getVersion());
        }
    }

    @Test
    public void testResponseTimes() {
        try (SpongeRestClient client = createRestClient()) {
            GetVersionResponse response = client.getVersion(new GetVersionRequest());
            assertNotNull(response.getHeader().getRequestTime());
            assertNotNull(response.getHeader().getResponseTime());
            assertFalse(response.getHeader().getResponseTime().isBefore(response.getHeader().getRequestTime()));
        }
    }

    @Test
    public void testFeatures() {
        try (SpongeRestClient client = createRestClient()) {
            Map<String, Object> features = client.getFeatures();
            assertEquals(1, features.size());
            assertTrue((Boolean) features.get(RestApiConstants.REMOTE_API_FEATURE_GRPC_ENABLED));
        }
    }

    @Test
    public void testVersionWithId() {
        try (SpongeRestClient client = createRestClient()) {
            client.getConfiguration().setUseRequestId(true);

            GetVersionRequest request = new GetVersionRequest();
            GetVersionResponse response = client.getVersion(request);

            assertEquals(null, response.getHeader().getErrorCode());
            assertEquals(null, response.getHeader().getErrorMessage());
            assertEquals(null, response.getHeader().getDetailedErrorMessage());
            assertEquals(engine.getVersion(), response.getVersion());
            assertEquals("1", response.getHeader().getId());
            assertEquals(response.getHeader().getId(), request.getHeader().getId());
        }
    }

    @Test
    public void testActions() {
        try (SpongeRestClient client = createRestClient()) {
            assertEquals(RestApiTestConstants.ANONYMOUS_ACTIONS_COUNT, client.getActions().size());
        }
    }

    @Test
    public void testActionsArgRequiredTrue() {
        try (SpongeRestClient client = createRestClient()) {
            assertEquals(RestApiTestConstants.ANONYMOUS_ACTIONS_WITH_METADATA_COUNT, client.getActions(null, true).size());
        }
    }

    @Test
    public void testActionsArgRequiredFalse() {
        try (SpongeRestClient client = createRestClient()) {
            List<RestActionMeta> actions = client.getActions(null, false);

            assertEquals(RestApiTestConstants.ANONYMOUS_ACTIONS_COUNT, actions.size());
            RestActionMeta meta = actions.stream().filter(action -> action.getName().equals("UpperCase")).findFirst().get();
            assertEquals(DataTypeKind.STRING, meta.getArgs().get(0).getKind());
            assertTrue(meta.getArgs().get(0) instanceof StringType);
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
            assertEquals(1, actionMeta.getArgs().size());
            assertTrue(actionMeta.getArgs().get(0) instanceof StringType);
            assertTrue(actionMeta.getResult() instanceof StringType);
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

    @Test
    public void testCallWithWrongExpectedKnowledgeBaseVersion() {
        try (SpongeRestClient client = createRestClient()) {
            String arg1 = "test1";

            RestActionMeta actionMeta = client.getActionMeta("UpperCase");
            actionMeta.setQualifiedVersion(new ProcessorQualifiedVersion(1, 1));

            assertThrows(InvalidKnowledgeBaseVersionException.class, () -> client.call("UpperCase", Arrays.asList(arg1)),
                    "The expected action qualified version (1.1) differs from the actual (2.2)");
        } finally {
            engine.clearError();
        }
    }

    @Test
    public void testCallBinaryArgAndResult() throws IOException {
        try (SpongeRestClient client = createRestClient()) {
            RestActionMeta actionMeta = client.getActionMeta("EchoImage");
            assertEquals(1, actionMeta.getArgs().size());
            assertEquals("image/png", ((BinaryType) actionMeta.getArgs().get(0)).getMimeType());

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
                assertEquals(RestApiConstants.ERROR_CODE_GENERIC, e.getErrorCode());
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
                assertEquals(RestApiConstants.ERROR_CODE_GENERIC, e.getErrorCode());
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

            assertEquals("Result value", result.getValueLabel());
            assertEquals("Result value description", result.getValueDescription());
            assertEquals("Result type", result.getTypeLabel());
            assertEquals("Result type description", result.getTypeDescription());

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testCallDynamicType() {
        try (SpongeRestClient client = createRestClient()) {
            RestActionMeta actionMeta = client.getActionMeta("DynamicResultAction");
            DataType resultType = actionMeta.getResult();
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

    @Test
    public void testCallTypeType() {
        try (SpongeRestClient client = createRestClient()) {
            RestActionMeta actionMeta = client.getActionMeta("TypeResultAction");
            DataType resultType = actionMeta.getResult();
            assertEquals(DataTypeKind.TYPE, resultType.getKind());

            assertTrue(client.call(DataType.class, actionMeta.getName(), Arrays.asList("string")) instanceof StringType);
            assertTrue(client.call(DataType.class, actionMeta.getName(), Arrays.asList("boolean")) instanceof BooleanType);

            assertFalse(engine.isError());
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testCallDateTimeType() {
        try (SpongeRestClient client = createRestClient()) {
            RestActionMeta actionMeta = client.getActionMeta("DateTimeAction");
            assertEquals(DateTimeKind.DATE_TIME, ((DateTimeType) actionMeta.getArgs().get(0)).getDateTimeKind());
            assertEquals(DateTimeKind.DATE_TIME_ZONE, ((DateTimeType) actionMeta.getArgs().get(1)).getDateTimeKind());
            assertEquals(DateTimeKind.DATE, ((DateTimeType) actionMeta.getArgs().get(2)).getDateTimeKind());
            assertEquals(DateTimeKind.TIME, ((DateTimeType) actionMeta.getArgs().get(3)).getDateTimeKind());
            assertEquals(DateTimeKind.INSTANT, ((DateTimeType) actionMeta.getArgs().get(4)).getDateTimeKind());

            LocalDateTime dateTime = LocalDateTime.now();
            ZonedDateTime dateTimeZone = ZonedDateTime.now(ZoneId.of("America/Detroit"));
            LocalDate date = LocalDate.parse("2019-02-06");
            LocalTime time = LocalTime.parse("15:15:00", DateTimeFormatter.ofPattern(actionMeta.getArgs().get(3).getFormat()));
            Instant instant = Instant.now();

            List<DynamicValue> dates =
                    client.call(List.class, actionMeta.getName(), Arrays.asList(dateTime, dateTimeZone, date, time, instant));
            assertTrue(dates.get(0).getValue() instanceof LocalDateTime);
            assertEquals(dateTime, dates.get(0).getValue());
            assertTrue(dates.get(1).getValue() instanceof ZonedDateTime);
            assertEquals(dateTimeZone.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    ((ZonedDateTime) dates.get(1).getValue()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            assertTrue(dates.get(2).getValue() instanceof LocalDate);
            assertEquals(date, dates.get(2).getValue());
            assertTrue(dates.get(3).getValue() instanceof LocalTime);
            assertEquals(time, dates.get(3).getValue());
            assertTrue(dates.get(4).getValue() instanceof Instant);
            assertEquals(instant, dates.get(4).getValue());

            assertFalse(engine.isError());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCallRecordType() {
        try (SpongeRestClient client = createRestClient()) {
            RestActionMeta actionMeta = client.getActionMeta("RecordAsResultAction");
            RemoteApiTestUtils.assertBookRecordType((RecordType) actionMeta.getResult());

            Map<String, Object> book1 = client.call(Map.class, actionMeta.getName(), Arrays.asList(1));
            assertEquals(4, book1.size());
            assertEquals(1, book1.get("id"));
            assertEquals("James Joyce", book1.get("author"));
            assertEquals("Ulysses", book1.get("title"));
            assertTrue(book1.containsKey("comment"));
            assertNull(book1.get("comment"));

            actionMeta = client.getActionMeta("RecordAsArgAction");
            RemoteApiTestUtils.assertBookRecordType((RecordType) actionMeta.getArgs().get(0));

            Map<String, Object> book2 = SpongeUtils.immutableMapOf("id", 5, "author", "Arthur Conan Doyle", "title",
                    "Adventures of Sherlock Holmes", "comment", null);
            Map<String, Object> book3 = client.call(Map.class, "RecordAsArgAction", Arrays.asList(book2));
            assertEquals(4, book3.size());
            book2.forEach((key, value) -> assertEquals(value, book3.get(key)));
            assertTrue(book3.containsKey("comment"));

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testRegisteredTypeArgAction() {
        try (SpongeRestClient client = createRestClient()) {
            GetActionsRequest request = new GetActionsRequest();
            request.setName("RegisteredTypeArgAction");
            request.setRegisteredTypes(true);

            Map<String, DataType<?>> types = client.getActions(request).getTypes();
            assertEquals(1, types.size());
            RemoteApiTestUtils.assertPersonRecordType((RecordType) types.get("Person"));

            String surname = client.call(String.class, "RegisteredTypeArgAction",
                    Arrays.asList(SpongeUtils.immutableMapOf("firstName", "James", "surname", "Joyce")));
            assertEquals("Joyce", surname);

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testInheritedRegisteredTypeArgAction() {
        try (SpongeRestClient client = createRestClient()) {
            GetActionsRequest request = new GetActionsRequest();
            request.setName("InheritedRegisteredTypeArgAction");
            request.setRegisteredTypes(true);

            Map<String, DataType<?>> types = client.getActions(request).getTypes();
            assertEquals(2, types.size());
            RemoteApiTestUtils.assertPersonRecordType((RecordType) types.get("Person"));
            RemoteApiTestUtils.assertCitizenRecordType((RecordType) types.get("Citizen"));

            String sentence = client.call(String.class, "InheritedRegisteredTypeArgAction",
                    Arrays.asList(SpongeUtils.immutableMapOf("firstName", "John", "surname", "Brown", "country", "UK")));
            assertEquals("John comes from UK", sentence);

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testNestedRecordAsArgAction() {
        try (SpongeRestClient client = createRestClient()) {
            RestActionMeta actionMeta = client.getActionMeta("NestedRecordAsArgAction");
            assertEquals(1, actionMeta.getArgs().size());
            RecordType argType = (RecordType) actionMeta.getArgs().get(0);
            assertEquals(DataTypeKind.RECORD, argType.getKind());
            assertEquals("book", argType.getName());
            assertEquals("Book", argType.getLabel());
            assertEquals(3, argType.getFields().size());

            assertEquals(DataTypeKind.INTEGER, argType.getFields().get(0).getKind());
            assertEquals("id", argType.getFields().get(0).getName());
            assertEquals("Identifier", argType.getFields().get(0).getLabel());

            RecordType authorType = (RecordType) argType.getFields().get(1);
            assertEquals("author", authorType.getName());
            assertEquals("Author", authorType.getLabel());
            assertEquals(3, authorType.getFields().size());

            assertEquals(DataTypeKind.INTEGER, authorType.getFields().get(0).getKind());
            assertEquals("id", authorType.getFields().get(0).getName());
            assertEquals("Identifier", authorType.getFields().get(0).getLabel());

            assertEquals(DataTypeKind.STRING, authorType.getFields().get(1).getKind());
            assertEquals("firstName", authorType.getFields().get(1).getName());
            assertEquals("First name", authorType.getFields().get(1).getLabel());

            assertEquals(DataTypeKind.STRING, authorType.getFields().get(2).getKind());
            assertEquals("surname", authorType.getFields().get(2).getName());
            assertEquals("Surname", authorType.getFields().get(2).getLabel());

            assertEquals(DataTypeKind.STRING, argType.getFields().get(2).getKind());
            assertEquals("title", argType.getFields().get(2).getName());
            assertEquals("Title", argType.getFields().get(2).getLabel());

            String bookSummary = client.call(String.class, actionMeta.getName(), Arrays.asList(SpongeUtils.immutableMapOf("author",
                    SpongeUtils.immutableMapOf("firstName", "James", "surname", "Joyce"), "title", "Ulysses")));

            assertEquals("James Joyce - Ulysses", bookSummary);

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testProvideActionArgs() {
        try (SpongeRestClient client = createRestClient()) {
            String actionName = "SetActuator";

            List<DataType> argTypes = client.getActionMeta(actionName).getArgs();

            assertTrue(argTypes.get(0).getProvided().isValue());
            assertTrue(argTypes.get(0).getProvided().hasValueSet());
            assertTrue(argTypes.get(0).getProvided().getValueSet().isLimited());
            assertEquals(0, argTypes.get(0).getProvided().getDependencies().size());
            assertFalse(argTypes.get(0).getProvided().isReadOnly());
            assertTrue(argTypes.get(1).getProvided().isValue());
            assertFalse(argTypes.get(1).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(1).getProvided().getDependencies().size());
            assertFalse(argTypes.get(1).getProvided().isReadOnly());
            assertTrue(argTypes.get(2).getProvided().isValue());
            assertFalse(argTypes.get(2).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(2).getProvided().getDependencies().size());
            assertTrue(argTypes.get(2).getProvided().isReadOnly());
            assertNull(argTypes.get(3).getProvided());

            // Reset the test state.
            client.call(actionName, Arrays.asList("A", false, null, 1));

            Map<String, ProvidedValue<?>> providedArgs =
                    client.provideActionArgs(actionName, Arrays.asList("actuator1", "actuator2", "actuator3"));
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
            // The value of actuator3 should not be asserted because it is read only in this test. Other tests may change its value.
            assertNull(providedArgs.get("actuator3").getAnnotatedValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNull(providedArgs.get("actuator4"));

            client.call(actionName, Arrays.asList("B", true, null, 10));

            providedArgs = client.provideActionArgs(actionName, Arrays.asList("actuator1", "actuator2", "actuator3"));
            assertEquals(3, providedArgs.size());
            assertNotNull(providedArgs.get("actuator1"));
            assertEquals("B", providedArgs.get("actuator1").getValue());
            assertEquals(Arrays.asList("A", "B", "C"),
                    SpongeApiUtils.unwrapAnnotatedValueList(providedArgs.get("actuator1").getAnnotatedValueSet()));
            assertTrue(providedArgs.get("actuator1").isValuePresent());

            assertNotNull(providedArgs.get("actuator2"));
            assertEquals(true, providedArgs.get("actuator2").getValue());
            assertNull(providedArgs.get("actuator2").getAnnotatedValueSet());
            assertTrue(providedArgs.get("actuator2").isValuePresent());

            assertNotNull(providedArgs.get("actuator3"));
            // The value of actuator3 should not be asserted because it is read only in this test. Other tests may change its value.
            assertNull(providedArgs.get("actuator3").getAnnotatedValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNull(providedArgs.get("actuator4"));

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testProvideActionArgsNotLimitedValueSet() {
        try (SpongeRestClient client = createRestClient()) {
            String actionName = "SetActuatorNotLimitedValueSet";

            List<DataType> argTypes = client.getActionMeta(actionName).getArgs();
            assertNotNull(argTypes.get(0).getProvided());
            assertTrue(argTypes.get(0).getProvided().isValue());
            assertTrue(argTypes.get(0).getProvided().hasValueSet());
            assertFalse(argTypes.get(0).getProvided().getValueSet().isLimited());

            assertFalse(engine.isError());
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testProvideActionArgsDepends() {
        try (SpongeRestClient client = createRestClient()) {
            String actionName = "SetActuatorDepends";

            // Reset the test state.
            client.call(actionName, Arrays.asList("A", false, 1, 1, "X"));

            List<DataType> argTypes = client.getActionMeta(actionName).getArgs();
            Map<String, ProvidedValue<?>> providedArgs;

            assertTrue(argTypes.get(0).getProvided().isValue());
            assertTrue(argTypes.get(0).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(0).getProvided().getDependencies().size());
            assertTrue(argTypes.get(1).getProvided().isValue());
            assertFalse(argTypes.get(1).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(1).getProvided().getDependencies().size());
            assertTrue(argTypes.get(2).getProvided().isValue());
            assertFalse(argTypes.get(2).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(2).getProvided().getDependencies().size());
            assertNull(argTypes.get(3).getProvided());
            assertTrue(argTypes.get(4).getProvided().isValue());
            assertTrue(argTypes.get(4).getProvided().hasValueSet());
            assertEquals(1, argTypes.get(4).getProvided().getDependencies().size());
            assertEquals("actuator1", argTypes.get(4).getProvided().getDependencies().get(0));

            providedArgs = client.provideActionArgs(actionName, Arrays.asList("actuator1"), Collections.emptyMap());
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

            providedArgs = client.provideActionArgs(actionName, Arrays.asList("actuator2", "actuator3", "actuator5"),
                    SpongeUtils.immutableMapOf("actuator1", actuator1value));
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

            client.call(actionName, Arrays.asList("B", true, 5, 10, "Y"));

            providedArgs = client.provideActionArgs(actionName, Arrays.asList("actuator1"), Collections.emptyMap());
            assertEquals(1, providedArgs.size());
            assertNotNull(providedArgs.get("actuator1"));
            actuator1value = providedArgs.get("actuator1").getValue();
            assertEquals("B", actuator1value);
            assertEquals(Arrays.asList("A", "B", "C"),
                    SpongeApiUtils.unwrapAnnotatedValueList(providedArgs.get("actuator1").getAnnotatedValueSet()));
            assertTrue(providedArgs.get("actuator1").isValuePresent());

            providedArgs = client.provideActionArgs(actionName, Arrays.asList("actuator2", "actuator3", "actuator5"),
                    SpongeUtils.immutableMapOf("actuator1", actuator1value));

            assertEquals(3, providedArgs.size());

            assertNotNull(providedArgs.get("actuator2"));
            assertEquals(true, providedArgs.get("actuator2").getValue());
            assertNull(providedArgs.get("actuator2").getAnnotatedValueSet());
            assertTrue(providedArgs.get("actuator2").isValuePresent());

            assertNotNull(providedArgs.get("actuator3"));
            assertEquals(5, providedArgs.get("actuator3").getValue());
            assertNull(providedArgs.get("actuator3").getAnnotatedValueSet());
            assertTrue(providedArgs.get("actuator3").isValuePresent());

            assertNull(providedArgs.get("actuator4"));

            assertNotNull(providedArgs.get("actuator5"));
            assertEquals("Y", providedArgs.get("actuator5").getValue());
            assertEquals(Arrays.asList("X", "Y", "Z", "B"),
                    SpongeApiUtils.unwrapAnnotatedValueList(providedArgs.get("actuator5").getAnnotatedValueSet()));
            assertTrue(providedArgs.get("actuator5").isValuePresent());

            assertFalse(engine.isError());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProvideActionArgByAction() {
        try (SpongeRestClient client = createRestClient()) {
            RestActionMeta actionMeta = client.getActionMeta("ProvideByAction");
            List<String> values = (List<String>) SpongeApiUtils.unwrapAnnotatedValueList(
                    client.provideActionArgs(actionMeta.getName(), Arrays.asList("value")).get("value").getAnnotatedValueSet());
            assertEquals("value3", client.call(actionMeta.getName(), Arrays.asList(values.get(values.size() - 1))));
        }
    }

    @Test
    public void testProvideActionArgsElementValueSet() {
        try (SpongeRestClient client = createRestClient()) {
            String actionName = "FruitsElementValueSetAction";

            ListType fruitsType = (ListType) client.getActionMeta(actionName).getArgs().get(0);
            assertTrue(fruitsType.isUnique());
            assertNotNull(fruitsType.getProvided());
            assertFalse(fruitsType.getProvided().isValue());
            assertFalse(fruitsType.getProvided().hasValueSet());
            assertTrue(fruitsType.getProvided().isElementValueSet());

            Map<String, ProvidedValue<?>> provided = client.provideActionArgs(actionName, Arrays.asList("fruits"));
            List<AnnotatedValue> elementValueSet = provided.get("fruits").getAnnotatedElementValueSet();
            assertEquals(3, elementValueSet.size());
            assertEquals("apple", elementValueSet.get(0).getValue());
            assertEquals("Apple", elementValueSet.get(0).getValueLabel());
            assertEquals("banana", elementValueSet.get(1).getValue());
            assertEquals("Banana", elementValueSet.get(1).getValueLabel());
            assertEquals("lemon", elementValueSet.get(2).getValue());
            assertEquals("Lemon", elementValueSet.get(2).getValueLabel());

            assertEquals(2, client.call(actionName, Arrays.asList(Arrays.asList("apple", "lemon"))));

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testProvideActionArgsSubmit() {
        try (SpongeRestClient client = createRestClient()) {
            String actionName = "SetActuatorSubmit";

            // Reset the test state.
            client.call(actionName, Arrays.asList("A", false));

            List<DataType> argTypes = client.getActionMeta(actionName).getArgs();

            assertNotNull(argTypes.get(0).getProvided());
            assertTrue(argTypes.get(0).getProvided().isValue());
            assertTrue(argTypes.get(0).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(0).getProvided().getDependencies().size());
            assertFalse(argTypes.get(0).getProvided().isReadOnly());
            assertTrue(argTypes.get(0).getProvided().isSubmittable());
            assertNotNull(argTypes.get(1).getProvided());
            assertTrue(argTypes.get(1).getProvided().isValue());
            assertFalse(argTypes.get(1).getProvided().hasValueSet());
            assertEquals(0, argTypes.get(1).getProvided().getDependencies().size());
            assertFalse(argTypes.get(1).getProvided().isReadOnly());
            assertFalse(argTypes.get(1).getProvided().isSubmittable());

            Map<String, ProvidedValue<?>> providedArgs;

            providedArgs = client.provideActionArgs(actionName, Arrays.asList("actuator1", "actuator2"));
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

            client.submitActionArgs(actionName, Arrays.asList("actuator1"), SpongeUtils.immutableMapOf("actuator1", "B"));
            assertEquals("B", client.provideActionArgs(actionName, Arrays.asList("actuator1")).get("actuator1").getValue());

            client.call(actionName, Arrays.asList("C", true));
            assertEquals("C", client.provideActionArgs(actionName, Arrays.asList("actuator1")).get("actuator1").getValue());

            // Reset the test state.
            client.call(actionName, Arrays.asList("A", false));

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testProvideActionArgsPagingMeta() {
        try (SpongeRestClient client = createRestClient()) {
            String actionName = "ViewFruitsPaging";

            RestActionMeta actionMeta = client.getActionMeta(actionName);
            List<DataType> argTypes = actionMeta.getArgs();

            assertNotNull(argTypes.get(0).getProvided());
            assertTrue(argTypes.get(0).getProvided().isValue());
            assertFalse(argTypes.get(0).getProvided().hasValueSet());
            assertTrue((Boolean) argTypes.get(0).getFeatures().get(Features.PROVIDE_VALUE_PAGINABLE));
            assertNull((Boolean) argTypes.get(0).getFeatures().get(Features.PROVIDE_VALUE_SET_PAGINABLE));

            assertNotNull(argTypes.get(1).getProvided());
            assertFalse(argTypes.get(1).getProvided().isValue());
            assertTrue(argTypes.get(1).getProvided().hasValueSet());
            assertNull((Boolean) argTypes.get(1).getFeatures().get(Features.PROVIDE_VALUE_PAGINABLE));
            assertTrue((Boolean) argTypes.get(1).getFeatures().get(Features.PROVIDE_VALUE_SET_PAGINABLE));

            assertFalse(engine.isError());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProvideActionArgsPagingValue() {
        try (SpongeRestClient client = createRestClient()) {
            String actionName = "ViewFruitsPaging";

            int valueLimit = 5;

            ProvidedValue<?> providedFruits = client
                    .provideActionArgs(actionName, Arrays.asList("fruits"), null, null,
                            SpongeUtils.immutableMapOf("fruits",
                                    SpongeUtils.immutableMapOf(Features.PROVIDE_VALUE_OFFSET, 0, Features.PROVIDE_VALUE_LIMIT, valueLimit)))
                    .get("fruits");

            List<String> fruits = (List<String>) providedFruits.getValue();
            assertEquals(valueLimit, fruits.size());
            assertEquals(Arrays.asList("apple", "orange", "lemon", "banana", "cherry"), fruits);
            assertEquals(0, providedFruits.getFeatures().get(Features.PROVIDE_VALUE_OFFSET));
            assertEquals(valueLimit, providedFruits.getFeatures().get(Features.PROVIDE_VALUE_LIMIT));

            providedFruits = client
                    .provideActionArgs(actionName, Arrays.asList("fruits"), null, null, SpongeUtils.immutableMapOf("fruits", SpongeUtils
                            .immutableMapOf(Features.PROVIDE_VALUE_OFFSET, valueLimit, Features.PROVIDE_VALUE_LIMIT, valueLimit)))
                    .get("fruits");

            fruits = (List<String>) providedFruits.getValue();
            assertEquals(valueLimit, fruits.size());
            assertEquals(Arrays.asList("grapes", "peach", "mango", "grapefruit", "kiwi"), fruits);
            assertEquals(valueLimit, providedFruits.getFeatures().get(Features.PROVIDE_VALUE_OFFSET));
            assertEquals(valueLimit, providedFruits.getFeatures().get(Features.PROVIDE_VALUE_LIMIT));

            providedFruits = client
                    .provideActionArgs(actionName, Arrays.asList("fruits"), null, null, SpongeUtils.immutableMapOf("fruits", SpongeUtils
                            .immutableMapOf(Features.PROVIDE_VALUE_OFFSET, 2 * valueLimit, Features.PROVIDE_VALUE_LIMIT, valueLimit)))
                    .get("fruits");

            fruits = (List<String>) providedFruits.getValue();
            assertEquals(1, fruits.size());
            assertEquals(Arrays.asList("plum"), fruits);
            assertEquals(2 * valueLimit, providedFruits.getFeatures().get(Features.PROVIDE_VALUE_OFFSET));
            assertEquals(valueLimit, providedFruits.getFeatures().get(Features.PROVIDE_VALUE_LIMIT));

            // All without paging
            providedFruits = client.provideActionArgs(actionName, Arrays.asList("fruits"), null, null).get("fruits");
            fruits = (List<String>) providedFruits.getValue();
            assertEquals(11, fruits.size());
            assertEquals(
                    Arrays.asList("apple", "orange", "lemon", "banana", "cherry", "grapes", "peach", "mango", "grapefruit", "kiwi", "plum"),
                    fruits);
            assertNull(providedFruits.getFeatures().get(Features.PROVIDE_VALUE_OFFSET));
            assertNull(providedFruits.getFeatures().get(Features.PROVIDE_VALUE_LIMIT));

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testActionsProvideArgsPagingValueSet() {
        try (SpongeRestClient client = createRestClient()) {
            String actionName = "ViewFruitsPaging";

            int valueSetLimit = 8;

            ProvidedValue<
                    ?> provided = client
                            .provideActionArgs(actionName, Arrays.asList("favouriteFruit"), null, null,
                                    SpongeUtils.immutableMapOf("favouriteFruit", SpongeUtils.immutableMapOf(
                                            Features.PROVIDE_VALUE_SET_OFFSET, 0, Features.PROVIDE_VALUE_SET_LIMIT, valueSetLimit)))
                            .get("favouriteFruit");
            assertEquals(Arrays.asList("apple", "orange", "lemon", "banana", "cherry", "grapes", "peach", "mango"),
                    SpongeApiUtils.unwrapAnnotatedValueList(provided.getAnnotatedValueSet()));
            assertEquals(0, provided.getFeatures().get(Features.PROVIDE_VALUE_SET_OFFSET));
            assertEquals(valueSetLimit, provided.getFeatures().get(Features.PROVIDE_VALUE_SET_LIMIT));

            provided = client.provideActionArgs(actionName, Arrays.asList("favouriteFruit"), null, null,
                    SpongeUtils.immutableMapOf("favouriteFruit", SpongeUtils.immutableMapOf(Features.PROVIDE_VALUE_SET_OFFSET,
                            valueSetLimit, Features.PROVIDE_VALUE_SET_LIMIT, valueSetLimit)))
                    .get("favouriteFruit");
            assertEquals(Arrays.asList("grapefruit", "kiwi", "plum", "pear", "strawberry", "blackberry", "pineapple", "papaya"),
                    SpongeApiUtils.unwrapAnnotatedValueList(provided.getAnnotatedValueSet()));
            assertEquals(valueSetLimit, provided.getFeatures().get(Features.PROVIDE_VALUE_SET_OFFSET));
            assertEquals(valueSetLimit, provided.getFeatures().get(Features.PROVIDE_VALUE_SET_LIMIT));

            provided = client.provideActionArgs(actionName, Arrays.asList("favouriteFruit"), null, null,
                    SpongeUtils.immutableMapOf("favouriteFruit", SpongeUtils.immutableMapOf(Features.PROVIDE_VALUE_SET_OFFSET,
                            2 * valueSetLimit, Features.PROVIDE_VALUE_SET_LIMIT, valueSetLimit)))
                    .get("favouriteFruit");
            assertEquals(Arrays.asList("melon"), SpongeApiUtils.unwrapAnnotatedValueList(provided.getAnnotatedValueSet()));
            assertEquals(2 * valueSetLimit, provided.getFeatures().get(Features.PROVIDE_VALUE_SET_OFFSET));
            assertEquals(valueSetLimit, provided.getFeatures().get(Features.PROVIDE_VALUE_SET_LIMIT));

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testActionsProvideArgsPagingElementValueSet() {
        try (SpongeRestClient client = createRestClient()) {
            String actionName = "ViewFavouriteFruitsPaging";

            int elementValueSetLimit = 10;

            ProvidedValue<
                    ?> provided = client
                            .provideActionArgs(actionName, Arrays.asList("favouriteFruits"), null, null,
                                    SpongeUtils.immutableMapOf("favouriteFruits",
                                            SpongeUtils.immutableMapOf(Features.PROVIDE_ELEMENT_VALUE_SET_OFFSET, 0,
                                                    Features.PROVIDE_ELEMENT_VALUE_SET_LIMIT, elementValueSetLimit)))
                            .get("favouriteFruits");
            assertEquals(Arrays.asList("apple", "orange", "lemon", "banana", "cherry", "grapes", "peach", "mango", "grapefruit", "kiwi"),
                    SpongeApiUtils.unwrapUnknownAnnotatedValueList(provided.getAnnotatedElementValueSet()));
            assertEquals(0, provided.getFeatures().get(Features.PROVIDE_ELEMENT_VALUE_SET_OFFSET));
            assertEquals(elementValueSetLimit, provided.getFeatures().get(Features.PROVIDE_ELEMENT_VALUE_SET_LIMIT));

            provided = client
                    .provideActionArgs(actionName, Arrays.asList("favouriteFruits"), null, null,
                            SpongeUtils
                                    .immutableMapOf("favouriteFruits",
                                            SpongeUtils.immutableMapOf(Features.PROVIDE_ELEMENT_VALUE_SET_OFFSET, elementValueSetLimit,
                                                    Features.PROVIDE_ELEMENT_VALUE_SET_LIMIT, elementValueSetLimit)))
                    .get("favouriteFruits");
            assertEquals(Arrays.asList("plum", "pear", "strawberry", "blackberry", "pineapple", "papaya", "melon"),
                    SpongeApiUtils.unwrapUnknownAnnotatedValueList(provided.getAnnotatedElementValueSet()));
            assertEquals(elementValueSetLimit, provided.getFeatures().get(Features.PROVIDE_ELEMENT_VALUE_SET_OFFSET));
            assertEquals(elementValueSetLimit, provided.getFeatures().get(Features.PROVIDE_ELEMENT_VALUE_SET_LIMIT));

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testTraverseActionArguments() {
        try (SpongeRestClient client = createRestClient()) {
            RestActionMeta meta = client.getActionMeta("NestedRecordAsArgAction");
            RecordType bookType = (RecordType) meta.getArgs().get(0);
            RecordType authorType = (RecordType) bookType.getFields().get(1);

            assertEquals(meta.getArg("book"), bookType);
            assertEquals(meta.getArg("book.id"), bookType.getFields().get(0));
            assertEquals(meta.getArg("book.author"), authorType);
            assertEquals(meta.getArg("book.author.id"), authorType.getFields().get(0));
            assertEquals(meta.getArg("book.author.firstName"), authorType.getFields().get(1));
            assertEquals(meta.getArg("book.author.surname"), authorType.getFields().get(2));
            assertEquals(meta.getArg("book.title"), bookType.getFields().get(2));

            List<QualifiedDataType> namedQTypes = new ArrayList<>();
            RestApiUtils.traverseActionArguments(meta, qType -> namedQTypes.add(qType), true);
            assertEquals(namedQTypes.get(0).getPath(), "book");
            assertEquals(namedQTypes.get(0).getType(), meta.getArg("book"));
            assertEquals(namedQTypes.get(1).getPath(), "book.id");
            assertEquals(namedQTypes.get(1).getType(), meta.getArg("book.id"));
            assertEquals(namedQTypes.get(2).getPath(), "book.author");
            assertEquals(namedQTypes.get(2).getType(), meta.getArg("book.author"));
            assertEquals(namedQTypes.get(3).getPath(), "book.author.id");
            assertEquals(namedQTypes.get(3).getType(), meta.getArg("book.author.id"));
            assertEquals(namedQTypes.get(4).getPath(), "book.author.firstName");
            assertEquals(namedQTypes.get(4).getType(), meta.getArg("book.author.firstName"));
            assertEquals(namedQTypes.get(5).getPath(), "book.author.surname");
            assertEquals(namedQTypes.get(5).getType(), meta.getArg("book.author.surname"));
            assertEquals(namedQTypes.get(6).getPath(), "book.title");
            assertEquals(namedQTypes.get(6).getType(), meta.getArg("book.title"));

            assertFalse(engine.isError());
        }
    }

    @Test
    public void testSend() {
        try (SpongeRestClient client = createGuestRestClient()) {
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

    @Test
    public void testGetEventTypes() {
        try (SpongeRestClient client = createRestClient()) {
            Map<String, RecordType> eventTypes = client.getEventTypes();
            assertEquals(1, eventTypes.size());
            RemoteApiTestUtils.assertNotificationRecordType(eventTypes.get("notification"));
        }
    }

    @Test
    public void testGetEventType() {
        try (SpongeRestClient client = createRestClient()) {
            RemoteApiTestUtils.assertNotificationRecordType(client.getEventType("notification"));
        }
    }
}
