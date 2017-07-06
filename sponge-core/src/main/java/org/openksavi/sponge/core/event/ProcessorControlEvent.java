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

package org.openksavi.sponge.core.event;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * A control event for a processor.
 */
@SuppressWarnings("rawtypes")
public abstract class ProcessorControlEvent extends BaseControlEvent {

    private static final long serialVersionUID = 6259820942438546724L;

    public static final String ATTR_PROCESSOR_ADAPTER = "processorAdapter";

    /** A processor adapter for which this control event is intended. */
    private transient ProcessorAdapter processorAdapter;

    public ProcessorControlEvent(ProcessorAdapter processorAdapter) {
        this(Utils.createControlEventName(ProcessorControlEvent.class), processorAdapter);
    }

    protected ProcessorControlEvent(String name, ProcessorAdapter processorAdapter) {
        super(name, EventClonePolicy.SHALLOW);

        this.processorAdapter = processorAdapter;
    }

    public ProcessorAdapter getProcessorAdapter() {
        return processorAdapter;
    }

    public void setProcessorAdapter(ProcessorAdapter processorAdapter) {
        this.processorAdapter = processorAdapter;
    }

    @Override
    public Object get(String name) {
        switch (name) {
        case ATTR_PROCESSOR_ADAPTER:
            return processorAdapter;
        default:
            throw new IllegalArgumentException("Unknown attribute " + name);
        }
    }

    @Override
    public ProcessorControlEvent set(String name, Object value) {
        switch (name) {
        case ATTR_PROCESSOR_ADAPTER:
            processorAdapter = (ProcessorAdapter) value;
            break;
        default:
            throw new IllegalArgumentException("Unknown attribute " + name);
        }

        return this;
    }

    @Override
    public boolean has(String name) {
        return ATTR_PROCESSOR_ADAPTER.equals(name);
    }

    @Override
    public Map<String, Object> getAll() {
        return ImmutableMap.of(ATTR_PROCESSOR_ADAPTER, processorAdapter);
    }
}
