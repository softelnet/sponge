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

import java.util.stream.Stream;

import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClass;

import org.openksavi.sponge.core.engine.BaseSpongeEngine;
import org.openksavi.sponge.core.kb.BaseKnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * Kotlin-specific engine operations. They are to be used in knowledge base files.
 */
public class KotlinKnowledgeBaseEngineOperations extends BaseKnowledgeBaseEngineOperations {

    public KotlinKnowledgeBaseEngineOperations(BaseSpongeEngine engine, KnowledgeBase knowledgeBase) {
        super(engine, knowledgeBase);
    }

    /**
     * Enables a processor.
     *
     * @param processorClass a processor class.
     */
    public void enable(KClass<?> processorClass) {
        engine.getProcessorManager().enable(getKnowledgeBase(), processorClass);
    }

    /**
     * Enables processors.
     *
     * @param processorClasses processor classes.
     */
    public void enableAll(KClass<?>... processorClasses) {
        Stream.of(processorClasses).forEachOrdered(processorClass -> enable(processorClass));
    }

    public void disable(KClass<?> processorClass) {
        engine.getProcessorManager().disable(getKnowledgeBase(), processorClass);
    }

    public void disableAll(KClass<?>... processorClasses) {
        Stream.of(processorClasses).forEachOrdered(processorClass -> disable(processorClass));
    }

    /**
     * Enables a filter.
     *
     * @param filterClass filter class.
     */
    public void enableFilter(KClass<?> filterClass) {
        engine.getProcessorManager().enableFilter(getKnowledgeBase(), filterClass);
    }

    /**
     * Enables filters.
     *
     * @param filterClasses filter classes.
     */
    public void enableFilters(KClass<?>... filterClasses) {
        for (KClass<?> filterClass : filterClasses) {
            enableFilter(filterClass);
        }
    }

    /**
     * Disables a filter.
     *
     * @param filterClass filter class.
     */
    public void disableFilter(KClass<?> filterClass) {
        engine.getProcessorManager().disableFilter(getKnowledgeBase(), filterClass);
    }

    /**
     * Enables a trigger.
     *
     * @param triggerClass trigger class.
     */
    public void enableTrigger(KClass<?> triggerClass) {
        engine.getProcessorManager().enableTrigger(getKnowledgeBase(), triggerClass);
    }

    public void enableTriggers(KClass<?>... triggerClasses) {
        for (KClass<?> triggerClass : triggerClasses) {
            enableTrigger(triggerClass);
        }
    }

    /**
     * Disables a trigger.
     *
     * @param triggerClass trigger class.
     */
    public void disableTrigger(KClass<?> triggerClass) {
        engine.getProcessorManager().disableTrigger(getKnowledgeBase(), triggerClass);
    }

    /**
     * Enables a rule.
     *
     * @param ruleClass rule class.
     */
    public void enableRule(KClass<?> ruleClass) {
        engine.getProcessorManager().enableRule(getKnowledgeBase(), ruleClass);
    }

    /**
     * Enables rules.
     *
     * @param ruleClasses rule classes.
     */
    public void enableRules(KClass<?>... ruleClasses) {
        for (KClass<?> ruleClass : ruleClasses) {
            enableRule(ruleClass);
        }
    }

    /**
     * Disables a rule.
     *
     * @param ruleClass rule class.
     */
    public void disableRule(KClass<?> ruleClass) {
        engine.getProcessorManager().disableRule(getKnowledgeBase(), ruleClass);
    }

    public void enableCorrelator(KClass<?> correlatorClass) {
        engine.getProcessorManager().enableCorrelator(getKnowledgeBase(), correlatorClass);
    }

    public void enableCorrelators(KClass<?>... correlatorClasses) {
        for (KClass<?> correlatorClass : correlatorClasses) {
            enableCorrelator(correlatorClass);
        }
    }

    public void disableCorrelator(KClass<?> correlatorClass) {
        engine.getProcessorManager().disableCorrelator(getKnowledgeBase(), correlatorClass);
    }

    /**
     * Enables an action.
     *
     * @param actionClass action class.
     */
    public void enableAction(KClass<?> actionClass) {
        engine.getProcessorManager().enableAction(getKnowledgeBase(), actionClass);
    }

    /**
     * Enables actions.
     *
     * @param actionClasses action classes.
     */
    public void enableActions(KClass<?>... actionClasses) {
        for (KClass<?> actionClass : actionClasses) {
            enableAction(actionClass);
        }
    }

    /**
     * Disables an action.
     *
     * @param actionClass action class.
     */
    public void disableAction(KClass<?> actionClass) {
        engine.getProcessorManager().disableAction(getKnowledgeBase(), actionClass);
    }

    public <T> T getVariable(KClass<T> cls, String name) {
        return engine.getSession().getVariable(JvmClassMappingKt.getJavaClass(cls), name);
    }

    public <T> T getVariable(KClass<T> cls, String name, T defaultValue) {
        return engine.getSession().getVariable(JvmClassMappingKt.getJavaClass(cls), name, defaultValue);
    }
}
