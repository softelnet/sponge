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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.openksavi.sponge.SpongeConstants;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.MapType;
import org.openksavi.sponge.type.ObjectType;
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
     * Traverses the data type.
     *
     * @param qualifiedType the qualified type.
     * @param onType the qualified type callback.
     * @param namedOnly traverse only through named types.
     * @param traverseCollections traverse collections.
     */
    public static void traverseDataType(QualifiedDataType qualifiedType, Consumer<QualifiedDataType> onType, boolean namedOnly,
            boolean traverseCollections) {
        if (namedOnly && qualifiedType.getType().getName() == null) {
            return;
        }

        onType.accept(qualifiedType);

        List<QualifiedDataType> subTypes = new ArrayList<>();

        // Traverses only through record types.
        switch (qualifiedType.getType().getKind()) {
        case RECORD:
            ((RecordType) qualifiedType.getType()).getFields().forEach(field -> subTypes.add(qualifiedType.createChild(field)));
            break;
        case LIST:
            if (traverseCollections) {
                DataType elementType = ((ListType) qualifiedType.getType()).getElementType();
                subTypes.add(new QualifiedDataType(
                        qualifiedType.getPath() != null && elementType.getName() != null
                                ? qualifiedType.getPath() + SpongeConstants.ACTION_SUB_ARG_SEPARATOR + elementType.getName() : null,
                        elementType));
            }
            break;
        case MAP:
            if (traverseCollections) {
                DataType keyType = ((MapType) qualifiedType.getType()).getKeyType();
                DataType valueType = ((MapType) qualifiedType.getType()).getValueType();
                subTypes.add(new QualifiedDataType(qualifiedType.getPath() != null && keyType.getName() != null
                        ? qualifiedType.getPath() + SpongeConstants.ACTION_SUB_ARG_SEPARATOR + keyType.getName() : null, keyType));
                subTypes.add(new QualifiedDataType(
                        qualifiedType.getPath() != null && valueType.getName() != null
                                ? qualifiedType.getPath() + SpongeConstants.ACTION_SUB_ARG_SEPARATOR + valueType.getName() : null,
                        valueType));
            }
            break;
        case OBJECT:
            ObjectType objectType = ((ObjectType) qualifiedType.getType());
            if (objectType.getCompanionType() != null) {
                subTypes.add(qualifiedType.createChild(objectType.getCompanionType()));
            }
            break;
        default:
            break;
        }

        subTypes.forEach((subType) -> traverseDataType(subType, onType, namedOnly, traverseCollections));
    }

    public static void traverseDataType(QualifiedDataType qualifiedType, Consumer<QualifiedDataType> onType) {
        traverseDataType(qualifiedType, onType, true, false);
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

    public static Set<String> getRegisteredTypeNames(DataType type) {
        Set<String> types = new LinkedHashSet<>();

        traverseDataType(new QualifiedDataType(type), qualifiedType -> {
            if (qualifiedType.getType().getRegisteredType() != null) {
                types.add(qualifiedType.getType().getRegisteredType());
            }

            // Walk through record base types.
            if (qualifiedType.getType() instanceof RecordType) {
                RecordType recordType = (RecordType) qualifiedType.getType();
                if (recordType.getBaseType() != null) {
                    types.addAll(getRegisteredTypeNames(recordType.getBaseType()));
                }
            }
        }, false, true);

        return types;
    }

    public static boolean supportsElementValueSet(DataType<?> type) {
        return type instanceof ListType;
    }

    public static boolean isProvidedRead(DataType<?> type) {
        return type.getProvided() != null
                && (type.getProvided().isValue() || type.getProvided().getValueSet() != null || type.getProvided().isElementValueSet());
    }

    public static boolean isProvidedWrite(DataType<?> type) {
        return type.getProvided() != null && type.getProvided().getSubmittable() != null;
    }

    public static List<DataType> getTypes(DataType type) {
        List<DataType> types = new ArrayList<>();
        traverseDataType(new QualifiedDataType(null, type), qualifiedType -> types.add(qualifiedType.getType()), false, true);

        return types;
    }
}
