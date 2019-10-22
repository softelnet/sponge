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

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.ProcessorDefinition;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.action.ActionDefinition;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.InitialProcessorInstance;
import org.openksavi.sponge.engine.ProcessorInstanceHolder;
import org.openksavi.sponge.engine.ProcessorProvider;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseInterpreter;

/**
 * A processor provider based on a processor class.
 */
public class ClassProcessorProvider<T extends Processor<?>> implements ProcessorProvider<T> {

    private Object processorClass;

    /** The processor class. */
    private Class<?> processorJavaClass;

    /** Is this processor defined in Java (not in the scripting knowledge base). */
    private boolean javaDefined = false;

    public ClassProcessorProvider(Object processorClass) {
        this.processorClass = processorClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InitialProcessorInstance<T> createInitialProcessorInstance(KnowledgeBase knowledgeBase, Class<T> cls) {
        ProcessorInstanceHolder holder =
                knowledgeBase.getInterpreter().createProcessorInstanceByProcessorClass(knowledgeBase, processorClass, cls);
        if (holder == null) {
            // Try to create an instance using the default (Java-based) knowledge base interpreter.
            holder = knowledgeBase.getEngineOperations().getEngine().getKnowledgeBaseManager().getDefaultKnowledgeBase().getInterpreter()
                    .createProcessorInstanceByProcessorClass(knowledgeBase, processorClass, cls);
        }

        if (holder == null) {
            throw new SpongeException("Unsupported processor class: " + processorClass);
        }

        javaDefined = holder.isJavaDefined();
        processorJavaClass = holder.getProcessor().getClass();

        return new InitialProcessorInstance<>((T) holder.getProcessor(), holder.getName());
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public T createAdditionalProcessorInstance(ProcessorDefinition definition, Class<T> cls) {
        if (javaDefined) {
            try {
                if (processorJavaClass == null) {
                    throw new SpongeException("No corresponding Java class for processor: " + definition.getMeta().getName());
                }

                return (T) processorJavaClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw SpongeUtils.wrapException(definition.getMeta().getName(), e);
            }
        } else {
            return definition.getKnowledgeBase().getInterpreter().createProcessorInstance(definition.getMeta().getName(), cls);
        }
    }

    @Override
    public Object invokeActionOnCall(Action action, List<Object> args) {
        KnowledgeBaseInterpreter interpreter =
                javaDefined ? action.getAdapter().getKnowledgeBase().getEngineOperations().getEngine().getKnowledgeBaseManager()
                        .getDefaultKnowledgeBase().getInterpreter() : action.getAdapter().getKnowledgeBase().getInterpreter();

        // Important cast to an array of objects.
        return interpreter.invokeMethod(action, ActionDefinition.ON_CALL_METHOD_NAME,
                (Object[]) (args != null ? args.toArray(new Object[args.size()]) : new Object[0]));
    }
}
