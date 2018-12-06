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

import java.util.List;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ArgMeta;
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
    public List<ArgMeta<?>> getArgsMeta() {
        return getDefinition().getArgsMeta();
    }

    @Override
    public void setArgsMeta(List<ArgMeta<?>> argsMeta) {
        getDefinition().setArgsMeta(argsMeta);
    }

    @Override
    public ResultMeta<?> getResultMeta() {
        return getDefinition().getResultMeta();
    }

    @Override
    public void setResultMeta(ResultMeta<?> resultMeta) {
        getDefinition().setResultMeta(resultMeta);
    }

    @Override
    public void validate() {
        super.validate();

        Validate.isTrue(getArgsMeta() != null && getResultMeta() != null || getArgsMeta() == null && getResultMeta() == null,
                "Both argument metadata and result metadata must be set or not");

        if (getArgsMeta() != null) {
            boolean foundFirstOptionalArg = false;
            for (ArgMeta<?> argMeta : getArgsMeta()) {
                validateArgMeta(argMeta);

                // Optional arguments may be specified only as last in the argument list.
                Validate.isTrue(!foundFirstOptionalArg || argMeta.isOptional(), "Only last arguments may be optional");
                if (argMeta.isOptional()) {
                    foundFirstOptionalArg = true;
                }
            }
        }

        if (getResultMeta() != null) {
            validateResultMeta(getResultMeta());
        }
    }

    private void validateArgMeta(ArgMeta<?> argMeta) {
        Validate.notNull(argMeta, "Null argument metadata in the %s action", getName());

        String errorSource =
                String.format("argument %s in the action %s", argMeta.getName() != null ? argMeta.getName() : "unnamed", getName());
        Validate.notNull(argMeta.getName(), "Null name of the %s", errorSource);
        Validate.notNull(argMeta.getType(), "Null type of the %s", errorSource);
        SpongeUtils.validateType(argMeta.getType(), errorSource);
    }

    private void validateResultMeta(ResultMeta<?> resultMeta) {
        String errorSource = String.format("result of the action %s", getName());
        Validate.notNull(resultMeta.getType(), "Null type of the %s", errorSource);
        SpongeUtils.validateType(resultMeta.getType(), errorSource);
    }
}
