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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.server.RestApiServerConstants;
import org.openksavi.sponge.restapi.server.security.User;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.type.value.AnnotatedValue;

/**
 * A set of REST API server utility methods.
 */
@SuppressWarnings("rawtypes")
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
        if (actionAdapter.getMeta().getArgs() == null) {
            return jsonArgs;
        }

        List<Object> finalArgs = new ArrayList<>();
        int index = 0;
        for (Object jsonArg : jsonArgs) {
            Object finalArg = jsonArg;
            // Unmarshal only those who have metadata. Others are returned as raw values.
            if (finalArg != null && index < actionAdapter.getMeta().getArgs().size()) {
                DataType argType = actionAdapter.getMeta().getArgs().get(index);
                if (argType != null) {
                    try {
                        finalArg = typeConverter.unmarshal(argType, finalArg);
                    } catch (Exception e) {
                        throw SpongeUtils.wrapException(String.format("Unmarshal argument %s in the %s action",
                                argType.getName() != null ? argType.getName() : index, actionAdapter.getMeta().getName()), e);
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
        if (result == null || actionAdapter.getMeta().getResult() == null) {
            return result;
        }

        return typeConverter.marshal(actionAdapter.getMeta().getResult(), result);
    }

    @SuppressWarnings({ "unchecked" })
    public static void marshalProvidedActionArgValues(TypeConverter typeConverter, ActionAdapter actionAdapter,
            Map<String, ProvidedValue<?>> providedValues) {

        providedValues.forEach((argName, argValue) -> {
            DataType argType = actionAdapter.getMeta().getArg(argName);
            ((ProvidedValue) argValue).setValue(typeConverter.marshal(argType, argValue.getValue()));

            if (argValue.getAnnotatedValueSet() != null) {
                ((ProvidedValue) argValue).setAnnotatedValueSet(argValue.getAnnotatedValueSet().stream()
                        .map(annotatedValue -> new AnnotatedValue(typeConverter.marshal(argType, annotatedValue.getValue()),
                                annotatedValue.getLabel(), annotatedValue.getDescription(), annotatedValue.getFeatures()))
                        .collect(Collectors.toList()));
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
        if (actionAdapter.getMeta().getArgs() == null) {
            return jsonArgs;
        }

        Map<String, Object> unmarshalled = new LinkedHashMap<>();
        jsonArgs.forEach((name, value) -> unmarshalled.put(name, typeConverter.unmarshal(actionAdapter.getMeta().getArg(name), value)));

        return unmarshalled;
    }

    /**
     * Actions will be sorted by a category sequence number, a knowledge base sequence number and an action label or name. The sequence
     * number reflects the order in which categories or knowledge bases have been added to the engine.
     *
     * @return the comparator.
     */
    public static Comparator<RestActionMeta> createActionsOrderComparator() {
        return (action1, action2) -> {
            if (action1.getCategory() != null && action2.getCategory() == null) {
                return -1;
            }
            if (action1.getCategory() == null && action2.getCategory() != null) {
                return 1;
            }

            if (action1.getCategory() != null && action2.getCategory() != null) {
                Integer catSeq1 = action1.getCategory().getSequenceNumber();
                Integer catSeq2 = action2.getCategory().getSequenceNumber();

                if (catSeq1 != null && catSeq2 == null) {
                    return -1;
                }
                if (catSeq1 == null && catSeq2 != null) {
                    return 1;
                }

                int categoryComparison = (catSeq1 != null && catSeq2 != null) ? catSeq1.compareTo(catSeq2) : 0;
                if (categoryComparison != 0) {
                    return categoryComparison;
                }
            }

            Integer kbSeq1 = action1.getKnowledgeBase().getSequenceNumber();
            Integer kbSeq2 = action2.getKnowledgeBase().getSequenceNumber();

            if (kbSeq1 != null && kbSeq2 == null) {
                return -1;
            }
            if (kbSeq1 == null && kbSeq2 != null) {
                return 1;
            }

            int kbComparison = (kbSeq1 != null && kbSeq2 != null) ? kbSeq1.compareTo(kbSeq2) : 0;
            if (kbComparison != 0) {
                return kbComparison;
            }

            return SpongeUtils.getDisplayLabel(action1).compareTo(SpongeUtils.getDisplayLabel(action2));
        };
    }
}
