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

package org.openksavi.sponge.core.correlator;

import java.util.List;

import org.openksavi.sponge.core.BaseEventSetProcessorAdapterGroup;
import org.openksavi.sponge.correlator.CorrelatorAdapter;
import org.openksavi.sponge.correlator.CorrelatorAdapterGroup;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.processing.EventSetProcessorMainProcessingUnitHandler;
import org.openksavi.sponge.event.Event;

/**
 * Correlator adapter group.
 */
public class BaseCorrelatorAdapterGroup extends BaseEventSetProcessorAdapterGroup<CorrelatorAdapter> implements CorrelatorAdapterGroup {

    /**
     * Creates a new correlator group.
     *
     * @param correlatorDefinition correlator definition.
     * @param handler handler.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public BaseCorrelatorAdapterGroup(BaseCorrelatorAdapter templateAdapter,
            EventSetProcessorMainProcessingUnitHandler<CorrelatorAdapterGroup, CorrelatorAdapter> handler) {
        super(templateAdapter, (EventSetProcessorMainProcessingUnitHandler) handler);
    }

    @Override
    public BaseCorrelatorDefinition getDefinition() {
        return (BaseCorrelatorDefinition) super.getDefinition();
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.CORRELATOR_GROUP;
    }

    /**
     * Checks if there is a need for creating a new correlator instance.
     *
     * @param event event.
     * @return {@code true} if there is a need for creating a new correlator instance.
     */
    @Override
    public boolean needNewInstance(Event event) {
        return getKnowledgeBase().getEngineOperations().getEngine().getPatternMatcher().matchesAny(getDefinition().getEventNames(),
                event.getName())
                && (getDefinition().isMaxInstancesUnbound() || getCorrelators().size() < getDefinition().getMaxInstances());
    }

    @Override
    protected BaseCorrelatorAdapter createNewEventSetProcessorAdapter() {
        return new BaseCorrelatorAdapter(getDefinition());
    }

    @Override
    public List<CorrelatorAdapter> getCorrelators() {
        return getEventSetProcessorAdapters();
    }

    @Override
    public BaseCorrelatorAdapter getTemplateAdapter() {
        return (BaseCorrelatorAdapter) super.getTemplateAdapter();
    }
}
