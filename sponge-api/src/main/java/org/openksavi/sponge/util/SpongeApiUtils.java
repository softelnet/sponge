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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.SpongeConstants;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.QualifiedDataType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.value.AnnotatedValue;

/**
 * A Sponge API utility methods.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class SpongeApiUtils {

    private SpongeApiUtils() {
    }

    public static int getActionArgIndex(List<DataType> argTypes, String argName) {
        if (argTypes != null) {
            for (int i = 0; i < argTypes.size(); i++) {
                if (argTypes.get(i).getName().equals(argName)) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static DataType getActionArgType(List<DataType> argTypes, String argName) {
        Validate.notNull(argTypes, "Arguments not defined");

        List<String> elements = getActionArgNameElements(argName);

        ValueHolder<DataType> argTypeHolder = new ValueHolder<>(argTypes.get(getActionArgIndex(argTypes, elements.get(0))));
        elements.stream().skip(1).limit(elements.size() - 1).forEach(element -> {
            DataType argType = argTypeHolder.getValue();

            Validate.notNull(argTypeHolder.getValue(), "Argument '%s' not found", argName);
            Validate.notNull(argType.getName(), "The sub-type nas no name");

            if (argType instanceof RecordType) {
                argTypeHolder.setValue(((RecordType) argType).getFieldType(element));
            } else if (argType instanceof ListType) {
                argTypeHolder.setValue(((ListType) argType).getElementType());
            } else {
                throw new SpongeException(String.format("The element %s is not a record or a list",
                        argType.getName() != null ? argType.getName() : argType.getKind().name()));
            }
        });

        return argTypeHolder.getValue();
    }

    // Sub-arguments only for records.
    public static Map<String, DataType> createNamedActionArgTypesMapRecursively(ActionMeta actionMeta) {
        Map<String, DataType> argTypesMap = new LinkedHashMap<>();

        traverseActionArguments(actionMeta, qualifiedType -> argTypesMap.put(qualifiedType.getPath(), qualifiedType.getType()), true);

        return argTypesMap;
    }

    public static List<String> getActionArgNameElements(String name) {
        return Arrays.asList(name.split("\\" + SpongeConstants.ACTION_SUB_ARG_SEPARATOR));
    }

    /**
     * Traverses the action argument types but only through record types.
     *
     * @param actionMeta the action metadata.
     * @param onType the qualified type callback.
     * @param namedOnly traverse only through named types.
     */
    public static void traverseActionArguments(ActionMeta actionMeta, Consumer<QualifiedDataType> onType, boolean namedOnly) {
        if (actionMeta.getArgs() != null) {
            actionMeta.getArgs().forEach(
                    argType -> DataTypeUtils.traverseDataType(new QualifiedDataType(argType.getName(), argType), onType, namedOnly, true));
        }
    }

    public static <K1, V1, K2, V2> Map<K2, V2> collectToLinkedMap(Map<K1, V1> source, Function<Map.Entry<K1, V1>, ? extends K2> keyMapper,
            Function<Map.Entry<K1, V1>, ? extends V2> valueMapper) {
        if (source == null) {
            return null;
        }

        Map<K2, V2> result = new LinkedHashMap<>();
        source.entrySet().forEach(entry -> result.put(keyMapper.apply(entry), valueMapper.apply(entry)));

        return result;
    }

    public static <T, K, V> Collector<T, ?, Map<K, V>> collectorToLinkedMap(Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends V> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper, (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        }, LinkedHashMap::new);
    }

    public static <T> List<T> unwrapAnnotatedValueList(List<AnnotatedValue<T>> annotatedValueList) {
        return annotatedValueList != null ? annotatedValueList.stream()
                .map(annotatedValue -> annotatedValue != null ? annotatedValue.getValue() : null).collect(Collectors.toList()) : null;
    }

    public static List<?> unwrapUnknownAnnotatedValueList(List<AnnotatedValue> annotatedValueList) {
        return annotatedValueList != null ? annotatedValueList.stream()
                .map(annotatedValue -> annotatedValue != null ? annotatedValue.getValue() : null).collect(Collectors.toList()) : null;
    }

    public static void validateActionCallArgs(List<DataType> argsMeta, List args) {
        if (argsMeta == null) {
            return;
        }

        int expectedAllArgCount = argsMeta.size();
        int expectedNonOptionalArgCount = (int) argsMeta.stream().filter(argType -> !argType.isOptional()).count();
        int actualArgCount = args != null ? args.size() : 0;

        if (expectedNonOptionalArgCount == expectedAllArgCount) {
            Validate.isTrue(expectedAllArgCount == actualArgCount, "Incorrect number of arguments. Expected %d but got %d",
                    expectedAllArgCount, actualArgCount);
        } else {
            Validate.isTrue(expectedNonOptionalArgCount <= actualArgCount && actualArgCount <= expectedAllArgCount,
                    "Incorrect number of arguments. Expected between %d and %d but got %d", expectedNonOptionalArgCount,
                    expectedAllArgCount, actualArgCount);
        }

        // Validate non-nullable arguments.
        for (int i = 0; i < argsMeta.size() && i < args.size(); i++) {
            validateActionCallArg(argsMeta.get(i), args.get(i));
        }
    }

    public static void validateActionCallArg(DataType argType, Object value) {
        Validate.isTrue(argType.isOptional() || argType.isNullable() || value != null, "Action argument '%s' is not set",
                argType.getLabel() != null ? argType.getLabel() : argType.getName());
    }
}
