/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.QualifiedDataType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.value.AnnotatedValue;

/**
 * Data type utility methods.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class DataTypeUtils {

    private DataTypeUtils() {
    }

    /**
     * Traverses the data type but only through record types.
     *
     * @param qualifiedType the qualified type.
     * @param onType the qualified type callback.
     * @param namedOnly traverse only through named types.
     */
    public static void traverseDataType(QualifiedDataType qualifiedType, Consumer<QualifiedDataType> onType, boolean namedOnly) {
        if (namedOnly && qualifiedType.getType().getName() == null) {
            return;
        }

        onType.accept(qualifiedType);

        // Traverses only through record types.
        switch (qualifiedType.getType().getKind()) {
        case RECORD:
            ((RecordType) qualifiedType.getType()).getFields()
                    .forEach(field -> traverseDataType(qualifiedType.createChild(field), onType, namedOnly));
            break;
        default:
            break;
        }
    }

    public static <P> P getFeatureOrProperty(DataType type, Object value, String propertyName, Supplier<P> orElse) {
        P property = null;
        if (value instanceof AnnotatedValue) {
            property = (P) ((AnnotatedValue) value).getFeatures().get(propertyName);
        }

        if (property == null) {
            property = (P) type.getFeatures().get(propertyName);
        }

        return property != null ? property : orElse.get();
    }
}
