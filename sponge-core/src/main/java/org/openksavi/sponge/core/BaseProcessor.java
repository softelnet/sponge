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

package org.openksavi.sponge.core;

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
    public void init() {
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
    public final void setDisplayName(String displayName) {
        adapter.setDisplayName(displayName);
    }

    @Override
    public final String getDisplayName() {
        return adapter.getDisplayName();
    }

    @Override
    public final KnowledgeBase getKnowledgeBase() {
        return adapter.getKnowledgeBase();
    }

    public final KnowledgeBaseEngineOperations getEPS() {
        return getKnowledgeBase().getEngineOperations();
    }

    public final KnowledgeBaseEngineOperations getEps() {
        return getKnowledgeBase().getEngineOperations();
    }

    public final KnowledgeBase getKb() {
        return getKnowledgeBase();
    }

    public final Logger getLogger() {
        return ((BaseProcessorAdapter<?>) adapter).getLogger();
    }

    @Override
    public String toString() {
        return getName() != null ? getName() : super.toString();
    }
}