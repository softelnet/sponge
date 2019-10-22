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

import java.util.Map;

import org.slf4j.Logger;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.ProcessorAdapterFactory;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;

/**
 * Processor.
 */
public abstract class BaseProcessor<T extends ProcessorAdapter<?>> implements Processor<T>, ProcessorAdapterFactory<T> {

    private T adapter;

    @Override
    public void onInit() {
        //
    }

    @Override
    public final T getAdapter() {
        return adapter;
    }

    public final void setAdapter(T adapter) {
        this.adapter = adapter;
    }

    @Override
    public BaseProcessorMeta getMeta() {
        return (BaseProcessorMeta) adapter.getMeta();
    }

    @Override
    public KnowledgeBase getKnowledgeBase() {
        return adapter.getKnowledgeBase();
    }

    @Override
    public KnowledgeBaseEngineOperations getSponge() {
        return getKnowledgeBase().getEngineOperations();
    }

    public KnowledgeBase getKb() {
        return getKnowledgeBase();
    }

    @Override
    public final Logger getLogger() {
        return ((BaseProcessorAdapter<?>) adapter).getLogger();
    }

    public BaseProcessor<T> withName(String name) {
        getMeta().withName(name);
        return this;
    }

    public BaseProcessor<T> withLabel(String label) {
        getMeta().withLabel(label);
        return this;
    }

    public BaseProcessor<T> withDescription(String description) {
        getMeta().withDescription(description);
        return this;
    }

    public BaseProcessor<T> withVersion(Integer version) {
        getMeta().withVersion(version);
        return this;
    }

    public BaseProcessor<T> withFeatures(Map<String, Object> features) {
        getMeta().withFeatures(features);
        return this;
    }

    public BaseProcessor<T> withFeature(String name, Object value) {
        getMeta().withFeature(name, value);
        return this;
    }

    public BaseProcessor<T> withCategory(String category) {
        getMeta().withCategory(category);
        return this;
    }

    @Override
    public String toString() {
        return getMeta().getName() != null ? getMeta().getName() : super.toString();
    }
}
