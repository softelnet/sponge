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

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.SpongeConstants;
import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.RecordType;

/**
 * A Sponge API utility methods.
 */
@SuppressWarnings("rawtypes")
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
        actionMeta.getArgs().forEach(argType -> populateNamedActionArgTypesMap(argTypesMap, null, argType));

        return argTypesMap;
    }

    protected static Map<String, DataType> populateNamedActionArgTypesMap(Map<String, DataType> argTypesMap, String parentArgName,
            DataType argType) {
        if (argType.getName() == null) {
            return argTypesMap;
        }

        String thisArgName = (parentArgName != null ? parentArgName + SpongeConstants.ACTION_SUB_ARG_SEPARATOR : "") + argType.getName();
        argTypesMap.put(thisArgName, argType);

        // Sub-arguments only for records.
        if (argType instanceof RecordType) {
            ((RecordType) argType).getFields().forEach(subArgType -> populateNamedActionArgTypesMap(argTypesMap, thisArgName, subArgType));
        }

        return argTypesMap;
    }

    public static List<String> getActionArgNameElements(String name) {
        return Arrays.asList(name.split("\\" + SpongeConstants.ACTION_SUB_ARG_SEPARATOR));
    }
}
