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

package org.openksavi.sponge.core.kb;

import java.util.stream.Stream;

import org.slf4j.Logger;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.aggregator.Aggregator;
import org.openksavi.sponge.core.BaseEngineOperations;
import org.openksavi.sponge.core.engine.BaseEngine;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBaseInterpreter;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.trigger.Trigger;

/**
 * Knowledge base engine operations.
 */
public class BaseKnowledgeBaseEngineOperations extends BaseEngineOperations implements KnowledgeBaseEngineOperations {

    protected KnowledgeBase knowledgeBase;

    public BaseKnowledgeBaseEngineOperations(BaseEngine engine, KnowledgeBase knowledgeBase) {
        super(engine);
        this.knowledgeBase = knowledgeBase;
    }

    @Override
    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void enableJava(Class<? extends Processor> processorClass) {
        engine.getProcessorManager().enable(knowledgeBase, processorClass);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void enableJavaAll(Class<? extends Processor>... processorClasses) {
        Stream.of(processorClasses).forEach(processorClass -> enableJava(processorClass));
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void disableJava(Class<? extends Processor> processorClass) {
        engine.getProcessorManager().disable(knowledgeBase, processorClass);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void disableJavaAll(Class<? extends Processor>... processorClasses) {
        Stream.of(processorClasses).forEach(processorClass -> disableJava(processorClass));
    }

    /**
     * Enables Java filter.
     *
     * @param filterClass
     *            filter Java class.
     */
    @Override
    public void enableJavaFilter(Class<? extends Filter> filterClass) {
        engine.getProcessorManager().enableFilter(knowledgeBase, filterClass);
    }

    /**
     * Disables Java filter.
     *
     * @param filterClass
     *            filter Java class.
     */
    @Override
    public void disableJavaFilter(Class<? extends Filter> filterClass) {
        engine.getProcessorManager().disableFilter(knowledgeBase, filterClass);
    }

    /**
     * Enables Java trigger.
     *
     * @param triggerClass
     *            trigger Java class.
     */
    @Override
    public void enableJavaTrigger(Class<? extends Trigger> triggerClass) {
        engine.getProcessorManager().enableTrigger(knowledgeBase, triggerClass);
    }

    /**
     * Disables Java trigger.
     *
     * @param triggerClass
     *            trigger Java class.
     */
    @Override
    public void disableJavaTrigger(Class<? extends Trigger> triggerClass) {
        engine.getProcessorManager().disableTrigger(knowledgeBase, triggerClass);
    }

    /**
     * Enables Java rule.
     *
     * @param ruleClass
     *            rule Java class.
     */
    @Override
    public void enableJavaRule(Class<? extends Rule> ruleClass) {
        engine.getProcessorManager().enableRule(knowledgeBase, ruleClass);
    }

    /**
     * Disables Java rule.
     *
     * @param ruleClass
     *            rule Java class.
     */
    @Override
    public void disableJavaRule(Class<? extends Rule> ruleClass) {
        engine.getProcessorManager().disableRule(knowledgeBase, ruleClass);
    }

    @Override
    public void enableJavaAggregator(Class<? extends Aggregator> aggregatorClass) {
        engine.getProcessorManager().enableAggregator(knowledgeBase, aggregatorClass);
    }

    @Override
    public void disableJavaAggregator(Class<? extends Aggregator> aggregatorClass) {
        engine.getProcessorManager().disableAggregator(knowledgeBase, aggregatorClass);
    }

    /**
     * Enables Java action.
     *
     * @param actionClass
     *            action Java class.
     */
    @Override
    public void enableJavaAction(Class<? extends Action> actionClass) {
        engine.getProcessorManager().enableAction(knowledgeBase, actionClass);
    }

    /**
     * Disables Java action.
     *
     * @param actionClass
     *            action Java class.
     */
    @Override
    public void disableJavaAction(Class<? extends Action> actionClass) {
        engine.getProcessorManager().disableAction(knowledgeBase, actionClass);
    }

    public KnowledgeBaseInterpreter getInterpreter() {
        return knowledgeBase.getInterpreter();
    }

    public KnowledgeBase getKb() {
        return getKnowledgeBase();
    }

    public Logger getLogger() {
        return ((BaseKnowledgeBaseInterpreter) getKnowledgeBase().getInterpreter()).getLogger();
    }
}