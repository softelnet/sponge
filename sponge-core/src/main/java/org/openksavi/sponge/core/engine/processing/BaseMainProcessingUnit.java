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

package org.openksavi.sponge.core.engine.processing;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import org.openksavi.sponge.EventProcessorAdapter;
import org.openksavi.sponge.EventSetProcessorAdapterGroup;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.correlator.CorrelatorAdapter;
import org.openksavi.sponge.correlator.CorrelatorAdapterGroup;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.engine.processing.EventSetProcessorMainProcessingUnitHandler;
import org.openksavi.sponge.engine.processing.MainProcessingUnit;
import org.openksavi.sponge.engine.processing.MainProcessingUnitHandler;
import org.openksavi.sponge.rule.RuleAdapter;
import org.openksavi.sponge.rule.RuleAdapterGroup;
import org.openksavi.sponge.trigger.TriggerAdapter;

/**
 * Main processing unit that handles rules and correlators.
 */
public abstract class BaseMainProcessingUnit extends BaseProcessingUnit<EventProcessorAdapter<?>> implements MainProcessingUnit {

    /** Map of handlers. */
    protected Map<ProcessorType, MainProcessingUnitHandler> handlers = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * Creates a new main processing unit.
     *
     * @param name name.
     * @param engine the engine.
     * @param inQueue input queue.
     * @param outQueue output queue.
     */
    protected BaseMainProcessingUnit(String name, SpongeEngine engine, EventQueue inQueue, EventQueue outQueue) {
        super(name, engine, inQueue, outQueue);

        createHandlers();
    }

    protected void createHandlers() {
        handlers.put(ProcessorType.TRIGGER, new TriggerMainProcessingUnitHandler(this));
        handlers.put(ProcessorType.RULE_GROUP,
                new SyncAsyncEventSetProcessorMainProcessingUnitHandler<RuleAdapterGroup, RuleAdapter>(ProcessorType.RULE_GROUP, this));
        handlers.put(ProcessorType.CORRELATOR_GROUP,
                new SyncAsyncEventSetProcessorMainProcessingUnitHandler<CorrelatorAdapterGroup, CorrelatorAdapter>(
                        ProcessorType.CORRELATOR_GROUP, this));
    }

    /**
     * Starts up handers.
     */
    protected void startupHandlers() {
        handlers.values().forEach(handler -> handler.startup());
    }

    protected void shutdownHandlers() {
        handlers.values().forEach(handler -> handler.shutdown());
    }

    @SuppressWarnings("unchecked")
    protected <A extends ProcessorAdapter<?>> List<A> getProcessorAdapterList(ProcessorType type) {
        return getRegisteredProcessorAdapterMap().values().stream().filter(adapter -> adapter.getType() == type).map(adapter -> (A) adapter)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsProcessor(String name, ProcessorType type) {
        return getRegisteredProcessorAdapterMap().values().stream()
                .filter(adapter -> adapter.getType() == type && StringUtils.equals(adapter.getName(), name)).findAny().isPresent();
    }

    @Override
    public List<TriggerAdapter> getTriggerAdapters() {
        return getProcessorAdapterList(ProcessorType.TRIGGER);
    }

    @Override
    public List<RuleAdapterGroup> getRuleAdapterGroups() {
        return getProcessorAdapterList(ProcessorType.RULE_GROUP);
    }

    @Override
    public List<CorrelatorAdapterGroup> getCorrelatorAdapterGroups() {
        return getProcessorAdapterList(ProcessorType.CORRELATOR_GROUP);
    }

    @Override
    public MainProcessingUnitHandler getHandler(ProcessorType type) {
        MainProcessingUnitHandler handler = handlers.get(type);
        if (handler == null) {
            throw new SpongeException("Handler for type " + type + " not registered");
        }

        return handler;
    }

    public Map<ProcessorType, MainProcessingUnitHandler> getHandlers() {
        return handlers;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void processorChanged(EventProcessorAdapter oldProcessor, EventProcessorAdapter newProcessor) {
        // If event set processor has been redefined then remove all duration timers for the old event set processor instances.
        if (oldProcessor instanceof EventSetProcessorAdapterGroup) {
            ((EventSetProcessorMainProcessingUnitHandler) getHandler(oldProcessor.getType()))
                    .removeDurations((EventSetProcessorAdapterGroup) oldProcessor);
        }
    }
}
