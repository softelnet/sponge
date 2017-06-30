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

package org.openksavi.sponge.core.engine.processing;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import org.openksavi.sponge.EventSetProcessorAdapter;
import org.openksavi.sponge.EventSetProcessorAdapterGroup;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.processing.EventSetProcessorMainProcessingUnitHandler;
import org.openksavi.sponge.event.Event;

public abstract class BaseEventSetProcessorMainProcessingUnitHandler<G extends EventSetProcessorAdapterGroup<T>,
        T extends EventSetProcessorAdapter<?>> extends BaseMainProcessingUnitHandler
        implements EventSetProcessorMainProcessingUnitHandler<G, T> {

    private ScheduledExecutorService executorService;

    private Map<T, EventSetProcessorDurationTask<T>> durationTasks = Collections.synchronizedMap(new WeakHashMap<>());

    private Lock lock = new ReentrantLock(true);

    protected BaseEventSetProcessorMainProcessingUnitHandler(ProcessorType type, BaseMainProcessingUnit processingUnit) {
        super(type, processingUnit);
    }

    private String getName() {
        return getType().getName() + "-duration";
    }

    @Override
    public void startup() {
        lock.lock();
        try {
            executorService =
                    Executors.newScheduledThreadPool(getProcessingUnit().getEngine().getConfigurationManager().getDurationThreadCount(),
                            new BasicThreadFactory.Builder().namingPattern(getName() + "-%d").build());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdown() {
        lock.lock();
        try {
            if (executorService != null) {
                durationTasks.values().stream().filter(task -> task.getFuture() != null).forEach(task -> task.getFuture().cancel(false));
                Utils.shutdownExecutorService(getProcessingUnit().getEngine(), getName(), executorService);

                executorService = null;
            }
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void processEvent(ProcessorAdapter<?> adapter, Event event) {
        ((EventSetProcessorAdapterGroup<EventSetProcessorAdapter>) adapter).processEvent(event);
    }

    @Override
    public void addDuration(T adapter) {
        lock.lock();
        try {
            if (adapter.hasDuration()) {
                EventSetProcessorDurationTask<T> task = new EventSetProcessorDurationTask<>(adapter);
                durationTasks.put(adapter, task);
                ScheduledFuture<?> future = executorService.schedule(task, adapter.getDuration().toMillis(), TimeUnit.MILLISECONDS);
                task.setFuture(future);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeDuration(T adapter) {
        lock.lock();
        try {
            if (adapter.hasDuration()) {
                EventSetProcessorDurationTask<T> task = durationTasks.get(adapter);
                if (task != null) {
                    task.getFuture().cancel(true);
                    durationTasks.remove(adapter);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeDurations(G adapterGroup) {
        lock.lock();
        try {
            adapterGroup.getEventSetProcessorAdapters().forEach(adapter -> removeDuration(adapter));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Executor getAsyncEventSetProcessorExecutor() {
        return getProcessingUnit().getAsyncEventSetProcessorExecutor();
    }

    public static class EventSetProcessorDurationTask<T extends EventSetProcessorAdapter<?>> implements Runnable {

        private T adapter;

        private ScheduledFuture<?> future;

        public EventSetProcessorDurationTask(T adapter) {
            this.adapter = adapter;
        }

        public T getAdapter() {
            return adapter;
        }

        public void setAdapter(T adapter) {
            this.adapter = adapter;
        }

        public ScheduledFuture<?> getFuture() {
            return future;
        }

        public void setFuture(ScheduledFuture<?> future) {
            this.future = future;
        }

        @Override
        public void run() {
            Engine engine = adapter.getKnowledgeBase().getEngineOperations().getEngine();

            try {
                engine.getEventSetProcessorDurationStrategy().durationOccurred(adapter);
            } catch (Throwable e) {
                engine.handleError(adapter, e);
            }
        }
    }
}
