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

package org.openksavi.sponge.kb;

import org.openksavi.sponge.EngineOperations;
import org.openksavi.sponge.Processor;
import org.openksavi.sponge.ProcessorBuilder;
import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.trigger.Trigger;

/**
 * An engine operations available in the knowledge base.
 */
public interface KnowledgeBaseEngineOperations extends EngineOperations {

    /**
     * Returns the knowledge base associated with this processor.
     *
     * @return the knowledge base.
     */
    KnowledgeBase getKnowledgeBase();

    /**
     * Enables a processor.
     *
     * @param processorBuilder the processor builder.
     */
    <T extends Processor<?>> void enable(ProcessorBuilder<T> processorBuilder);

    /**
     * Enables the Java-based processor.
     *
     * @param processorClass Java-based processor class.
     */
    @SuppressWarnings("rawtypes")
    void enableJava(Class<? extends Processor> processorClass);

    /**
     * Enables Java-based processors.
     *
     * @param processorClasses Java-based processor classes.
     */
    void enableJavaAll(Class<?>... processorClasses);

    /**
     * Enables Java-based processors by scanning the given packages in search of all non abstract processor classes.
     *
     * @param params the parameters compatible with the org.reflections.Reflections(Object...) constructor.
     */
    void enableJavaByScan(final Object... params);

    /**
     * Disables the Java-based processor.
     *
     * @param processorClass Java-based processor class.
     */
    @SuppressWarnings("rawtypes")
    void disableJava(Class<? extends Processor> processorClass);

    /**
     * Disables Java-based processors.
     *
     * @param processorClasses Java-based processor classes.
     */
    void disableJavaAll(Class<?>... processorClasses);

    /**
     * Disables Java-based processors by scanning the given packages in search of all non abstract processor classes.
     *
     * @param params the parameters compatible with the org.reflections.Reflections(Object...) constructor.
     */
    void disableJavaByScan(final Object... params);

    /**
     * Enables the Java-based filter.
     *
     * @param filterClass filter Java class.
     */
    void enableJavaFilter(Class<? extends Filter> filterClass);

    /**
     * Disables the Java-based filter.
     *
     * @param filterClass filter Java class.
     */
    void disableJavaFilter(Class<? extends Filter> filterClass);

    /**
     * Enables the Java-based trigger.
     *
     * @param triggerClass trigger Java class.
     */
    void enableJavaTrigger(Class<? extends Trigger> triggerClass);

    /**
     * Disables the Java-based trigger.
     *
     * @param triggerClass trigger Java class.
     */
    void disableJavaTrigger(Class<? extends Trigger> triggerClass);

    /**
     * Enables the Java-based rule.
     *
     * @param ruleClass rule Java class.
     */
    void enableJavaRule(Class<? extends Rule> ruleClass);

    /**
     * Disables the Java-based rule.
     *
     * @param ruleClass rule Java class.
     */
    void disableJavaRule(Class<? extends Rule> ruleClass);

    /**
     * Enables the Java-based correlator.
     *
     * @param correlatorClass correlator Java class.
     */
    void enableJavaCorrelator(Class<? extends Correlator> correlatorClass);

    /**
     * Disables the Java-based correlator.
     *
     * @param correlatorClass correlator Java class.
     */
    void disableJavaCorrelator(Class<? extends Correlator> correlatorClass);

    /**
     * Enables the Java-based action.
     *
     * @param actionClass action Java class.
     */
    void enableJavaAction(Class<? extends Action> actionClass);

    /**
     * Disables the Java-based action.
     *
     * @param actionClass action Java class.
     */
    void disableJavaAction(Class<? extends Action> actionClass);

    /**
     * Disables a processor.
     *
     * @param processorName the processor name.
     */
    void disable(String processorName);
}
