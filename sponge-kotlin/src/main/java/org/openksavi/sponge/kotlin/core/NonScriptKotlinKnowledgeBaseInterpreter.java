/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.kotlin.core;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.BaseSpongeEngine;
import org.openksavi.sponge.core.engine.DefaultProcessorInstanceHolder;
import org.openksavi.sponge.core.kb.BaseNonScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.ProcessorInstanceHolder;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * Non script Kotlin based knowledge base interpreter.
 */
public class NonScriptKotlinKnowledgeBaseInterpreter extends BaseNonScriptKnowledgeBaseInterpreter {

    public NonScriptKotlinKnowledgeBaseInterpreter(SpongeEngine engine, KnowledgeBase knowledgeBase) {
        super(new KotlinKnowledgeBaseEngineOperations((BaseSpongeEngine) engine, knowledgeBase), KotlinConstants.TYPE_NON_SCRIPT);
    }

    @Override
    public Object invokeMethod(Object target, String name, Object... args) {
        return SpongeUtils.invokeMethod(target, name, (Object[]) args);
    }

    @Override
    protected <T> T doCreateInstance(String className, Class<T> javaClass) {
        return SpongeUtils.createInstance(className, javaClass);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ProcessorInstanceHolder createProcessorInstanceByProcessorClass(KnowledgeBase knowledgeBase, Object processorClass,
            Class<?> javaClass) {
        if (processorClass instanceof KClass) {
            // Kotlin-based processor.
            KClass kclass = (KClass) processorClass;
            Class<?> destJavaClass = JvmClassMappingKt.getJavaClass(kclass);
            if (!javaClass.isAssignableFrom(destJavaClass)) {
                throw new SpongeException(
                        "Unsupported processor specification: " + destJavaClass.getName() + " can't be used as " + javaClass.getName());
            }

            try {
                return new DefaultProcessorInstanceHolder((Processor) destJavaClass.newInstance(), KotlinUtils.createProcessorName(kclass),
                        true);
            } catch (Throwable e) {
                throw SpongeUtils.wrapException(destJavaClass.getName(), knowledgeBase.getInterpreter(), e);
            }
        }

        return null;
    }
}
