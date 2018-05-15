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

package org.openksavi.sponge.java.core;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.BaseSpongeEngine;
import org.openksavi.sponge.core.engine.GenericProcessorInstanceHolder;
import org.openksavi.sponge.core.kb.BaseNonScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.ProcessorInstanceHolder;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * Java based knowledge base interpreter.
 */
public class JavaKnowledgeBaseInterpreter extends BaseNonScriptKnowledgeBaseInterpreter {

    public JavaKnowledgeBaseInterpreter(SpongeEngine engine, KnowledgeBase knowledgeBase) {
        super(new JavaKnowledgeBaseEngineOperations((BaseSpongeEngine) engine, knowledgeBase), JavaConstants.TYPE);
    }

    @Override
    public Object invokeMethod(Object target, String name, Object... args) {
        return SpongeUtils.invokeMethod(target, name, args);
    }

    @Override
    protected <T> T doCreateInstance(String className, Class<T> javaClass) {
        return SpongeUtils.createInstance(className, javaClass);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ProcessorInstanceHolder createProcessorInstanceByProcessorClass(KnowledgeBase knowledgeBase, Object processorClass,
            Class<?> javaClass) {
        if (processorClass instanceof Class) {
            // Java-based processor.
            Class<?> destJavaClass = (Class<?>) processorClass;
            if (!javaClass.isAssignableFrom(destJavaClass)) {
                throw new SpongeException(
                        "Unsupported processor specification: " + destJavaClass.getName() + " can't be used as " + javaClass.getName());
            }

            try {
                return new GenericProcessorInstanceHolder((Processor) destJavaClass.newInstance(),
                        JavaUtils.createProcessorName(destJavaClass), true);
            } catch (Throwable e) {
                throw SpongeUtils.wrapException(destJavaClass.getName(), knowledgeBase.getInterpreter(), e);
            }
        } else {
            return null;
        }
    }
}
