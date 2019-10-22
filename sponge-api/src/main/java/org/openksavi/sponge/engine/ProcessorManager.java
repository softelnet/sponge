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

package org.openksavi.sponge.engine;

import java.util.List;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.ProcessorBuilder;
import org.openksavi.sponge.ProcessorDefinition;
import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * Processor manager.
 */
public interface ProcessorManager extends EngineModule {

    /**
     * Enables a processor.
     *
     * @param knowledgeBase knowledge base.
     * @param processorClass processor class.
     */
    void enable(KnowledgeBase knowledgeBase, Object processorClass);

    /**
     * Enables a processor.
     *
     * @param knowledgeBase knowledge base.
     * @param processorBuilder processor builder.
     */
    <T extends Processor<?>> void enable(KnowledgeBase knowledgeBase, ProcessorBuilder<T> processorBuilder);

    /**
     * Disables a processor.
     *
     * @param knowledgeBase knowledge base.
     * @param processorClass processor class.
     */
    void disable(KnowledgeBase knowledgeBase, Object processorClass);

    /**
     * Disables a processor.
     *
     * @param knowledgeBase knowledge base.
     * @param processorName processor name.
     */
    void disable(KnowledgeBase knowledgeBase, String processorName);

    /**
     * Enables an action.
     *
     * @param knowledgeBase knowledge base.
     * @param actionClass action class.
     */
    void enableAction(KnowledgeBase knowledgeBase, Object actionClass);

    /**
     * Enables a filter.
     *
     * @param knowledgeBase knowledge base.
     * @param filterClass filter class.
     */
    void enableFilter(KnowledgeBase knowledgeBase, Object filterClass);

    /**
     * Enables a trigger.
     *
     * @param knowledgeBase knowledge base.
     * @param triggerClass trigger class.
     */
    void enableTrigger(KnowledgeBase knowledgeBase, Object triggerClass);

    /**
     * Enables a rule.
     *
     * @param knowledgeBase knowledge base.
     * @param ruleClass rule class.
     */
    void enableRule(KnowledgeBase knowledgeBase, Object ruleClass);

    /**
     * Enables a correlator.
     *
     * @param knowledgeBase knowledge base.
     * @param correlatorClass correlator class.
     */
    void enableCorrelator(KnowledgeBase knowledgeBase, Object correlatorClass);

    /**
     * Disables the action.
     *
     * @param knowledgeBase knowledge base.
     * @param actionClass action class.
     */
    void disableAction(KnowledgeBase knowledgeBase, Object actionClass);

    /**
     * Disables the filter.
     *
     * @param knowledgeBase knowledge base.
     * @param filterClass filter class.
     */
    void disableFilter(KnowledgeBase knowledgeBase, Object filterClass);

    /**
     * Disables the trigger.
     *
     * @param knowledgeBase knowledge base.
     * @param triggerClass trigger class.
     */
    void disableTrigger(KnowledgeBase knowledgeBase, Object triggerClass);

    /**
     * Disables the rule.
     *
     * @param knowledgeBase knowledge base.
     * @param ruleClass rule class.
     */
    void disableRule(KnowledgeBase knowledgeBase, Object ruleClass);

    /**
     * Disables the correlator.
     *
     * @param knowledgeBase knowledge base.
     * @param correlatorClass correlator class.
     */
    void disableCorrelator(KnowledgeBase knowledgeBase, Object correlatorClass);

    /**
     * Creates a new uninitialized processor instance.
     *
     * @param definition processor definition.
     * @param cls processor class.
     * @return a new uninitialized processor instance.
     *
     * @param <T> processor.
     */
    @SuppressWarnings("rawtypes")
    <T extends Processor> T createProcessorInstance(ProcessorDefinition definition, Class<T> cls);

    /**
     * Returns all processor adapters.
     *
     * @return all processor adapters.
     */
    @SuppressWarnings("rawtypes")
    List<ProcessorAdapter> getAllProcessorAdapters();
}
