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

package org.openksavi.sponge.core.action;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.core.engine.EngineConstants;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.RecordTypeField;

public abstract class ActionUtils {

    protected ActionUtils() {
        //
    }

    public static Map<String, ArgMeta> createFullActionArgsMetaMap(ActionMeta actionMeta) {
        Map<String, ArgMeta> argMetaMap = new LinkedHashMap<>();
        actionMeta.getArgsMeta().forEach(argMeta -> populateFullActionArgsMetaMap(argMetaMap, null, argMeta));

        return argMetaMap;
    }

    protected static Map<String, ArgMeta> populateFullActionArgsMetaMap(Map<String, ArgMeta> argMetaMap, String parentArgName,
            ArgMeta argMeta) {
        String thisArgName = (parentArgName != null ? parentArgName + EngineConstants.ACTION_SUB_ARG_SEPARATOR : "") + argMeta.getName();
        argMetaMap.put(thisArgName, argMeta);
        argMeta.getSubArgs().forEach(subArgMeta -> populateFullActionArgsMetaMap(argMetaMap, thisArgName, subArgMeta));

        return argMetaMap;
    }

    public static ArgMeta mergeRecordFieldToArgMeta(ArgMeta argMeta, RecordTypeField field) {
        if (argMeta.getName() == null) {
            argMeta.setName(field.getName());
        }

        if (argMeta.getLabel() == null) {
            argMeta.setLabel(field.getLabel());
        }

        if (argMeta.getDescription() == null) {
            argMeta.setDescription(field.getDescription());
        }

        // Merge features.
        Map<String, Object> features = new LinkedHashMap<>();
        features.putAll(field.getFeatures());
        features.putAll(argMeta.getFeatures());
        argMeta.setFeatures(features);

        // Overwrite the type.
        Validate.isTrue(argMeta.getType().getKind() == DataTypeKind.ANY, "Sub-argument metadata should not specify a data type");
        argMeta.setType(field.getType());

        return argMeta;
    }

    public static List<String> getArgNameElements(String name) {
        return Arrays.asList(name.split("\\" + EngineConstants.ACTION_SUB_ARG_SEPARATOR));
    }
}
