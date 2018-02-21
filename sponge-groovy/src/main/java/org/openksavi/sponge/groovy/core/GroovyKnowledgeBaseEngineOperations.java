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

package org.openksavi.sponge.groovy.core;

import java.util.stream.Stream;

import org.openksavi.sponge.core.engine.BaseSpongeEngine;
import org.openksavi.sponge.core.kb.BaseKnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * Groovy-specific engine operations. They are to be used in knowledge base files.
 */
@SuppressWarnings("rawtypes")
public class GroovyKnowledgeBaseEngineOperations extends BaseKnowledgeBaseEngineOperations {

    public GroovyKnowledgeBaseEngineOperations(BaseSpongeEngine engine, KnowledgeBase knowledgeBase) {
        super(engine, knowledgeBase);
    }

    public void reloadClass(Class clazz) {
        ((GroovyKnowledgeBaseInterpreter) getKnowledgeBase().getInterpreter()).reloadClass(clazz);
    }

    protected GroovyClassWrapper wrapClass(Class clazz) {
        return new GroovyClassWrapper(clazz);
    }

    /**
     * Enables the processor.
     *
     * @param processorClass the processor class.
     */
    public void enable(Class processorClass) {
        engine.getProcessorManager().enable(getKnowledgeBase(), wrapClass(processorClass));
    }

    /**
     * Enables processors.
     *
     * @param processorClasses processor classes.
     */
    public void enableAll(Class... processorClasses) {
        Stream.of(processorClasses).forEachOrdered(processorClass -> enable(processorClass));
    }

    public void disable(Class processorClass) {
        engine.getProcessorManager().disable(getKnowledgeBase(), wrapClass(processorClass));
    }

    public void disableAll(Class... processorClasses) {
        Stream.of(processorClasses).forEachOrdered(processorClass -> disable(processorClass));
    }

    /**
     * Enables a filter.
     *
     * @param filterClass filter class.
     */
    public void enableFilter(Class filterClass) {
        engine.getProcessorManager().enableFilter(getKnowledgeBase(), wrapClass(filterClass));
    }

    /**
     * Enables filters.
     *
     * @param filterClasses filter classes.
     */
    public void enableFilters(Class... filterClasses) {
        for (Class filterClass : filterClasses) {
            enableFilter(filterClass);
        }
    }

    /**
     * Disables a filter.
     *
     * @param filterClass filter class.
     */
    public void disableFilter(Class filterClass) {
        engine.getProcessorManager().disableFilter(getKnowledgeBase(), wrapClass(filterClass));
    }

    /**
     * Enables a trigger.
     *
     * @param triggerClass trigger class.
     */
    public void enableTrigger(Class triggerClass) {
        engine.getProcessorManager().enableTrigger(getKnowledgeBase(), wrapClass(triggerClass));
    }

    public void enableTriggers(Class... triggerClasses) {
        for (Class triggerClass : triggerClasses) {
            enableTrigger(triggerClass);
        }
    }

    /**
     * Disables a trigger.
     *
     * @param triggerClass trigger class.
     */
    public void disableTrigger(Class triggerClass) {
        engine.getProcessorManager().disableTrigger(getKnowledgeBase(), wrapClass(triggerClass));
    }

    /**
     * Enables a rule.
     *
     * @param ruleClass rule class.
     */
    public void enableRule(Class ruleClass) {
        engine.getProcessorManager().enableRule(getKnowledgeBase(), wrapClass(ruleClass));
    }

    /**
     * Enables rules.
     *
     * @param ruleClasses rule KB classes.
     */
    public void enableRules(Class... ruleClasses) {
        for (Class ruleClass : ruleClasses) {
            enableRule(ruleClass);
        }
    }

    /**
     * Disables a rule.
     *
     * @param ruleClass rule class.
     */
    public void disableRule(Class ruleClass) {
        engine.getProcessorManager().disableRule(getKnowledgeBase(), wrapClass(ruleClass));
    }

    public void enableCorrelator(Class correlatorClass) {
        engine.getProcessorManager().enableCorrelator(getKnowledgeBase(), wrapClass(correlatorClass));
    }

    public void enableCorrelators(Class... correlatorClasses) {
        for (Class correlatorClass : correlatorClasses) {
            enableCorrelator(correlatorClass);
        }
    }

    public void disableCorrelator(Class correlatorClass) {
        engine.getProcessorManager().disableCorrelator(getKnowledgeBase(), wrapClass(correlatorClass));
    }

    /**
     * Enables an action.
     *
     * @param actionClass action class.
     */
    public void enableAction(Class actionClass) {
        engine.getProcessorManager().enableAction(getKnowledgeBase(), wrapClass(actionClass));
    }

    /**
     * Enables actions.
     *
     * @param actionClasses action classes.
     */
    public void enableActions(Class... actionClasses) {
        for (Class actionClass : actionClasses) {
            enableAction(actionClass);
        }
    }

    /**
     * Disables an action.
     *
     * @param actionClass action class.
     */
    public void disableAction(Class actionClass) {
        engine.getProcessorManager().disableAction(getKnowledgeBase(), wrapClass(actionClass));
    }
}
