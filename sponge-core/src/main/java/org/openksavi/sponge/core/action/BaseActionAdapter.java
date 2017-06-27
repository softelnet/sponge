/*
 * Copyright 2016-2017 Softelnet.
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

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.core.BaseProcessorAdapter;
import org.openksavi.sponge.engine.ProcessorType;

/**
 * A base action adapter.
 */
public class BaseActionAdapter extends BaseProcessorAdapter<Action> implements ActionAdapter {

    /**
     * Creates a new action adapter.
     *
     * @param definition
     *            a base action definition.
     */
    public BaseActionAdapter(BaseActionDefinition definition) {
        super(definition);
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.ACTION;
    }

    @Override
    public void validate() {
        if (getName() == null) {
            throw new SpongeException("Invalid action. Name must not be empty.");
        }
    }
}