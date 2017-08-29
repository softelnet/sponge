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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Iterables;

import org.openksavi.sponge.EventSetProcessorAdapter;
import org.openksavi.sponge.EventSetProcessorAdapterGroup;
import org.openksavi.sponge.EventSetProcessorDefinition;
import org.openksavi.sponge.EventSetProcessorState;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.event.Event;

/**
 * Synchronous event set processors are processed synchronously (in one thread) in the boundaries of one event set processor adapter group.
 * Asynchronous event set processors are processed asynchronously (in many threads) in the boundaries of one event set processor adapter
 * group.
 */
public class SyncAsyncEventSetProcessorMainProcessingUnitHandler<G extends EventSetProcessorAdapterGroup<T>,
        T extends EventSetProcessorAdapter<?>> extends BaseEventSetProcessorMainProcessingUnitHandler<G, T> {

    private int asyncEventSetProcessorProcessingPartitionSize;

    private int asyncEventSetProcessorProcessingThreshold;

    public SyncAsyncEventSetProcessorMainProcessingUnitHandler(ProcessorType type, BaseMainProcessingUnit processingUnit) {
        super(type, processingUnit);

        asyncEventSetProcessorProcessingPartitionSize =
                getProcessingUnit().getEngine().getDefaultParameters().getAsyncEventSetProcessorProcessingPartitionSize();

        asyncEventSetProcessorProcessingThreshold =
                getProcessingUnit().getEngine().getDefaultParameters().getAsyncEventSetProcessorProcessingThreshold();
    }

    @Override
    public void processEventForEventSetProcessorAdapters(EventSetProcessorDefinition processorDefinition, List<T> eventSetProcessorAdapters,
            Event event) {

        if (isSynchronous(processorDefinition) || eventSetProcessorAdapters.size() <= asyncEventSetProcessorProcessingThreshold) {
            processSynchronously(eventSetProcessorAdapters, event);
        } else {
            processAsynchronously(eventSetProcessorAdapters, event);
        }
    }

    private boolean isSynchronous(EventSetProcessorDefinition processorDefinition) {
        Boolean isSynchronous = processorDefinition.isSynchronous();
        if (isSynchronous != null) {
            return isSynchronous;
        }

        return getProcessingUnit().getEngine().getConfigurationManager().getEventSetProcessorDefaultSynchronous();
    }

    protected void processAdapter(T adapter, Event event) {
        if (adapter.getState() == EventSetProcessorState.FINISHED) {
            return;
        }

        try {
            adapter.processEvent(event);
        } catch (Throwable e) {
            getProcessingUnit().getEngine().handleError(adapter, e);
        }
    }

    protected void processSynchronously(List<T> adapters, Event event) {
        adapters.forEach(adapter -> processAdapter(adapter, event));
    }

    protected void processAsynchronously(List<T> adapters, Event event) {
        // It must be Iterables.partition here, not Lists.partition because of concurrent access to the adapters list.
        for (List<T> partition : Iterables.partition(adapters, asyncEventSetProcessorProcessingPartitionSize)) {
            CompletableFuture.allOf(partition.stream().map(adapter -> CompletableFuture.runAsync(() -> {
                try {
                    processAdapter(adapter, event);
                } catch (Throwable e) {
                    getProcessingUnit().getEngine().handleError(
                            SyncAsyncEventSetProcessorMainProcessingUnitHandler.class.getSimpleName() + ".processAsynchronously", e);
                }
            }, getAsyncEventSetProcessorThreadPool().getExecutor())).toArray(CompletableFuture[]::new)).join();
        }
    }
}
