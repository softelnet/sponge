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

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.SpongeConstants;
import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.QualifiedDataType;
import org.openksavi.sponge.type.RecordType;

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

            // Verify Record/Map type.
            Validate.isTrue(argType instanceof RecordType, "The element %s is not a record", argType.getName());

            argTypeHolder.setValue(((RecordType) argType).getFields().stream().filter(fieldType -> fieldType.getName().equals(element))
                    .findFirst().orElse(null));
        });

        return argTypeHolder.getValue();
    }

    // Sub-arguments only for records.
    public static Map<String, DataType> createNamedActionArgTypesMap(ActionMeta actionMeta) {
        Map<String, DataType> argTypesMap = new LinkedHashMap<>();

        traverseActionArguments(actionMeta, qType -> argTypesMap.put(qType.getPath(), qType.getType()), true);

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
            actionMeta.getArgs().forEach(argType -> traverseDataType(new QualifiedDataType(argType.getName(), argType), onType, namedOnly));
        }
    }

    /**
     * Traverses the data type but only through record types.
     *
     * @param dataType the type.
     * @param onType the qualified type callback.
     * @param namedOnly traverse only through named types.
     */
    public static void traverseDataType(QualifiedDataType qType, Consumer<QualifiedDataType> onType, boolean namedOnly) {
        if (namedOnly && qType.getType().getName() == null) {
            return;
        }

        onType.accept(qType);

        // Traverses only through record types.
        switch (qType.getType().getKind()) {
        case RECORD:
            ((RecordType) qType.getType()).getFields().forEach(field -> traverseDataType(qType.createChild(field), onType, namedOnly));
            break;
        default:
            break;
        }
    }
}
