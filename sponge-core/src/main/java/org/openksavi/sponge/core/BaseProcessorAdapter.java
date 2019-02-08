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

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.ProcessorQualifiedVersion;
import org.openksavi.sponge.core.kb.BaseKnowledgeBase;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * Processor adapter.
 */
public abstract class BaseProcessorAdapter<T extends Processor<?>> implements ProcessorAdapter<T>, ProcessorAdapterImplementor, Cloneable {

    /** processor definition. */
    private BaseProcessorDefinition definition;

    /** Target processor. */
    private T processor;

    /**
     * Creates a new processor.
     *
     * @param definition a processor definition.
     */
    protected BaseProcessorAdapter(BaseProcessorDefinition definition) {
        this.definition = definition;
    }

    @Override
    public BaseProcessorMeta getMeta() {
        return (BaseProcessorMeta) definition.getMeta();
    }

    @Override
    public T getProcessor() {
        return processor;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void setProcessor(T processor) {
        this.processor = processor;

        BaseProcessor processorImpl = (BaseProcessor) processor;
        if (processorImpl.getAdapter() == null) {
            processorImpl.setAdapter(this);
        }
    }

    /**
     * Sets processor definition.
     *
     * @param definition processor definition.
     */
    @Override
    public void setDefinition(BaseProcessorDefinition definition) {
        if (this.definition != null) {
            return;
        }
        this.definition = definition;
    }

    /**
     * Returns processor definition.
     *
     * @return processor definition.
     */
    @Override
    public BaseProcessorDefinition getDefinition() {
        return definition;
    }

    @Override
    public KnowledgeBase getKnowledgeBase() {
        return definition.getKnowledgeBase();
    }

    @Override
    public void setKnowledgeBase(BaseKnowledgeBase knowledgeBase) {
        definition.setKnowledgeBase(knowledgeBase);
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
     * Clears this processor.
     */
    @Override
    public void clear() {
    }

    /**
     * Returns Java-defined flag.
     *
     * @return Java-defined flag.
     */
    @Override
    public boolean isJavaDefined() {
        return definition.isJavaDefined();
    }

    /**
     * Returns the logger.
     *
     * @return logger.
     */
    public Logger getLogger() {
        return LoggerFactory.getLogger(SpongeUtils.createLoggerName(getKnowledgeBase(), getMeta().getName()));
    }

    /**
     * Returns string representation of this processor.
     *
     * @return string representation of this processor.
     */
    @Override
    public String toString() {
        return getMeta().getName() != null ? getMeta().getName() : super.toString();
    }

    @Override
    public void validate() {
        Validate.isTrue(getMeta().getName() != null, "Invalid %s. Name must not be empty.", getType().getName());
        Validate.isTrue(
                getMeta().getCategory() == null || getKnowledgeBase().getEngineOperations().getCategory(getMeta().getCategory()) != null,
                "Category %s is not registered", getMeta().getCategory());
    }

    @Override
    public ProcessorQualifiedVersion getQualifiedVersion() {
        return new ProcessorQualifiedVersion(getKnowledgeBase().getVersion(), getMeta().getVersion());
    }
}
