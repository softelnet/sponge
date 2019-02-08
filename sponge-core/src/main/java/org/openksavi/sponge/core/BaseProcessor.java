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

    public final void setAdapter(T owner) {
        this.adapter = owner;
    }

    @Override
    public final String getName() {
        return adapter.getName();
    }

    @Override
    public final void setName(String name) {
        adapter.setName(name);
    }

    @Override
    public final void setLabel(String label) {
        adapter.setLabel(label);
    }

    @Override
    public final String getLabel() {
        return adapter.getLabel();
    }

    @Override
    public final void setDescription(String description) {
        adapter.setDescription(description);
    }

    @Override
    public final String getDescription() {
        return adapter.getDescription();
    }

    @Override
    public KnowledgeBase getKnowledgeBase() {
        return adapter.getKnowledgeBase();
    }

    @Override
    public void setVersion(Integer version) {
        adapter.setVersion(version);
    }

    @Override
    public Integer getVersion() {
        return adapter.getVersion();
    }

    @Override
    public Map<String, Object> getFeatures() {
        return adapter.getFeatures();
    }

    @Override
    public void setFeatures(Map<String, Object> features) {
        adapter.setFeatures(features);
    }

    @Override
    public String getCategory() {
        return adapter.getCategory();
    }

    @Override
    public void setCategory(String category) {
        adapter.setCategory(category);
    }

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
        setName(name);
        return this;
    }

    public BaseProcessor<T> withLabel(String label) {
        setLabel(label);
        return this;
    }

    public BaseProcessor<T> withDescription(String description) {
        setDescription(description);
        return this;
    }

    public BaseProcessor<T> withVersion(Integer version) {
        setVersion(version);
        return this;
    }

    public BaseProcessor<T> withFeatures(Map<String, Object> features) {
        setFeatures(features);
        return this;
    }

    public BaseProcessor<T> withFeature(String name, Object value) {
        getFeatures().put(name, value);
        return this;
    }

    public BaseProcessor<T> withCategory(String category) {
        setCategory(category);
        return this;
    }

    @Override
    public String toString() {
        return getName() != null ? getName() : super.toString();
    }
}
