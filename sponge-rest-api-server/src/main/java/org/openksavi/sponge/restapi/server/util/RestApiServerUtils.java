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

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.server.RestApiServerConstants;
import org.openksavi.sponge.restapi.server.RestApiSettings;
import org.openksavi.sponge.restapi.server.security.User;
import org.openksavi.sponge.restapi.server.security.UserContext;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.type.value.AnnotatedValue;
import org.openksavi.sponge.util.DataTypeUtils;

/**
 * A set of REST API server utility methods.
 */
@SuppressWarnings("rawtypes")
public abstract class RestApiServerUtils {

    private RestApiServerUtils() {
        //
    }

    public static User createAnonymousUser(String anonymousRole) {
        return new User(RestApiServerConstants.DEFAULT_ANONYMOUS_USERNAME, null, anonymousRole);
    }

    public static UserContext createAnonymousUserContext(String anonymousRole) {
        return new UserContext(RestApiServerConstants.DEFAULT_ANONYMOUS_USERNAME, anonymousRole);
    }

    public static boolean isActionInternal(String actionName) {
        return actionName.startsWith(RestApiServerConstants.INTERNAL_ACTION_NAME_PREFIX);
    }

    /**
     * Verifies if a user can access a resource (e.g. a knowledge base, an event).
     *
     * @param roleToResources the map of (role name to resource names regexps).
     * @param userContext the user context.
     * @param resourceName the resource name.
     * @return {@code true} if the user can access the resource.
     */
    public static boolean canAccessResource(Map<String, Collection<String>> roleToResources, UserContext userContext, String resourceName) {
        if (roleToResources == null) {
            return false;
        }

        return userContext.getRoles().stream().filter(role -> roleToResources.containsKey(role)).anyMatch(role -> roleToResources.get(role)
                .stream().filter(Objects::nonNull).anyMatch(resourceRegexp -> resourceName.matches(resourceRegexp)));
    }

    public static List<Object> unmarshalActionCallArgs(TypeConverter typeConverter, ActionAdapter actionAdapter, List<Object> jsonArgs) {
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

    public static Object marshalActionCallResult(TypeConverter typeConverter, ActionAdapter actionAdapter, Object result) {
        if (result == null || actionAdapter.getMeta().getResult() == null) {
            return result;
        }

        return typeConverter.marshal(actionAdapter.getMeta().getResult(), result);
    }

    @SuppressWarnings({ "unchecked" })
    public static void marshalProvidedActionArgValues(TypeConverter typeConverter, ActionAdapter actionAdapter,
            Map<String, ProvidedValue<?>> providedValues, Map<String, DataType> dynamicTypes) {

        providedValues.forEach((argName, argValue) -> {
            DataType argType = dynamicTypes != null && dynamicTypes.containsKey(argName) ? dynamicTypes.get(argName)
                    : actionAdapter.getMeta().getArg(argName);

            ((ProvidedValue) argValue).setValue(typeConverter.marshal(argType, argValue.getValue()));

            if (argValue.getAnnotatedValueSet() != null) {
                ((ProvidedValue) argValue).setAnnotatedValueSet(argValue.getAnnotatedValueSet().stream()
                        .map(annotatedValue -> new AnnotatedValue(typeConverter.marshal(argType, annotatedValue.getValue()),
                                annotatedValue.getValueLabel(), annotatedValue.getValueDescription(), annotatedValue.getFeatures(),
                                annotatedValue.getTypeLabel(), annotatedValue.getTypeDescription()))
                        .collect(Collectors.toList()));
            }

            if (argValue.getAnnotatedElementValueSet() != null && DataTypeUtils.supportsElementValueSet(argType)) {
                ((ProvidedValue) argValue).setAnnotatedElementValueSet(argValue.getAnnotatedElementValueSet().stream()
                        .map(annotatedValue -> new AnnotatedValue(
                                typeConverter.marshal(((ListType) argType).getElementType(), annotatedValue.getValue()),
                                annotatedValue.getValueLabel(), annotatedValue.getValueDescription(), annotatedValue.getFeatures(),
                                annotatedValue.getTypeLabel(), annotatedValue.getTypeDescription()))
                        .collect(Collectors.toList()));
            }
        });
    }

    public static Map<String, Object> unmarshalAuxiliaryActionArgs(TypeConverter typeConverter, ActionAdapter actionAdapter,
            Map<String, Object> jsonArgs, Map<String, DataType> dynamicTypes) {
        // No arguments provided. No type checking.
        if (jsonArgs == null) {
            return null;
        }

        // Argument metadata not defined for this action. No type checking, returning raw values.
        if (actionAdapter.getMeta().getArgs() == null) {
            return jsonArgs;
        }

        Map<String, Object> unmarshalled = new LinkedHashMap<>();
        jsonArgs.forEach((name, value) -> unmarshalled.put(name, typeConverter.unmarshal(
                dynamicTypes != null && dynamicTypes.containsKey(name) ? dynamicTypes.get(name) : actionAdapter.getMeta().getArg(name),
                value)));

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

    public static String resolveServiceName(SpongeEngine engine, RestApiSettings settings) {
        if (settings.getName() != null) {
            return settings.getName();
        }

        if (engine.getLabel() != null) {
            return engine.getLabel();
        }

        if (engine.getName() != null) {
            return engine.getName();
        }

        return RestApiServerConstants.DEFAULT_NAME;
    }
}
