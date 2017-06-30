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

package org.openksavi.sponge.jruby.core;

import java.util.stream.Stream;

import org.jruby.RubyClass;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.core.engine.BaseEngine;
import org.openksavi.sponge.core.kb.BaseKnowledgeBaseEngineOperations;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.trigger.Trigger;

/**
 * JRuby-specific engine operations. They are to be used in knowledge base files.
 */
public class JRubyKnowledgeBaseEngineOperations extends BaseKnowledgeBaseEngineOperations {

    public JRubyKnowledgeBaseEngineOperations(BaseEngine engine, KnowledgeBase knowledgeBase) {
        super(engine, knowledgeBase);
    }

    /**
     * Enables a processor.
     *
     * @param processorClass a processor class.
     */
    public void enable(RubyClass processorClass) {
        engine.getProcessorManager().enable(getKnowledgeBase(), processorClass);
    }

    /**
     * Enables processors.
     *
     * @param processorClasses processor classes.
     */
    public void enableAll(RubyClass... processorClasses) {
        Stream.of(processorClasses).forEach(processorClass -> enable(processorClass));
    }

    public void disable(RubyClass processorClass) {
        engine.getProcessorManager().disable(getKnowledgeBase(), processorClass);
    }

    public void disableAll(RubyClass... processorClasses) {
        Stream.of(processorClasses).forEach(processorClass -> disable(processorClass));
    }

    /**
     * Enables a filter.
     *
     * @param filterClass filter KB class.
     */
    public void enableFilter(RubyClass filterClass) {
        engine.getProcessorManager().enableFilter(getKnowledgeBase(), filterClass);
    }

    /**
     * Enables filters.
     *
     * @param filterClasses filter classes.
     */
    public void enableFilters(RubyClass... filterClasses) {
        for (RubyClass filterClass : filterClasses) {
            enableFilter(filterClass);
        }
    }

    /**
     * Disables a filter.
     *
     * @param filterClass filter class.
     */
    public void disableFilter(RubyClass filterClass) {
        engine.getProcessorManager().disableFilter(getKnowledgeBase(), filterClass);
    }

    /**
     * Enables a trigger.
     *
     * @param triggerClass trigger class.
     */
    public void enableTrigger(RubyClass triggerClass) {
        engine.getProcessorManager().enableTrigger(getKnowledgeBase(), triggerClass);
    }

    public void enableTriggers(RubyClass... triggerClasses) {
        for (RubyClass triggerClass : triggerClasses) {
            enableTrigger(triggerClass);
        }
    }

    /**
     * Disables a trigger.
     *
     * @param triggerClass trigger class.
     */
    public void disableTrigger(RubyClass triggerClass) {
        engine.getProcessorManager().disableTrigger(getKnowledgeBase(), triggerClass);
    }

    /**
     * Enables a rule.
     *
     * @param ruleClass rule class.
     */
    public void enableRule(RubyClass ruleClass) {
        engine.getProcessorManager().enableRule(getKnowledgeBase(), ruleClass);
    }

    /**
     * Enables rules.
     *
     * @param ruleClasses rule classes.
     */
    public void enableRules(RubyClass... ruleClasses) {
        for (RubyClass ruleClass : ruleClasses) {
            enableRule(ruleClass);
        }
    }

    /**
     * Disables a rule.
     *
     * @param ruleClass rule class.
     */
    public void disableRule(RubyClass ruleClass) {
        engine.getProcessorManager().disableRule(getKnowledgeBase(), ruleClass);
    }

    public void enableCorrelator(RubyClass correlatorClass) {
        engine.getProcessorManager().enableCorrelator(getKnowledgeBase(), correlatorClass);
    }

    public void enableCorrelators(RubyClass... correlatorClasses) {
        for (RubyClass correlatorClass : correlatorClasses) {
            enableCorrelator(correlatorClass);
        }
    }

    public void disableCorrelator(RubyClass correlatorClass) {
        engine.getProcessorManager().disableCorrelator(getKnowledgeBase(), correlatorClass);
    }

    /**
     * Enables an action.
     *
     * @param actionClass action class.
     */
    public void enableAction(RubyClass actionClass) {
        engine.getProcessorManager().enableAction(getKnowledgeBase(), actionClass);
    }

    /**
     * Enables actions.
     *
     * @param actionClasses action classes.
     */
    public void enableActions(RubyClass... actionClasses) {
        for (RubyClass actionClass : actionClasses) {
            enableAction(actionClass);
        }
    }

    /**
     * Disables an action.
     *
     * @param actionClass action class.
     */
    public void disableAction(RubyClass actionClass) {
        engine.getProcessorManager().disableAction(getKnowledgeBase(), actionClass);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void enableJava(RubyClass processorClass) {
        super.enableJava((Class<? extends Processor>) processorClass.toJava(Class.class));
    }

    public void enableJavaAll(RubyClass... processorClasses) {
        Stream.of(processorClasses).forEach(processorClass -> enableJava(processorClass));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void disableJava(RubyClass processorClass) {
        super.disableJava((Class<? extends Processor>) processorClass.toJava(Class.class));
    }

    public void disableJavaAll(RubyClass... processorClasses) {
        Stream.of(processorClasses).forEach(processorClass -> disableJava(processorClass));
    }

    /**
     * Enables Java filter.
     *
     * @param filterClass filter Java class.
     */
    @SuppressWarnings("unchecked")
    public void enableJavaFilter(RubyClass filterClass) {
        super.enableJavaFilter((Class<? extends Filter>) filterClass.toJava(Class.class));
    }

    /**
     * Disables Java filter.
     *
     * @param filterClass filter Java class.
     */
    @SuppressWarnings("unchecked")
    public void disableJavaFilter(RubyClass filterClass) {
        disableJavaFilter((Class<? extends Filter>) filterClass.toJava(Class.class));
    }

    /**
     * Enables Java trigger.
     *
     * @param triggerClass trigger Java class.
     */
    @SuppressWarnings("unchecked")
    public void enableJavaTrigger(RubyClass triggerClass) {
        enableJavaTrigger((Class<? extends Trigger>) triggerClass.toJava(Class.class));
    }

    /**
     * Disables Java trigger.
     *
     * @param triggerClass trigger Java class.
     */
    @SuppressWarnings("unchecked")
    public void disableJavaTrigger(RubyClass triggerClass) {
        disableJavaTrigger((Class<? extends Trigger>) triggerClass.toJava(Class.class));
    }

    /**
     * Enables Java rule.
     *
     * @param ruleClass rule Java class.
     */
    @SuppressWarnings("unchecked")
    public void enableJavaRule(RubyClass ruleClass) {
        enableJavaRule((Class<? extends Rule>) ruleClass.toJava(Class.class));
    }

    /**
     * Disables Java rule.
     *
     * @param ruleClass rule Java class.
     */
    @SuppressWarnings("unchecked")
    public void disableJavaRule(RubyClass ruleClass) {
        disableJavaRule((Class<? extends Rule>) ruleClass.toJava(Class.class));
    }

    /**
     * Enables Java correlator.
     *
     * @param correlatorClass correlator Java class.
     */
    @SuppressWarnings("unchecked")
    public void enableJavaCorrelator(RubyClass correlatorClass) {
        enableJavaCorrelator((Class<? extends Correlator>) correlatorClass.toJava(Class.class));
    }

    /**
     * Disables Java correlator.
     *
     * @param correlatorClass correlator Java class.
     */
    @SuppressWarnings("unchecked")
    public void disableJavaCorrelator(RubyClass correlatorClass) {
        disableJavaCorrelator((Class<? extends Correlator>) correlatorClass.toJava(Class.class));
    }

    /**
     * Enables Java action.
     *
     * @param actionClass action Java class.
     */
    @SuppressWarnings("unchecked")
    public void enableJavaAction(RubyClass actionClass) {
        enableJavaAction((Class<? extends Action>) actionClass.toJava(Class.class));
    }

    /**
     * Disables Java action.
     *
     * @param actionClass action Java class.
     */
    @SuppressWarnings("unchecked")
    public void disableJavaAction(RubyClass actionClass) {
        disableJavaAction((Class<? extends Action>) actionClass.toJava(Class.class));
    }
}
