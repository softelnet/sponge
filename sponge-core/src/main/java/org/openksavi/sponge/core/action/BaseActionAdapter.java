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

import java.util.Arrays;

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
    public ArgMeta<?>[] getArgsMeta() {
        return getDefinition().getArgsMeta();
    }

    @Override
    public void setArgsMeta(ArgMeta<?>... argsMeta) {
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

        SpongeUtils.isTrue(getArgsMeta() != null && getResultMeta() != null || getArgsMeta() == null && getResultMeta() == null,
                "Both argument metadata and result metadata must be set or not");

        if (getArgsMeta() != null) {
            Arrays.stream(getArgsMeta()).filter(argMeta -> argMeta != null && argMeta.getType() != null)
                    .forEach(argMeta -> SpongeUtils.validateType(argMeta.getType(), String.format("argument %s in the action %s",
                            argMeta.getName() != null ? argMeta.getName() : "unnamed", getName())));
        }

        if (getResultMeta() != null) {
            SpongeUtils.validateType(getResultMeta().getType(), String.format("result of the action %s", getName()));
        }
    }
}
