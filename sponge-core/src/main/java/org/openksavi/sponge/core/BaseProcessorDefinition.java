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

import org.openksavi.sponge.ProcessorDefinition;
import org.openksavi.sponge.ProcessorMeta;
import org.openksavi.sponge.core.kb.BaseKnowledgeBase;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * Processor definition.
 */
public abstract class BaseProcessorDefinition implements ProcessorDefinition {

    /** Is this processor defined in Java (not in the scripting knowledge base). */
    private boolean javaDefined = false;

    /** The processor class. */
    private Class<?> processorClass;

    /** Knowledge base reference. */
    private KnowledgeBase knowledgeBase;

    /** The processor metadata. */
    private ProcessorMeta meta;

    protected BaseProcessorDefinition(ProcessorMeta meta) {
        this.meta = meta;
    }

    @Override
    public ProcessorMeta getMeta() {
        return meta;
    }

    /**
     * Returns the string representation.
     *
     * @return the string representation.
     */
    @Override
    public String toString() {
        return getMeta().getLabel() != null ? getMeta().getLabel() : getMeta().getName();
    }

    /**
     * Clones this processor.
     *
     * @return cloned processor.
     */
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw SpongeUtils.wrapException(getMeta().getName(), e);
        }
    }

    /**
     * Sets Java-defined flag.
     *
     * @param javaDefined Java-defined flag.
     */
    public void setJavaDefined(boolean javaDefined) {
        this.javaDefined = javaDefined;
    }

    /**
     * Returns Java-defined flag.
     *
     * @return Java-defined flag.
     */
    public boolean isJavaDefined() {
        return javaDefined;
    }

    /**
     * Returns the optional processor class.
     *
     * @return the processor class or {@code null} if this processor has no corresponding Java class.
     */
    public Class<?> getProcessorClass() {
        return processorClass;
    }

    /**
     * Sets the processor class.
     *
     * @param processorClass the processor class.
     */
    public void setProcessorClass(Class<?> processorClass) {
        this.processorClass = processorClass;
    }

    @Override
    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public void setKnowledgeBase(BaseKnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }
}
