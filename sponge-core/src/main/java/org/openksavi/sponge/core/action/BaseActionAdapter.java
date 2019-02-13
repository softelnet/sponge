/*
 * Copyright 2016-2017 The Sponge authors.
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

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.action.ArgProvidedValue;
import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.core.BaseProcessorAdapter;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.ProcessorType;

/**
 * A base action adapter.
 */
public class BaseActionAdapter extends BaseProcessorAdapter<Action> implements ActionAdapter {

    /**
     * Creates a new action adapter.
     *
     * @param definition a base action definition.
     */
    public BaseActionAdapter(BaseActionDefinition definition) {
        super(definition);
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.ACTION;
    }

    @Override
    public BaseActionDefinition getDefinition() {
        return (BaseActionDefinition) super.getDefinition();
    }

    @Override
    public BaseActionMeta getMeta() {
        return (BaseActionMeta) super.getMeta();
    }

    @Override
    public Map<String, ArgProvidedValue<?>> provideArgs(List<String> names, Map<String, Object> current) {
        Validate.notNull(getMeta().getArgsMeta(), "Argument metadata not defined");

        Set<String> allProvidedArguments =
                getMeta().getArgsMeta().stream().filter(BaseActionAdapter::isArgProvided).map(ArgMeta::getName).collect(Collectors.toSet());
        Set<String> finalNames = new LinkedHashSet<>();

        if (names != null) {
            for (String name : names) {
                Validate.isTrue(isArgProvided(getMeta().getArgMeta(name)), "Argument '%s' is not defined as provided", name);
                finalNames.add(name);
            }
        } else {
            // Use all provided argument names.
            finalNames.addAll(allProvidedArguments);
        }

        if (current == null) {
            current = Collections.emptyMap();
        }

        Map<String, ArgProvidedValue<?>> provided = new LinkedHashMap<>();
        getProcessor().onProvideArgs(finalNames, current, provided);

        provided.keySet().forEach(providedArg -> {
            Validate.isTrue(allProvidedArguments.contains(providedArg), "The provided argument '%s' is not specified", providedArg);
        });

        return provided;
    }

    @Override
    public void validate() {
        super.validate();

        Validate.isTrue(
                getMeta().getArgsMeta() != null && getMeta().getResultMeta() != null
                        || getMeta().getArgsMeta() == null && getMeta().getResultMeta() == null,
                "Both argument metadata and result metadata must be set or not");

        if (getMeta().getArgsMeta() != null) {
            boolean foundFirstOptionalArg = false;
            for (ArgMeta<?> argMeta : getMeta().getArgsMeta()) {
                validateArgMeta(argMeta);

                // Optional arguments may be specified only as last in the argument list.
                Validate.isTrue(!foundFirstOptionalArg || argMeta.isOptional(), "Only last arguments may be optional");
                if (argMeta.isOptional()) {
                    foundFirstOptionalArg = true;
                }
            }

            validateArgProvided(getMeta().getArgsMeta());
        }

        validateResultMeta(getMeta().getResultMeta());
    }

    private void validateArgMeta(ArgMeta<?> argMeta) {
        Validate.notNull(argMeta, "Null argument metadata in the '%s' action", getMeta().getName());

        String errorSource = String.format("argument '%s' in the action '%s'", argMeta.getName() != null ? argMeta.getName() : "unnamed",
                getMeta().getName());
        Validate.notNull(argMeta.getName(), "Null name of the %s", errorSource);
        Validate.notNull(argMeta.getType(), "Null type of the %s", errorSource);
        SpongeUtils.validateType(argMeta.getType(), errorSource);
    }

    private void validateArgProvided(List<ArgMeta<?>> argsMeta) {
        Set<String> prevArgNames = new HashSet<>();
        for (ArgMeta<?> argMeta : getMeta().getArgsMeta()) {
            if (argMeta.getProvided() != null) {
                argMeta.getProvided().getDepends().forEach(dependsOnArg -> {
                    Validate.isTrue(prevArgNames.contains(dependsOnArg),
                            "The argument '%s' depends on argument '%s' that is not defined before", argMeta.getName(), dependsOnArg);
                });
                Validate.isTrue(!argMeta.getProvided().isReadOnly() || argMeta.getType().isNullable(),
                        "The provided, read only argument '%s' type must be nullable", argMeta.getName());
            }

            prevArgNames.add(argMeta.getName());
        }
    }

    private static boolean isArgProvided(ArgMeta<?> argMeta) {
        return argMeta.getProvided() != null;
    }

    private void validateResultMeta(ResultMeta<?> resultMeta) {
        if (resultMeta != null) {
            String errorSource = String.format("result of the action '%s'", getMeta().getName());
            Validate.notNull(resultMeta.getType(), "Null type of the %s", errorSource);
            SpongeUtils.validateType(resultMeta.getType(), errorSource);
        }
    }
}
