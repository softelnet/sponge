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

package org.openksavi.sponge.restapi.model.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.core.util.PatternStringReplacer;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.model.type.RestType;
import org.openksavi.sponge.restapi.security.User;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.MapType;
import org.openksavi.sponge.type.ObjectType;
import org.openksavi.sponge.type.Type;

/**
 * A set of REST API utility methods.
 */
public abstract class RestApiUtils {

    private static final PatternStringReplacer PASSWORD_REPLACER =
            new PatternStringReplacer(RestApiConstants.JSON_REQUEST_PASSWORD_REGEXP, RestApiConstants.JSON_REQUEST_PASSWORD_REPLACE);

    private RestApiUtils() {
        //
    }

    public static User createAnonymousUser(String guestRole) {
        User user = new User(RestApiConstants.DEFAULT_ANONYMOUS_USERNAME, null);
        user.addRoles(guestRole);

        return user;
    }

    public static boolean isActionPrivate(String actionName) {
        return actionName.startsWith(RestApiConstants.PRIVATE_ACTION_NAME_PREFIX);
    }

    /**
     * Verifies if a user can use a knowledge base.
     *
     * @param roleToKnowledgeBases the map of (role name -> knowledge base names regexps).
     * @param user the user.
     * @param kbName the knowledge base name.
     * @return {@code true} if this user can use the knowledge base.
     */
    public static boolean canUseKnowledgeBase(Map<String, Collection<String>> roleToKnowledgeBases, User user, String kbName) {
        if (roleToKnowledgeBases == null) {
            return false;
        }

        return user.getRoles().stream().filter(role -> roleToKnowledgeBases.containsKey(role)).anyMatch(
                role -> roleToKnowledgeBases.get(role).stream().filter(Objects::nonNull).anyMatch(kbRegexp -> kbName.matches(kbRegexp)));
    }

    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Ensure a proper Type inheritance hierarchy deserialization from JSON to Java.
        mapper.addMixIn(Type.class, RestType.class);

        return mapper;
    }

    public static Object[] unmarshalActionArgs(ObjectMapper mapper, ActionAdapter actionAdapter, List<Object> jsonArgs) {
        // No arguments provided. No type checking.
        if (jsonArgs == null) {
            return null;
        }

        // Argument metadata not defined for this action. No type checking, returning raw values.
        if (actionAdapter.getArgsMeta() == null) {
            return jsonArgs.toArray();
        }

        List<Object> finalArgs = new ArrayList<>();
        int index = 0;
        for (Object jsonArg : jsonArgs) {
            Object finalArg = jsonArg;
            // Unmarshal only those who have metadata. Others are returned as raw values.
            if (jsonArg != null && index < actionAdapter.getArgsMeta().length) {
                ArgMeta<?> argMeta = actionAdapter.getArgsMeta()[index];
                if (argMeta != null && argMeta.getType() != null) {
                    finalArg = unmarshalValue(mapper, argMeta.getType(), jsonArg, String.format("argument %s in the action %s",
                            argMeta.getName() != null ? argMeta.getName() : index, actionAdapter.getName()));
                }
            }

            finalArgs.add(finalArg);
            index++;
        }

        return finalArgs.toArray();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected static Object unmarshalValue(ObjectMapper mapper, Type type, Object jsonValue, String valueName) {
        if (jsonValue == null || type == null) {
            return null;
        }

        // Note that metadata not null checks have been performed while enabling the action.
        switch (type.getKind()) {
        case OBJECT:
            return mapper.convertValue(jsonValue, SpongeUtils.getClass(((ObjectType) type).getClassName()));
        case LIST:
            Validate.isInstanceOf(Collection.class, jsonValue, "Non list type in the %s: %s", valueName, jsonValue.getClass());

            return ((Collection) jsonValue).stream()
                    .map((Object jsonElem) -> unmarshalValue(mapper, ((ListType) type).getElementType(), jsonElem, valueName))
                    .collect(Collectors.toList());
        case MAP:
            Validate.isInstanceOf(Map.class, jsonValue, "Non map type in the %s: %s", valueName, jsonValue.getClass());

            return ((Map) jsonValue).entrySet().stream()
                    .collect(Collectors.toMap(
                            (Map.Entry entry) -> unmarshalValue(mapper, ((MapType) type).getKeyType(), entry.getKey(), valueName),
                            (Map.Entry entry) -> unmarshalValue(mapper, ((MapType) type).getValueType(), entry.getValue(), valueName)));
        default:
            return jsonValue;
        }
    }

    public static Object unmarshalActionResult(ObjectMapper mapper, ResultMeta<?> resultMeta, Object jsonResult) {
        return unmarshalValue(mapper, resultMeta.getType(), jsonResult, String.format("result of the action"));
    }

    public static String hidePassword(String text) {
        return PASSWORD_REPLACER.replaceAll(text);
    }
}
