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

package org.openksavi.sponge.core.aggregator;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.aggregator.Aggregator;
import org.openksavi.sponge.aggregator.AggregatorAdapter;
import org.openksavi.sponge.core.BaseEventSetProcessorAdapter;
import org.openksavi.sponge.core.BaseEventSetProcessorDefinition;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.event.Event;

/**
 * Abstract aggregator.
 */
public class BaseAggregatorAdapter extends BaseEventSetProcessorAdapter<Aggregator> implements AggregatorAdapter {

    public BaseAggregatorAdapter(BaseEventSetProcessorDefinition definition) {
        super(definition);
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.AGGREGATOR;
    }

    @Override
    protected void onDuration() {
        getProcessor().onDuration();
    }

    @Override
    public boolean acceptsAsFirst(Event event) {
        return getProcessor().acceptsAsFirst(event);
    }

    @Override
    protected void onEvent(Event event) {
        getProcessor().onEvent(event);
    }

    @Override
    public void validate() {
        if (getName() == null) {
            throw new SpongeException("Invalid aggregator. Name must not be empty.");
        }
        if (getEventNames() == null || getEventNames().length < 1) {
            throw new SpongeException("Invalid aggregator " + getName() + ". At least one event must be specified.");
        }
    }
}
