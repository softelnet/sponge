/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.nashorn.core;

import java.util.stream.Stream;

import org.openksavi.sponge.core.engine.BaseEngine;
import org.openksavi.sponge.core.kb.BaseKnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * Jython-specific engine operations. They are to be used in knowledge base files.
 */
public class NashornKnowledgeBaseEngineOperations extends BaseKnowledgeBaseEngineOperations {

    public NashornKnowledgeBaseEngineOperations(BaseEngine engine, KnowledgeBase knowledgeBase) {
        super(engine, knowledgeBase);
    }

    /**
     * Enables a processor.
     *
     * @param processorClass
     *            processor class.
     */
    public void enable(Object processorClass) {
        engine.getProcessorManager().enable(getKnowledgeBase(), resolveProcessorName(processorClass));
    }

    /**
     * Enables processors.
     *
     * @param processorClasses
     *            processor classes.
     */
    public void enableAll(Object... processorClasses) {
        Stream.of(processorClasses).forEach(processorClass -> enable(processorClass));
    }

    public void disable(Object processorClass) {
        engine.getProcessorManager().disable(getKnowledgeBase(), resolveProcessorName(processorClass));
    }

    public void disableAll(Object... processorClasses) {
        Stream.of(processorClasses).forEach(processorClass -> disable(processorClass));
    }

    /**
     * Enables a filter.
     *
     * @param filterClass
     *            filter class.
     */
    public void enableFilter(Object filterClass) {
        engine.getProcessorManager().enableFilter(getKnowledgeBase(), resolveProcessorName(filterClass));
    }

    /**
     * Enables filters.
     *
     * @param filterClasses
     *            filter classes.
     */
    public void enableFilters(Object... filterClasses) {
        for (Object filterClass : filterClasses) {
            enableFilter(filterClass);
        }
    }

    /**
     * Disables a filter.
     *
     * @param filterClass
     *            filter class.
     */
    public void disableFilter(Object filterClass) {
        engine.getProcessorManager().disableFilter(getKnowledgeBase(), resolveProcessorName(filterClass));
    }

    /**
     * Enables a trigger.
     *
     * @param triggerClass
     *            trigger class.
     */
    public void enableTrigger(Object triggerClass) {
        engine.getProcessorManager().enableTrigger(getKnowledgeBase(), resolveProcessorName(triggerClass));
    }

    public void enableTriggers(Object... triggerClasses) {
        for (Object triggerClass : triggerClasses) {
            enableTrigger(triggerClass);
        }
    }

    /**
     * Disables a trigger.
     *
     * @param triggerClass
     *            trigger class.
     */
    public void disableTrigger(Object triggerClass) {
        engine.getProcessorManager().disableTrigger(getKnowledgeBase(), resolveProcessorName(triggerClass));
    }

    /**
     * Enables a rule.
     *
     * @param ruleClass
     *            rule class.
     */
    public void enableRule(Object ruleClass) {
        engine.getProcessorManager().enableRule(getKnowledgeBase(), resolveProcessorName(ruleClass));
    }

    /**
     * Enables rules.
     *
     * @param ruleClasses
     *            rule classes.
     */
    public void enableRules(Object... ruleClasses) {
        for (Object ruleClass : ruleClasses) {
            enableRule(ruleClass);
        }
    }

    /**
     * Disables a rule.
     *
     * @param ruleClass
     *            rule class.
     */
    public void disableRule(Object ruleClass) {
        engine.getProcessorManager().disableRule(getKnowledgeBase(), resolveProcessorName(ruleClass));
    }

    public void enableCorrelator(Object correlatorClass) {
        engine.getProcessorManager().enableCorrelator(getKnowledgeBase(), resolveProcessorName(correlatorClass));
    }

    public void enableCorrelators(Object... correlatorClasses) {
        for (Object correlatorClass : correlatorClasses) {
            enableCorrelator(correlatorClass);
        }
    }

    public void disableCorrelator(Object correlatorClass) {
        engine.getProcessorManager().disableCorrelator(getKnowledgeBase(), resolveProcessorName(correlatorClass));
    }

    /**
     * Enables an action.
     *
     * @param actionClass
     *            action class.
     */
    public void enableAction(Object actionClass) {
        engine.getProcessorManager().enableAction(getKnowledgeBase(), resolveProcessorName(actionClass));
    }

    /**
     * Enables actions.
     *
     * @param actionClasses
     *            action classes.
     */
    public void enableActions(Object... actionClasses) {
        for (Object actionClass : actionClasses) {
            enableAction(actionClass);
        }
    }

    /**
     * Disables an action.
     *
     * @param actionClass
     *            action class.
     */
    public void disableAction(Object actionClass) {
        engine.getProcessorManager().disableAction(getKnowledgeBase(), resolveProcessorName(actionClass));
    }

    @Override
    public NashornKnowledgeBaseInterpreter getInterpreter() {
        return (NashornKnowledgeBaseInterpreter) getKnowledgeBase().getInterpreter();
    }

    protected String resolveProcessorName(Object processorClass) {
        return processorClass instanceof String ? (String) processorClass : getInterpreter().resolveVariableName(processorClass);
    }
}
