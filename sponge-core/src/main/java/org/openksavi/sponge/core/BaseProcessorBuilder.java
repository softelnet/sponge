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

import java.util.Map;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.ProcessorBuilder;
import org.openksavi.sponge.ProcessorOnInitCallback;

/**
 * A base processor builder.
 */
public abstract class BaseProcessorBuilder<T extends Processor<?>> implements ProcessorBuilder<T> {

    private BaseProcessorMeta meta;

    private ProcessorOnInitCallback<T> onInitCallback;

    protected BaseProcessorBuilder(BaseProcessorMeta meta, String name) {
        this.meta = meta;
        this.meta.setName(name);
    }

    public final String getName() {
        return getMeta().getName();
    }

    protected BaseProcessorMeta getMeta() {
        return meta;
    }

    @Override
    public BaseProcessorBuilder<T> withName(String name) {
        getMeta().withName(name);
        return this;
    }

    @Override
    public BaseProcessorBuilder<T> withLabel(String label) {
        getMeta().withLabel(label);
        return this;
    }

    @Override
    public BaseProcessorBuilder<T> withDescription(String description) {
        getMeta().withDescription(description);
        return this;
    }

    @Override
    public BaseProcessorBuilder<T> withVersion(Integer version) {
        getMeta().withVersion(version);
        return this;
    }

    @Override
    public BaseProcessorBuilder<T> withFeatures(Map<String, Object> features) {
        getMeta().withFeatures(features);
        return this;
    }

    @Override
    public BaseProcessorBuilder<T> withFeature(String name, Object value) {
        getMeta().withFeature(name, value);
        return this;
    }

    @Override
    public BaseProcessorBuilder<T> withCategory(String category) {
        getMeta().withCategory(category);
        return this;
    }

    @Override
    public BaseProcessorBuilder<T> withOnInit(ProcessorOnInitCallback<T> onInitCallback) {
        this.onInitCallback = onInitCallback;

        return this;
    }

    public ProcessorOnInitCallback<T> getOnInitCallback() {
        return onInitCallback;
    }

    public void setOnInitCallback(ProcessorOnInitCallback<T> onInitCallback) {
        this.onInitCallback = onInitCallback;
    }
}
