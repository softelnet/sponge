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

package org.openksavi.sponge.remoteapi.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.features.model.geo.GeoLayer;
import org.openksavi.sponge.remoteapi.feature.converter.FeaturesUtils;
import org.openksavi.sponge.remoteapi.model.RemoteActionMeta;
import org.openksavi.sponge.remoteapi.model.RemoteDataType;
import org.openksavi.sponge.remoteapi.model.RemoteEvent;
import org.openksavi.sponge.remoteapi.model.RemoteGeoLayer;
import org.openksavi.sponge.remoteapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.QualifiedDataType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.value.AnnotatedValue;
import org.openksavi.sponge.util.DataTypeUtils;

/**
 * A set of common Remote API utility methods.
 */
public abstract class RemoteApiUtils {

    private RemoteApiUtils() {
        //
    }

    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());

        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER, true);

        // Ensure a proper Type inheritance hierarchy deserialization from JSON to Java.
        mapper.addMixIn(DataType.class, RemoteDataType.class);

        mapper.addMixIn(GeoLayer.class, RemoteGeoLayer.class);

        return mapper;
    }

    /**
     * Finds a class by the name.
     *
     * @param className the class name.
     * @return the class or {@code null} if not found.
     */
    public static Class<?> getClass(String className) {
        try {
            return ClassUtils.getClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static boolean isHttpSuccess(int code) {
        return 200 <= code && code <= 299;
    }

    public static String obfuscatePassword(String text) {
        if (text == null) {
            return null;
        }

        Matcher m = Pattern.compile("\"(\\w*password\\w*)\":\".*?\"", Pattern.CASE_INSENSITIVE).matcher(text);

        return m.find() ? m.replaceAll("\"$1\":\"***\"") : text;
    }

    /**
     * Traverses the action argument types but only through record types.
     *
     * @param actionMeta the action metadata.
     * @param onType the qualified type callback.
     * @param namedOnly traverse only through named types.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void traverseActionArguments(RemoteActionMeta actionMeta, Consumer<QualifiedDataType> onType, boolean namedOnly) {

        if (actionMeta.getArgs() != null) {
            actionMeta.getArgs().forEach(
                    argType -> DataTypeUtils.traverseDataType(new QualifiedDataType(argType.getName(), argType), onType, namedOnly, true));
        }
    }

    @SuppressWarnings("rawtypes")
    public static boolean isAnnotatedValueMap(Object value) {
        return value instanceof Map && AnnotatedValue.FIELDS.equals(((Map) value).keySet());
    }

    @SuppressWarnings("unchecked")
    public static RemoteEvent marshalRemoteEvent(RemoteEvent event, TypeConverter converter,
            Function<String, RecordType> eventTypeSupplier) {
        if (event == null) {
            return null;
        }

        event = event.clone();

        RecordType eventType = eventTypeSupplier != null ? eventTypeSupplier.apply(event.getName()) : null;
        if (eventType != null) {
            event.setAttributes((Map<String, Object>) converter.marshal(eventType, event.getAttributes()));
        }

        event.setFeatures(FeaturesUtils.marshal(converter.getFeatureConverter(), event.getFeatures()));

        return event;
    }

    @SuppressWarnings("unchecked")
    public static RemoteEvent unmarshalRemoteEvent(Object eventObject, TypeConverter converter,
            Function<String, RecordType> eventTypeSupplier) {
        if (eventObject == null) {
            return null;
        }

        RemoteEvent event = (eventObject instanceof RemoteEvent) ? (RemoteEvent) eventObject
                : converter.getObjectMapper().convertValue(eventObject, RemoteEvent.class);

        if (event == null) {
            return null;
        }

        RecordType eventType = eventTypeSupplier != null ? eventTypeSupplier.apply(event.getName()) : null;
        if (eventType != null) {
            event.setAttributes((Map<String, Object>) converter.unmarshal(eventType, event.getAttributes()));
        }

        event.setFeatures(FeaturesUtils.unmarshal(converter.getFeatureConverter(), event.getFeatures()));

        return event;
    }

    @SuppressWarnings("rawtypes")
    public static int getActionArgIndex(RemoteActionMeta actionMeta, String argName) {
        List<DataType> args = actionMeta.getArgs();
        Validate.notNull(args, "Action %s doesn't have argument metadata", actionMeta.getName());

        for (int i = 0; i < args.size(); i++) {
            if (Objects.equals(args.get(i).getName(), argName)) {
                return i;
            }
        }

        throw new SpongeException(String.format("Argument %s not found", argName));
    }
}
