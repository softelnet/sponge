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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ProvideArgsContext;
import org.openksavi.sponge.core.BaseProcessorAdapter;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.util.SpongeApiUtils;

/**
 * A base action adapter.
 */
@SuppressWarnings("rawtypes")
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
    public Map<String, ProvidedValue<?>> provideArgs(List<String> names, Map<String, Object> current) {
        Validate.notNull(getMeta().getArgs(), "Arguments not defined");

        Set<String> finalNames = new LinkedHashSet<>();

        if (names != null) {
            for (String name : names) {
                Validate.isTrue(isArgProvided(getMeta().getArg(name)), "Argument '%s' is not defined as provided", name);
                finalNames.add(name);
            }
        } else {
            // Use all named arguments that are configured to be provided, including sub-arguments.
            SpongeApiUtils.traverseActionArguments(getMeta(), qType -> {
                if (qType.getType().getProvided() != null) {
                    finalNames.add(qType.getPath());
                }
            }, true);
        }

        if (current == null) {
            current = Collections.emptyMap();
        }

        Map<String, ProvidedValue<?>> provided = new LinkedHashMap<>();
        getProcessor().onProvideArgs(new ProvideArgsContext(finalNames, current, provided));

        provided.keySet().forEach(providedArg -> {
            Validate.isTrue(getMeta().getArg(providedArg).getProvided() != null,
                    "The argument '%s' that has been provided is not configured as provided", providedArg);
        });

        return provided;
    }

    @Override
    public void validate() {
        super.validate();

        Validate.isTrue(
                getMeta().getArgs() != null && getMeta().getResult() != null
                        || getMeta().getArgs() == null && getMeta().getResult() == null,
                "Both argument types and result type must be set or not");

        if (getMeta().getArgs() != null) {
            boolean foundFirstOptionalArg = false;
            for (DataType argType : getMeta().getArgs()) {
                validateArg(argType);

                // Optional arguments may be specified only as last in the argument list.
                Validate.isTrue(!foundFirstOptionalArg || argType.isOptional(), "Only last arguments may be optional");
                if (argType.isOptional()) {
                    foundFirstOptionalArg = true;
                }
            }

            validateArgProvided();
        }

        validateResult(getMeta().getResult());
    }

    private void validateArg(DataType argType) {
        Validate.notNull(argType, "Null argument type in the '%s' action", getMeta().getName());
        Validate.notNull(argType.getName(), "Argument has no name");
        String errorSource = String.format("argument '%s' in the action '%s'", argType.getName(), getMeta().getName());
        SpongeUtils.validateType(argType, errorSource);
    }

    private void validateArgProvided() {
        Map<String, DataType> fullArgTypesMap = SpongeApiUtils.createNamedActionArgTypesMap(getMeta());

        SpongeApiUtils.traverseActionArguments(getMeta(), qType -> {
            DataType type = qType.getType();

            if (qType.getPath() != null) { // If it's named.
                if (type.getProvided() != null) {
                    type.getProvided().getDependencies().forEach(dependency -> {
                        Validate.isTrue(fullArgTypesMap.containsKey(dependency),
                                "The argument '%s' depends on an argument '%s' that is not defined or is not allowed", qType.getPath(),
                                dependency);
                    });
                    Validate.isTrue(!type.getProvided().isReadOnly() || type.isNullable(),
                            "The provided, read only argument '%s' must be nullable", qType.getPath());
                }
            } else { // If it's unnamed.
                Validate.isTrue(type.getProvided() == null, "The %s argument is set as provided but doesn't have a complete name path",
                        type.getKind().name());
            }
        }, false);
    }

    private static boolean isArgProvided(DataType argType) {
        return argType.getProvided() != null;
    }

    private void validateResult(DataType resultType) {
        if (resultType != null) {
            String errorSource = String.format("result of the action '%s'", getMeta().getName());
            SpongeUtils.validateType(resultType, errorSource);
        }
    }
}
