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

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.EventSetProcessor;
import org.openksavi.sponge.EventSetProcessorBuilder;
import org.openksavi.sponge.ProcessorOnInitCallback;

/**
 * A base event set processor builder.
 */
public abstract class BaseEventSetProcessorBuilder<T extends EventSetProcessor<?>> extends BaseEventProcessorBuilder<T>
        implements EventSetProcessorBuilder<T> {

    protected BaseEventSetProcessorBuilder(BaseEventSetProcessorMeta meta, String name) {
        super(meta, name);
    }

    @Override
    protected BaseEventSetProcessorMeta getMeta() {
        return (BaseEventSetProcessorMeta) super.getMeta();
    }

    @Override
    public BaseEventSetProcessorBuilder<T> withName(String name) {
        return (BaseEventSetProcessorBuilder<T>) super.withName(name);
    }

    @Override
    public BaseEventSetProcessorBuilder<T> withLabel(String label) {
        return (BaseEventSetProcessorBuilder<T>) super.withLabel(label);
    }

    @Override
    public BaseEventSetProcessorBuilder<T> withDescription(String description) {
        return (BaseEventSetProcessorBuilder<T>) super.withDescription(description);
    }

    @Override
    public BaseEventSetProcessorBuilder<T> withVersion(Integer version) {
        return (BaseEventSetProcessorBuilder<T>) super.withVersion(version);
    }

    @Override
    public BaseEventSetProcessorBuilder<T> withFeatures(Map<String, Object> features) {
        return (BaseEventSetProcessorBuilder<T>) super.withFeatures(features);
    }

    @Override
    public BaseEventSetProcessorBuilder<T> withFeature(String name, Object value) {
        return (BaseEventSetProcessorBuilder<T>) super.withFeature(name, value);
    }

    @Override
    public BaseEventSetProcessorBuilder<T> withCategory(String category) {
        return (BaseEventSetProcessorBuilder<T>) super.withCategory(category);
    }

    @Override
    public BaseEventSetProcessorBuilder<T> withOnInit(ProcessorOnInitCallback<T> onInitCallback) {
        return (BaseEventSetProcessorBuilder<T>) super.withOnInit(onInitCallback);
    }

    @Override
    public BaseEventSetProcessorBuilder<T> withEvents(List<String> eventNames) {
        return (BaseEventSetProcessorBuilder<T>) super.withEvents(eventNames);
    }

    @Override
    public BaseEventSetProcessorBuilder<T> withEvent(String eventName) {
        return (BaseEventSetProcessorBuilder<T>) super.withEvent(eventName);
    }

    @Override
    public EventSetProcessorBuilder<T> withDuration(Duration duration) {
        getMeta().setDuration(duration);
        return this;
    }

    @Override
    public EventSetProcessorBuilder<T> withSynchronous(Boolean synchronous) {
        getMeta().setSynchronous(synchronous);
        return this;
    }
}
