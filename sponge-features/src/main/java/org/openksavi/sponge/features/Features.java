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

package org.openksavi.sponge.features;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;

/**
 * The predefined action and type features.
 */
public final class Features {

    private Features() {
        //
    }

    public static final String INTENT = "intent";

    public static final String VISIBLE = "visible";

    public static final String ICON = "icon";

    public static final String ACTION_CONFIRMATION = "confirmation";

    public static final String TYPE_CHARACTERISTIC = "characteristic";

    public static final String TYPE_CHARACTERISTIC_DRAWING = "drawing";

    public static final String TYPE_CHARACTERISTIC_COLOR = "color";

    public static final String TYPE_FILENAME = "filename";

    public static final String ACTION_INTENT_VALUE_LOGIN = "login";

    public static final String ACTION_INTENT_VALUE_LOGOUT = "logout";

    public static final String ACTION_INTENT_VALUE_SIGN_UP = "signUp";

    public static final String TYPE_INTENT_VALUE_USERNAME = "username";

    public static final String TYPE_INTENT_VALUE_PASSWORD = "password";

    public static final String ACTION_INTENT_VALUE_SUBSCRIPTION = "subscription";

    public static final String TYPE_INTENT_VALUE_EVENT_NAMES = "eventNames";

    public static final String TYPE_INTENT_VALUE_SUBSCRIBE = "subscribe";

    public static final String ACTION_REFRESH_EVENTS = "refreshEvents";

    public static final String EVENT_HANDLER_ACTION = "handlerAction";

    public static final String ACTION_INTENT_DEFAULT_EVENT_HANDLER = "defaultEventHandler";

    public static final String STRING_MULTILINE = "multiline";

    public static final String STRING_MAX_LINES = "maxLines";

    public static final String STRING_OBSCURE = "obscure";

    public static final String STRING_RESPONSIVE = "responsive";

    public static final String ACTION_CALL_LABEL = "callLabel";

    public static final String ACTION_REFRESH_LABEL = "refreshLabel";

    public static final String ACTION_CLEAR_LABEL = "clearLabel";

    public static final String ACTION_CANCEL_LABEL = "cancelLabel";

    public static final String CONTEXT_ACTIONS = "contextActions";

    public static final String ACTION_ARG_LIST_CREATE_ACTION = "createAction";

    public static final String ACTION_ARG_LIST_READ_ACTION = "readAction";

    public static final String ACTION_ARG_LIST_UPDATE_ACTION = "updateAction";

    public static final String ACTION_ARG_LIST_DELETE_ACTION = "deleteAction";

    public static final String BINARY_WIDTH = "width";

    public static final String BINARY_HEIGHT = "height";

    public static final String BINARY_STROKE_WIDTH = "strokeWidth";

    public static final String BINARY_COLOR = "color";

    public static final String BINARY_BACKGROUND = "background";

    public static final class Formats {

        public static final String STRING_FORMAT_PHONE = "phone";

        public static final String STRING_FORMAT_EMAIL = "email";

        public static final String STRING_FORMAT_URL = "url";

        public static final String STRING_FORMAT_CONSOLE = "console";

        public static final String STRING_FORMAT_MARKDOWN = "markdown";
    }

    public static String getCharacteristic(Map<String, Object> features) {
        Object characteristic = features.get(TYPE_CHARACTERISTIC);

        if (characteristic != null) {
            Validate.isInstanceOf(String.class, characteristic, "The characteristic feature should be a string");
        }

        return (String) characteristic;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getOptional(Map<String, Object> features, String name, Supplier<T> defaultValue) {
        return features.containsKey(name) ? (T) features.get(name) : defaultValue.get();
    }

    @SuppressWarnings("unchecked")
    public static <T> T findFeature(List<Map<String, Object>> featuresList, String name) {
        return (T) featuresList.stream().map(features -> features.get(name)).filter(feature -> feature != null).findFirst().orElse(null);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List<String> getStringList(Map<String, Object> features, String name) {
        Object feature = features.get(name);
        if (feature instanceof Collection) {
            Stream<String> featureStream = ((Collection) feature).stream().<String>map(f -> (String) f);
            return featureStream.collect(Collectors.toList());
        } else if (feature instanceof String) {
            // Allow converting a single string feature to a list.
            return Arrays.asList((String) feature);
        }

        return Collections.emptyList();
    }
}
