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

package org.openksavi.sponge.restapi.server.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.action.ArgValue;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.restapi.server.RestApiServerConstants;
import org.openksavi.sponge.restapi.server.security.User;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;

/**
 * A set of REST API server utility methods.
 */
public abstract class RestApiServerUtils {

    private RestApiServerUtils() {
        //
    }

    public static User createAnonymousUser(String guestRole) {
        User user = new User(RestApiServerConstants.DEFAULT_ANONYMOUS_USERNAME, null);
        user.addRoles(guestRole);

        return user;
    }

    public static boolean isActionPrivate(String actionName) {
        return actionName.startsWith(RestApiServerConstants.PRIVATE_ACTION_NAME_PREFIX);
    }

    /**
     * Verifies if a user can use a knowledge base.
     *
     * @param roleToKnowledgeBases the map of (role name to knowledge base names regexps).
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

    public static List<Object> unmarshalActionCallArgs(TypeConverter typeConverter, ActionAdapter actionAdapter, List<Object> jsonArgs,
            Exchange exchange) {
        // No arguments provided. No type checking.
        if (jsonArgs == null) {
            return null;
        }

        // Argument metadata not defined for this action. No type checking, returning raw values.
        if (actionAdapter.getArgsMeta() == null) {
            return jsonArgs;
        }

        List<Object> finalArgs = new ArrayList<>();
        int index = 0;
        for (Object jsonArg : jsonArgs) {
            Object finalArg = jsonArg;
            // Unmarshal only those who have metadata. Others are returned as raw values.
            if (finalArg != null && index < actionAdapter.getArgsMeta().size()) {
                ArgMeta<?> argMeta = actionAdapter.getArgsMeta().get(index);
                if (argMeta != null && argMeta.getType() != null) {
                    try {
                        finalArg = typeConverter.unmarshal(argMeta.getType(), finalArg);
                    } catch (Exception e) {
                        throw SpongeUtils.wrapException(String.format("Unmarshal argument %s in the %s action",
                                argMeta.getName() != null ? argMeta.getName() : index, actionAdapter.getName()), e);
                    }
                }
            }

            finalArgs.add(finalArg);
            index++;
        }

        return finalArgs;
    }

    public static Object marshalActionCallResult(TypeConverter typeConverter, ActionAdapter actionAdapter, Object result,
            Exchange exchange) {
        if (result == null || actionAdapter.getResultMeta() == null) {
            return result;
        }

        return typeConverter.marshal(actionAdapter.getResultMeta().getType(), result);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void marshalProvidedActionArgValues(TypeConverter typeConverter, ActionAdapter actionAdapter,
            Map<String, ArgValue<?>> argValues) {

        argValues.forEach((argName, argValue) -> {
            ArgMeta<?> argMeta = actionAdapter.getArgMeta(argName);
            ((ArgValue) argValue).setValue(typeConverter.marshal(argMeta.getType(), argValue.getValue()));

            if (argValue.getValueSet() != null) {
                ((ArgValue) argValue).setValueSet(argValue.getValueSet().stream()
                        .map(value -> typeConverter.marshal(argMeta.getType(), value)).collect(Collectors.toList()));
            }
        });
    }

    public static Map<String, Object> unmarshalProvideActionArgs(TypeConverter typeConverter, ActionAdapter actionAdapter,
            Map<String, Object> jsonArgs, Exchange exchange) {
        // No arguments provided. No type checking.
        if (jsonArgs == null) {
            return null;
        }

        // Argument metadata not defined for this action. No type checking, returning raw values.
        if (actionAdapter.getArgsMeta() == null) {
            return jsonArgs;
        }

        Map<String, Object> unmarshalled = new LinkedHashMap<>();
        jsonArgs.forEach((name, value) -> unmarshalled.put(name, typeConverter.unmarshal(actionAdapter.getArgMeta(name).getType(), value)));

        return unmarshalled;
    }
}
