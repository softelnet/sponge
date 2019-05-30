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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.openksavi.sponge.restapi.test.base.ComplexObject;
import org.openksavi.sponge.restapi.test.base.CompoundComplexObject;

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

    private RemoteApiTestUtils() {
        //
    }
}
