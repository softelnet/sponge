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
import org.openksavi.sponge.core.engine.BaseSpongeEngine;
import org.openksavi.sponge.core.kb.BaseKnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * Java-specific engine operations. They are to be used in Java knowledge bases.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class JavaKnowledgeBaseEngineOperations extends BaseKnowledgeBaseEngineOperations {

    public JavaKnowledgeBaseEngineOperations(BaseSpongeEngine engine, KnowledgeBase knowledgeBase) {
        super(engine, knowledgeBase);
    }

    /**
     * Enables Java processor.
     *
     * @param processorClass Java processor class.
     */
    public void enable(Class<? extends Processor> processorClass) {
        super.enableJava(processorClass);
    }

    /**
     * Enables Java processors.
     *
     * @param processorClasses Java processor classes.
     */
    public void enableAll(Class<? extends Processor>... processorClasses) {
        super.enableJavaAll(processorClasses);
    }

    /**
     * Disables Java processor.
     *
     * @param processorClass Java processor class.
     */
    public void disable(Class<? extends Processor> processorClass) {
        super.disableJava(processorClass);

    }

    /**
     * Disables Java processors.
     *
     * @param processorClasses Java processor classes.
     */
    public void disableAll(Class<? extends Processor>... processorClasses) {
        super.disableJavaAll(processorClasses);
    }
}
