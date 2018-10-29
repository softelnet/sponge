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

package org.openksavi.sponge.restapi.type.converter;

import org.openksavi.sponge.type.Type;
import org.openksavi.sponge.type.TypeKind;

@SuppressWarnings("rawtypes")
public interface UnitTypeConverter<T extends Type> {

    TypeKind getTypeKind();

    // The {@code value} will never be null here.
    Object marshal(TypeConverter converter, T type, Object value);

    // The {@code value} will never be null here.
    Object unmarshal(TypeConverter converter, T type, Object value);
}
