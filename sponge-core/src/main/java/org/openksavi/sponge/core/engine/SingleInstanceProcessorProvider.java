/*
 * Copyright 2016-2021 The Sponge authors.
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
import org.openksavi.sponge.ProcessorDefinition;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.action.ActionDefinition;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.InitialProcessorInstance;
import org.openksavi.sponge.engine.ProcessorInstanceHolder;
import org.openksavi.sponge.engine.ProcessorProvider;
import org.openksavi.sponge.java.core.JavaUtils;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseInterpreter;

/**
 * A processor provider based on a single instance of a processor.
 */
public class SingleInstanceProcessorProvider<T extends Processor<?>> implements ProcessorProvider<T> {

    private Processor<?> processorInstance;

    public SingleInstanceProcessorProvider(Processor<?> processorInstance) {
        this.processorInstance = processorInstance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InitialProcessorInstance<T> createInitialProcessorInstance(KnowledgeBase knowledgeBase, Class<T> cls) {
        Validate.notNull(processorInstance, "Null processor instance");
        Validate.isTrue(SpongeUtils.isSingleton(processorInstance), "The processor %s is not a singleton", processorInstance.getClass());

        ProcessorInstanceHolder holder =
                new DefaultProcessorInstanceHolder(processorInstance, JavaUtils.createProcessorName(processorInstance.getClass()), true);

        return new InitialProcessorInstance<>((T) holder.getProcessor(), holder.getName());
    }

    @Override
    public T createAdditionalProcessorInstance(ProcessorDefinition definition, Class<T> cls) {
        throw new SpongeException("Single instance processors can't have additional instances");
    }

    @Override
    public Object invokeActionOnCall(Action action, List<Object> args) {
        // Using a default Java based knowledge base interpreter.
        KnowledgeBaseInterpreter interpreter = action.getAdapter().getKnowledgeBase().getEngineOperations().getEngine()
                .getKnowledgeBaseManager().getDefaultKnowledgeBase().getInterpreter();

        // Important cast to an array of objects.
        return interpreter.invokeMethod(action, ActionDefinition.ON_CALL_METHOD_NAME,
                (Object[]) (args != null ? args.toArray(new Object[args.size()]) : new Object[0]));
    }
}
