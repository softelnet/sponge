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

package org.openksavi.sponge.remoteapi.server.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.openksavi.sponge.remoteapi.server.RemoteApiSettings;
import org.openksavi.sponge.remoteapi.test.base.ComplexObject;
import org.openksavi.sponge.remoteapi.test.base.CompoundComplexObject;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.RecordType;

public abstract class RemoteApiTestUtils {

    public static ComplexObject createComplexObject() {
        ComplexObject complexObject = new ComplexObject();
        complexObject.setId(1l);
        complexObject.setName("TestComplexObject1");
        complexObject.setBigDecimal(new BigDecimal("1.25"));
        complexObject.setDate(LocalDateTime.now());

        return complexObject;
    }

    public static CompoundComplexObject createCompoundComplexObject() {
        CompoundComplexObject compoundObject = new CompoundComplexObject();
        compoundObject.setId(1l);
        compoundObject.setName("TestCompoundComplexObject1");
        compoundObject.setComplexObject(createComplexObject());

        return compoundObject;
    }

    public static void assertBookRecordType(RecordType bookType) {
        assertEquals(DataTypeKind.RECORD, bookType.getKind());
        assertEquals("book", bookType.getName());
        assertEquals(4, bookType.getFields().size());
        assertEquals("id", bookType.getFields().get(0).getName());
        assertEquals(DataTypeKind.INTEGER, bookType.getFields().get(0).getKind());
        assertEquals("author", bookType.getFields().get(1).getName());
        assertEquals(DataTypeKind.STRING, bookType.getFields().get(1).getKind());
        assertEquals("title", bookType.getFields().get(2).getName());
        assertEquals(DataTypeKind.STRING, bookType.getFields().get(2).getKind());
        assertEquals("comment", bookType.getFields().get(3).getName());
        assertEquals(DataTypeKind.STRING, bookType.getFields().get(3).getKind());
    }

    public static void assertPersonRecordType(RecordType personType) {
        assertNotNull(personType);
        assertEquals(2, personType.getFields().size());
        assertEquals("firstName", personType.getFields().get(0).getName());
        assertEquals(DataTypeKind.STRING, personType.getFields().get(0).getKind());
        assertEquals("surname", personType.getFields().get(1).getName());
        assertEquals(DataTypeKind.STRING, personType.getFields().get(1).getKind());
    }

    public static void assertCitizenRecordType(RecordType citizenType) {
        assertNotNull(citizenType);
        assertEquals(3, citizenType.getFields().size());
        assertEquals("firstName", citizenType.getFields().get(0).getName());
        assertEquals(DataTypeKind.STRING, citizenType.getFields().get(0).getKind());
        assertEquals("surname", citizenType.getFields().get(1).getName());
        assertEquals(DataTypeKind.STRING, citizenType.getFields().get(1).getKind());
        assertEquals("country", citizenType.getFields().get(2).getName());
        assertEquals(DataTypeKind.STRING, citizenType.getFields().get(2).getKind());
    }

    public static void assertNotificationRecordType(RecordType notificationType) {
        assertNotNull(notificationType);
        assertEquals(3, notificationType.getFields().size());
        assertEquals(DataTypeKind.STRING, notificationType.getFields().get(0).getKind());
        assertEquals("source", notificationType.getFields().get(0).getName());
        assertEquals("Source", notificationType.getFields().get(0).getLabel());
        assertEquals(DataTypeKind.INTEGER, notificationType.getFields().get(1).getKind());
        assertEquals("severity", notificationType.getFields().get(1).getName());
        assertEquals("Severity", notificationType.getFields().get(1).getLabel());

        assertPersonRecordType(notificationType.getFieldType("person"));
    }

    public static void setupRemoteService(RemoteApiSettings settings) {
        settings.setName("Sponge Test Remote API");
        settings.setDescription("Sponge Test Remote API description");
        settings.setLicense("Apache 2.0");
    }

    private RemoteApiTestUtils() {
        //
    }
}
