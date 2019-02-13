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

package org.openksavi.sponge.core;

import java.util.List;

import org.openksavi.sponge.EventProcessor;
import org.openksavi.sponge.EventProcessorAdapter;
import org.openksavi.sponge.SpongeException;

public abstract class BaseEventProcessorAdapter<T extends EventProcessor<?>> extends BaseProcessorAdapter<T>
        implements EventProcessorAdapter<T> {

    protected BaseEventProcessorAdapter(BaseProcessorDefinition definition) {
        super(definition);
    }

    @Override
    public BaseEventProcessorDefinition getDefinition() {
        return (BaseEventProcessorDefinition) super.getDefinition();
    }

    @Override
    public BaseEventProcessorMeta getMeta() {
        return (BaseEventProcessorMeta) super.getMeta();
    }

    @Override
    public void validate() {
        super.validate();

        List<String> eventNames = getMeta().getEventNames();

        if (eventNames == null || eventNames.isEmpty()) {
            throw new SpongeException(
                    "Invalid " + getType().getName() + " " + getMeta().getName() + ". At least one event must be specified.");
        }

        // Validate the patterns for event names.
        for (String eventName : eventNames) {
            getKnowledgeBase().getEngineOperations().getEngine().getPatternMatcher().validatePattern(eventName);
        }
    }
}
