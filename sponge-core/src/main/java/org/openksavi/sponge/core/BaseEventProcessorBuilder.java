/*
 * Copyright 2016-2019 The Sponge authors.
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.EventProcessor;
import org.openksavi.sponge.EventProcessorBuilder;
import org.openksavi.sponge.ProcessorOnInitCallback;

/**
 * A base event processor builder.
 */
public abstract class BaseEventProcessorBuilder<T extends EventProcessor<?>> extends BaseProcessorBuilder<T>
        implements EventProcessorBuilder<T> {

    protected BaseEventProcessorBuilder(BaseEventProcessorMeta meta, String name) {
        super(meta, name);
    }

    @Override
    protected BaseEventProcessorMeta getMeta() {
        return (BaseEventProcessorMeta) super.getMeta();
    }

    @Override
    public BaseEventProcessorBuilder<T> withName(String name) {
        return (BaseEventProcessorBuilder<T>) super.withName(name);
    }

    @Override
    public BaseEventProcessorBuilder<T> withLabel(String label) {
        return (BaseEventProcessorBuilder<T>) super.withLabel(label);
    }

    @Override
    public BaseEventProcessorBuilder<T> withDescription(String description) {
        return (BaseEventProcessorBuilder<T>) super.withDescription(description);
    }

    @Override
    public BaseEventProcessorBuilder<T> withVersion(Integer version) {
        return (BaseEventProcessorBuilder<T>) super.withVersion(version);
    }

    @Override
    public BaseEventProcessorBuilder<T> withFeatures(Map<String, Object> features) {
        return (BaseEventProcessorBuilder<T>) super.withFeatures(features);
    }

    @Override
    public BaseEventProcessorBuilder<T> withFeature(String name, Object value) {
        return (BaseEventProcessorBuilder<T>) super.withFeature(name, value);
    }

    @Override
    public BaseEventProcessorBuilder<T> withCategory(String category) {
        return (BaseEventProcessorBuilder<T>) super.withCategory(category);
    }

    @Override
    public BaseEventProcessorBuilder<T> withOnInit(ProcessorOnInitCallback<T> onInitCallback) {
        return (BaseEventProcessorBuilder<T>) super.withOnInit(onInitCallback);
    }

    @Override
    public BaseEventProcessorBuilder<T> withEvents(List<String> eventNames) {
        getMeta().setEventNames(eventNames);
        return this;
    }

    @Override
    public BaseEventProcessorBuilder<T> withEvent(String eventName) {
        return withEvents(Arrays.asList(eventName));
    }
}
