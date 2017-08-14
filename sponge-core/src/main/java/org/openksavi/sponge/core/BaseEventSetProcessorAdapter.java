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

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.EventSetProcessor;
import org.openksavi.sponge.EventSetProcessorAdapter;
import org.openksavi.sponge.EventSetProcessorAdapterGroup;
import org.openksavi.sponge.EventSetProcessorState;
import org.openksavi.sponge.event.Event;

/**
 * Event set processor adapter.
 */
public abstract class BaseEventSetProcessorAdapter<T extends EventSetProcessor<?>> extends BaseEventProcessorAdapter<T>
        implements EventSetProcessorAdapter<T> {

    private static final Logger logger = LoggerFactory.getLogger(BaseEventSetProcessorAdapter.class);

    /** Event set processor state. */
    private AtomicReference<EventSetProcessorState> state = new AtomicReference<>(EventSetProcessorState.CREATED);

    /** Indicates that duration trigger for this event set processor occurred. */
    protected AtomicBoolean durationTriggered = new AtomicBoolean(false);

    /** Event set processor group. */
    protected EventSetProcessorAdapterGroup<EventSetProcessorAdapter<?>> group;

    /** Synchronization lock. */
    protected Lock lock = new ReentrantLock(true);

    protected BaseEventSetProcessorAdapter(BaseEventSetProcessorDefinition definition) {
        super(definition);
    }

    @Override
    public BaseEventSetProcessorDefinition getDefinition() {
        return (BaseEventSetProcessorDefinition) super.getDefinition();
    }

    /**
     * Sets event set processor state.
     *
     * @param state event set processor state.
     */
    @Override
    public void setState(EventSetProcessorState state) {
        this.state.set(state);
    }

    /**
     * Returns event set processor state.
     *
     * @return event set processor state.
     */
    @Override
    public EventSetProcessorState getState() {
        return state.get();
    }

    /**
     * Informs whether this event set processor is running.
     *
     * @return whether this event set processor is running.
     */
    @Override
    public boolean isRunning() {
        return state.get() == EventSetProcessorState.RUNNING;
    }

    @Override
    public EventSetProcessorAdapterGroup<EventSetProcessorAdapter<?>> getGroup() {
        return group;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setGroup(EventSetProcessorAdapterGroup<? extends EventSetProcessorAdapter<?>> group) {
        this.group = (EventSetProcessorAdapterGroup<EventSetProcessorAdapter<?>>) group;
    }

    @Override
    public void finish() {
        lock.lock();
        try {
            if (getState() != EventSetProcessorState.FINISHED) {
                setState(EventSetProcessorState.FINISHED);
                clear();
                group.removeEventSetProcessorAdapter(this);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final void processEvent(Event event) {
        onEvent(event);
    }

    protected abstract void onEvent(Event event);

    @Override
    public final void durationOccurred() {
        logger.debug("{} - Duration occurred", getName());
        lock.lock();
        try {
            if (isRunning()) {
                setDurationTriggered(true);
                onDuration();
                finish();
            }
        } finally {
            lock.unlock();
        }
    }

    protected abstract void onDuration();

    @Override
    public boolean hasDuration() {
        return getDefinition().hasDuration();
    }

    @Override
    public void setDuration(Duration duration) {
        getDefinition().setDuration(duration);
    }

    @Override
    public Duration getDuration() {
        return getDefinition().getDuration();
    }

    @Override
    public Boolean isSynchronous() {
        return getDefinition().isSynchronous();
    }

    @Override
    public void setSynchronous(Boolean synchronous) {
        getDefinition().setSynchronous(synchronous);
    }

    public boolean isDurationTriggered() {
        return durationTriggered.get();
    }

    public void setDurationTriggered(boolean durationTriggered) {
        this.durationTriggered.set(durationTriggered);
    }
}
