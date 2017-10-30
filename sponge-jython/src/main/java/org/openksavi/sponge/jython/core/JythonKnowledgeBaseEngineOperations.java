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

package org.openksavi.sponge.jython.core;

import java.util.stream.Stream;

import org.python.core.PyType;

import org.openksavi.sponge.core.engine.BaseEngine;
import org.openksavi.sponge.core.kb.BaseKnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * Jython-specific engine operations. They are to be used in knowledge base files.
 */
public class JythonKnowledgeBaseEngineOperations extends BaseKnowledgeBaseEngineOperations {

    public JythonKnowledgeBaseEngineOperations(BaseEngine engine, KnowledgeBase knowledgeBase) {
        super(engine, knowledgeBase);
    }

    /**
     * Enables a processor.
     *
     * @param processorClass a processor class.
     */
    public void enable(PyType processorClass) {
        engine.getProcessorManager().enable(getKnowledgeBase(), processorClass);
    }

    /**
     * Enables processors.
     *
     * @param processorClasses processor classes.
     */
    public void enableAll(PyType... processorClasses) {
        Stream.of(processorClasses).forEachOrdered(processorClass -> enable(processorClass));
    }

    public void disable(PyType processorClass) {
        engine.getProcessorManager().disable(getKnowledgeBase(), processorClass);
    }

    public void disableAll(PyType... processorClasses) {
        Stream.of(processorClasses).forEachOrdered(processorClass -> disable(processorClass));
    }

    /**
     * Enables a filter.
     *
     * @param filterClass filter class.
     */
    public void enableFilter(PyType filterClass) {
        engine.getProcessorManager().enableFilter(getKnowledgeBase(), filterClass);
    }

    /**
     * Enables filters.
     *
     * @param filterClasses filter classes.
     */
    public void enableFilters(PyType... filterClasses) {
        for (PyType filterClass : filterClasses) {
            enableFilter(filterClass);
        }
    }

    /**
     * Disables a filter.
     *
     * @param filterClass filter class.
     */
    public void disableFilter(PyType filterClass) {
        engine.getProcessorManager().disableFilter(getKnowledgeBase(), filterClass);
    }

    /**
     * Enables a trigger.
     *
     * @param triggerClass trigger class.
     */
    public void enableTrigger(PyType triggerClass) {
        engine.getProcessorManager().enableTrigger(getKnowledgeBase(), triggerClass);
    }

    public void enableTriggers(PyType... triggerClasses) {
        for (PyType triggerClass : triggerClasses) {
            enableTrigger(triggerClass);
        }
    }

    /**
     * Disables a trigger.
     *
     * @param triggerClass trigger class.
     */
    public void disableTrigger(PyType triggerClass) {
        engine.getProcessorManager().disableTrigger(getKnowledgeBase(), triggerClass);
    }

    /**
     * Enables a rule.
     *
     * @param ruleClass rule class.
     */
    public void enableRule(PyType ruleClass) {
        engine.getProcessorManager().enableRule(getKnowledgeBase(), ruleClass);
    }

    /**
     * Enables rules.
     *
     * @param ruleClasses rule classes.
     */
    public void enableRules(PyType... ruleClasses) {
        for (PyType ruleClass : ruleClasses) {
            enableRule(ruleClass);
        }
    }

    /**
     * Disables a rule.
     *
     * @param ruleClass rule class.
     */
    public void disableRule(PyType ruleClass) {
        engine.getProcessorManager().disableRule(getKnowledgeBase(), ruleClass);
    }

    public void enableCorrelator(PyType correlatorClass) {
        engine.getProcessorManager().enableCorrelator(getKnowledgeBase(), correlatorClass);
    }

    public void enableCorrelators(PyType... correlatorClasses) {
        for (PyType correlatorClass : correlatorClasses) {
            enableCorrelator(correlatorClass);
        }
    }

    public void disableCorrelator(PyType correlatorClass) {
        engine.getProcessorManager().disableCorrelator(getKnowledgeBase(), correlatorClass);
    }

    /**
     * Enables an action.
     *
     * @param actionClass action class.
     */
    public void enableAction(PyType actionClass) {
        engine.getProcessorManager().enableAction(getKnowledgeBase(), actionClass);
    }

    /**
     * Enables actions.
     *
     * @param actionClasses action classes.
     */
    public void enableActions(PyType... actionClasses) {
        for (PyType actionClass : actionClasses) {
            enableAction(actionClass);
        }
    }

    /**
     * Disables an action.
     *
     * @param actionClass action class.
     */
    public void disableAction(PyType actionClass) {
        engine.getProcessorManager().disableAction(getKnowledgeBase(), actionClass);
    }
}
