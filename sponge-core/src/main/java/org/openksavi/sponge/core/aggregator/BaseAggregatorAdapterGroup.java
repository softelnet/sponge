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

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import org.openksavi.sponge.aggregator.AggregatorAdapter;
import org.openksavi.sponge.aggregator.AggregatorAdapterGroup;
import org.openksavi.sponge.core.BaseEventSetProcessorAdapterGroup;
import org.openksavi.sponge.core.BaseEventSetProcessorDefinition;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.processing.EventSetProcessorMainProcessingUnitHandler;
import org.openksavi.sponge.event.Event;

/**
 * Aggregator adapter group.
 */
public class BaseAggregatorAdapterGroup extends BaseEventSetProcessorAdapterGroup<AggregatorAdapter> implements AggregatorAdapterGroup {

    /**
     * Creates a new aggregator group.
     *
     * @param aggregatorDefinition
     *            aggregator definition.
     * @param handler
     *            handler.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public BaseAggregatorAdapterGroup(BaseEventSetProcessorDefinition aggregatorDefinition,
            EventSetProcessorMainProcessingUnitHandler<AggregatorAdapterGroup, AggregatorAdapter> handler) {
        super(aggregatorDefinition, (EventSetProcessorMainProcessingUnitHandler) handler);
    }

    @Override
    public BaseEventSetProcessorDefinition getDefinition() {
        return super.getDefinition();
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.AGGREGATOR_GROUP;
    }

    /**
     * Checks if there is a need for creating a new aggregator instance.
     *
     * @param event
     *            event.
     * @return {@code true} if there is a need for creating
     *         a new aggregator instance.
     */
    @Override
    public boolean needNewInstance(Event event) {
        return ArrayUtils.contains(getDefinition().getEventNames(), event.getName());
    }

    @Override
    protected BaseAggregatorAdapter createNewEventSetProcessorAdapter() {
        return new BaseAggregatorAdapter(getDefinition());
    }

    @Override
    public void validate() {
        //
    }

    @Override
    public List<AggregatorAdapter> getAggregators() {
        return getEventSetProcessorAdapters();
    }
}
