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

package org.openksavi.sponge.core.engine;

import java.util.List;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.ProcessorBuilder;
import org.openksavi.sponge.ProcessorDefinition;
import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.core.action.DefaultBuilderAction;
import org.openksavi.sponge.engine.InitialProcessorInstance;
import org.openksavi.sponge.engine.ProcessorProvider;
import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * A processor provider based on a processor builder.
 */
public class BuilderProcessorProvider<T extends Processor<?>> implements ProcessorProvider<T> {

    private ProcessorBuilder<T> builder;

    public BuilderProcessorProvider(ProcessorBuilder<T> builder) {
        this.builder = builder;
    }

    public ProcessorBuilder<T> getBuilder() {
        return builder;
    }

    public void setBuilder(ProcessorBuilder<T> builder) {
        this.builder = builder;
    }

    @Override
    public InitialProcessorInstance<T> createInitialProcessorInstance(KnowledgeBase knowledgeBase, Class<T> cls) {
        return new InitialProcessorInstance<>(builder.build(), builder.getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    public T createAdditionalProcessorInstance(ProcessorDefinition definition, Class<T> cls) {
        Processor<?> processor = builder.build();

        Validate.isInstanceOf(cls, processor, "The builder builds %s which is not an instance of expected %s", processor.getClass(), cls);

        return (T) processor;
    }

    @Override
    public Object invokeActionOnCall(Action action, List<Object> args) {
        Validate.isInstanceOf(DefaultBuilderAction.class, action);

        // Important cast to an array of objects.
        return ((DefaultBuilderAction) action).onCall((Object[]) (args != null ? args.toArray(new Object[args.size()]) : new Object[0]));
    }
}
