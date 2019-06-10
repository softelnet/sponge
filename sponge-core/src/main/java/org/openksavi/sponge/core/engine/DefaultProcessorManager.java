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

package org.openksavi.sponge.core.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.ProcessorAdapterFactory;
import org.openksavi.sponge.ProcessorDefinition;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.core.BaseProcessorAdapter;
import org.openksavi.sponge.core.BaseProcessorDefinition;
import org.openksavi.sponge.core.correlator.BaseCorrelatorAdapter;
import org.openksavi.sponge.core.correlator.BaseCorrelatorAdapterGroup;
import org.openksavi.sponge.core.kb.BaseKnowledgeBase;
import org.openksavi.sponge.core.rule.BaseRuleAdapter;
import org.openksavi.sponge.core.rule.BaseRuleAdapterGroup;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.correlator.CorrelatorAdapter;
import org.openksavi.sponge.correlator.CorrelatorAdapterGroup;
import org.openksavi.sponge.engine.ProcessorInstanceHolder;
import org.openksavi.sponge.engine.ProcessorManager;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.processing.EventSetProcessorMainProcessingUnitHandler;
import org.openksavi.sponge.filter.FilterAdapter;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.rule.RuleAdapter;
import org.openksavi.sponge.rule.RuleAdapterGroup;
import org.openksavi.sponge.trigger.TriggerAdapter;

/**
 * Processor manager.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultProcessorManager extends BaseEngineModule implements ProcessorManager {

    //@formatter:off
    /** Processor registration handlers. */
    protected Map<ProcessorType, RegistrationHandler> registrationHandlers = SpongeUtils.immutableMapOf(
        ProcessorType.ACTION, new RegistrationHandler(
            (adapter) -> getEngine().getActionManager().addAction((ActionAdapter) adapter),
            (adapter) -> getEngine().getActionManager().removeAction(adapter.getMeta().getName()),
            (adapter) -> getEngine().getActionManager().hasAction(adapter.getMeta().getName())),
        ProcessorType.FILTER, new RegistrationHandler(
            (adapter) -> getEngine().getFilterProcessingUnit().addProcessor((FilterAdapter) adapter),
            (adapter) -> getEngine().getFilterProcessingUnit().removeProcessor(adapter.getMeta().getName()),
            (adapter) -> getEngine().getFilterProcessingUnit().hasProcessor(adapter.getMeta().getName())),
        ProcessorType.TRIGGER, new RegistrationHandler(
            (adapter) -> getEngine().getMainProcessingUnit().addProcessor((TriggerAdapter) adapter),
            (adapter) -> getEngine().getMainProcessingUnit().removeProcessor(adapter.getMeta().getName()),
            (adapter) -> getEngine().getMainProcessingUnit().hasProcessor(adapter.getMeta().getName(), adapter.getType())),
        ProcessorType.RULE, new RegistrationHandler(
            (adapter) -> getEngine().getMainProcessingUnit().addProcessor(
                        new BaseRuleAdapterGroup((BaseRuleAdapter) adapter,
                        (EventSetProcessorMainProcessingUnitHandler<RuleAdapterGroup, RuleAdapter>) getEngine()
                                .getMainProcessingUnit().getHandler(ProcessorType.RULE_GROUP))),
            (adapter) -> getEngine().getMainProcessingUnit().removeProcessor(adapter.getMeta().getName()),
            (adapter) -> getEngine().getMainProcessingUnit().hasProcessor(adapter.getMeta().getName(), ProcessorType.RULE_GROUP)),
        ProcessorType.CORRELATOR, new RegistrationHandler(
            (adapter) -> getEngine().getMainProcessingUnit().addProcessor(
                        new BaseCorrelatorAdapterGroup((BaseCorrelatorAdapter) adapter,
                        (EventSetProcessorMainProcessingUnitHandler<CorrelatorAdapterGroup, CorrelatorAdapter>) getEngine()
                                .getMainProcessingUnit().getHandler(ProcessorType.CORRELATOR_GROUP))),
            (adapter) -> getEngine().getMainProcessingUnit().removeProcessor(adapter.getMeta().getName()),
            (adapter) -> getEngine().getMainProcessingUnit().hasProcessor(adapter.getMeta().getName(), ProcessorType.CORRELATOR_GROUP))
        );
    //@formatter:on

    private Lock lock = new ReentrantLock(true);

    /**
     * Creates a new processor manager.
     *
     * @param engine the engine.
     */
    public DefaultProcessorManager(SpongeEngine engine) {
        super("ProcessorManager", engine);
    }

    /**
     * Returns the engine.
     *
     * @return the engine.
     */
    @Override
    public BaseSpongeEngine getEngine() {
        return (BaseSpongeEngine) super.getEngine();
    }

    @Override
    public void enable(KnowledgeBase knowledgeBase, Object processorClass) {
        doEnable(knowledgeBase, processorClass, null);
    }

    protected void doEnable(KnowledgeBase knowledgeBase, Object processorClass, ProcessorType requiredType) {
        lock.lock();
        try {
            SpongeUtils.doInWrappedException(knowledgeBase, () -> {
                ProcessorInstanceHolder instanceHolder =
                        createProcessorInstanceByProcessorClass(knowledgeBase, processorClass, Processor.class);
                BaseProcessorAdapter adapter = createAdapter(instanceHolder, requiredType);

                bindAdapter(knowledgeBase, instanceHolder.getName(), instanceHolder.getProcessor(), adapter);
                initializeProcessor(instanceHolder, adapter);
                getRegistrationHandler(adapter.getType()).register(adapter);
            }, "enable");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void disable(KnowledgeBase knowledgeBase, Object processorClass) {
        doDisable(knowledgeBase, processorClass, null);
    }

    protected void doDisable(KnowledgeBase knowledgeBase, Object processorClass, ProcessorType requiredType) {
        lock.lock();
        try {
            // Creating temporary instance of a processor to resolve its type.
            ProcessorInstanceHolder instanceHolder =
                    createProcessorInstanceByProcessorClass(knowledgeBase, processorClass, Processor.class);

            BaseProcessorAdapter adapter = createAdapter(instanceHolder, requiredType);
            bindAdapter(knowledgeBase, instanceHolder.getName(), instanceHolder.getProcessor(), adapter);
            getRegistrationHandler(adapter.getType()).deregister(adapter);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T extends Processor> T createProcessorInstance(ProcessorDefinition definition, Class<T> cls) {
        Validate.isInstanceOf(BaseProcessorDefinition.class, definition, "Processor definition must be or extend %s",
                BaseProcessorDefinition.class);
        BaseProcessorDefinition baseDefinition = (BaseProcessorDefinition) definition;
        if (baseDefinition.isJavaDefined()) {
            try {
                if (baseDefinition.getProcessorClass() == null) {
                    throw new SpongeException("No corresponding Java class for processor: " + definition.getMeta().getName());
                }

                return (T) baseDefinition.getProcessorClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw SpongeUtils.wrapException(definition.getMeta().getName(), e);
            }
        } else {
            return definition.getKnowledgeBase().getInterpreter().createProcessorInstance(definition.getMeta().getName(), cls);
        }
    }

    protected ProcessorInstanceHolder createProcessorInstanceByProcessorClass(KnowledgeBase knowledgeBase, Object processorClass,
            Class javaClass) {
        Validate.notNull(processorClass, "Processor class cannot be null");

        ProcessorInstanceHolder result =
                knowledgeBase.getInterpreter().createProcessorInstanceByProcessorClass(knowledgeBase, processorClass, javaClass);
        if (result == null) {
            // Try to create an instance using the default (Java-based) knowledge base interpreter.
            result = getEngine().getKnowledgeBaseManager().getDefaultKnowledgeBase().getInterpreter()
                    .createProcessorInstanceByProcessorClass(knowledgeBase, processorClass, javaClass);
        }

        if (result == null) {
            throw new SpongeException("Unsupported processor class: " + processorClass);
        }

        return result;
    }

    protected BaseProcessorAdapter createAdapter(ProcessorInstanceHolder instanceHolder, ProcessorType requiredType) {
        Processor processor = instanceHolder.getProcessor();
        Validate.isInstanceOf(ProcessorAdapterFactory.class, processor, "Processor must implement %s", ProcessorAdapterFactory.class);

        ProcessorAdapter adapter = ((ProcessorAdapterFactory) processor).createAdapter();
        Validate.isInstanceOf(BaseProcessorAdapter.class, adapter, "Processor adapter must extend %s", BaseProcessorAdapter.class);

        BaseProcessorAdapter result = (BaseProcessorAdapter) adapter;
        result.getDefinition().setJavaDefined(instanceHolder.isJavaDefined());
        result.getDefinition().setProcessorClass(processor.getClass());

        if (requiredType != null) {
            Validate.isTrue(adapter.getType() == requiredType, "%s is %s but should be %s", adapter.getMeta().getName(),
                    adapter.getType().getLabel(), requiredType.getLabel());
        }

        return result;
    }

    protected void bindAdapter(KnowledgeBase knowledgeBase, String name, Processor processor, BaseProcessorAdapter adapter) {
        adapter.setKnowledgeBase((BaseKnowledgeBase) knowledgeBase);
        adapter.setProcessor(processor);
        processor.getMeta().setName(name);
    }

    protected void initializeProcessor(ProcessorInstanceHolder instanceHolder, BaseProcessorAdapter adapter) {
        Processor processor = instanceHolder.getProcessor();

        SpongeUtils.doInWrappedException(adapter.getKnowledgeBase(), () -> processor.onConfigure(),
                SpongeUtils.getProcessorQualifiedName(adapter) + ".onConfigure");

        // Must be verified after onConfigure, because onConfigure may change for example the name of the processor.
        Optional<Map.Entry<ProcessorType, RegistrationHandler>> alreadyRegistered = findAlreadyRegisteredByDifferentType(adapter);
        if (alreadyRegistered.isPresent()) {
            Validate.isTrue(false, "%s named '%s' has already been registered as %s type", adapter.getType().getLabel(),
                    adapter.getMeta().getName(), alreadyRegistered.get().getKey().getLabel());
        }

        SpongeUtils.doInWrappedException(adapter.getKnowledgeBase(), () -> processor.getAdapter().validate(),
                SpongeUtils.getProcessorQualifiedName(adapter).toString());

        if (processor.getAdapter().getDefinition().isSingleton()) {
            SpongeUtils.doInWrappedException(adapter.getKnowledgeBase(), () -> processor.onInit(),
                    SpongeUtils.getProcessorQualifiedName(adapter) + ".onInit");
        }
    }

    protected Optional<Map.Entry<ProcessorType, RegistrationHandler>> findAlreadyRegisteredByDifferentType(ProcessorAdapter adapter) {
        return registrationHandlers.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(adapter.getType()) && entry.getValue().exists(adapter)).findFirst();
    }

    protected RegistrationHandler getRegistrationHandler(ProcessorType type) {
        return Validate.notNull(registrationHandlers.get(type), "Unsupported processor type %s", type);
    }

    @Override
    public void enableAction(KnowledgeBase knowledgeBase, Object actionClass) {
        doEnable(knowledgeBase, actionClass, ProcessorType.ACTION);
    }

    @Override
    public void enableFilter(KnowledgeBase knowledgeBase, Object filterClass) {
        doEnable(knowledgeBase, filterClass, ProcessorType.FILTER);
    }

    @Override
    public void enableTrigger(KnowledgeBase knowledgeBase, Object triggerClass) {
        doEnable(knowledgeBase, triggerClass, ProcessorType.TRIGGER);
    }

    @Override
    public void enableRule(KnowledgeBase knowledgeBase, Object ruleClass) {
        doEnable(knowledgeBase, ruleClass, ProcessorType.RULE);
    }

    @Override
    public void enableCorrelator(KnowledgeBase knowledgeBase, Object correlatorClass) {
        doEnable(knowledgeBase, correlatorClass, ProcessorType.CORRELATOR);
    }

    @Override
    public void disableAction(KnowledgeBase knowledgeBase, Object actionClass) {
        doDisable(knowledgeBase, actionClass, ProcessorType.ACTION);
    }

    @Override
    public void disableFilter(KnowledgeBase knowledgeBase, Object filterClass) {
        doDisable(knowledgeBase, filterClass, ProcessorType.FILTER);
    }

    @Override
    public void disableTrigger(KnowledgeBase knowledgeBase, Object triggerClass) {
        doDisable(knowledgeBase, triggerClass, ProcessorType.TRIGGER);
    }

    @Override
    public void disableRule(KnowledgeBase knowledgeBase, Object ruleClass) {
        doDisable(knowledgeBase, ruleClass, ProcessorType.RULE);
    }

    @Override
    public void disableCorrelator(KnowledgeBase knowledgeBase, Object correlatorClass) {
        doDisable(knowledgeBase, correlatorClass, ProcessorType.CORRELATOR);
    }

    protected String resolveProcessorName(KnowledgeBase knowledgeBase, Object processorClass, Class javaClass) {
        Validate.notNull(processorClass, "Processor class cannot be null");
        String name = knowledgeBase.getInterpreter().getScriptKnowledgeBaseProcessorClassName(processorClass);

        if (name != null) {
            // Script-based processor.
            return name;
        }

        // Java-based processor.
        if (processorClass instanceof Class) {
            Class destJavaClass = (Class) processorClass;
            if (!javaClass.isAssignableFrom(destJavaClass)) {
                throw new SpongeException(
                        "Unsupported processor specification: " + destJavaClass.getName() + " can't be used as " + javaClass.getName());
            }

            return destJavaClass.getName();
        } else {
            throw new SpongeException("Unsupported processor class: " + processorClass);
        }
    }

    @Override
    public List<ProcessorAdapter> getAllProcessorAdapters() {
        List<ProcessorAdapter> processors = new ArrayList<>();

        processors.addAll(getEngine().getActionManager().getRegisteredActionAdapterMap().values());
        processors.addAll(getEngine().getFilterProcessingUnit().getRegisteredProcessorAdapterMap().values());
        processors.addAll(getEngine().getMainProcessingUnit().getRegisteredProcessorAdapterMap().values());

        return processors;
    }

    /**
     * Processor registration handler.
     */
    protected static class RegistrationHandler {

        private Consumer<ProcessorAdapter> registration;

        private Consumer<ProcessorAdapter> deregistration;

        private Predicate<ProcessorAdapter> existence;

        public RegistrationHandler(Consumer<ProcessorAdapter> registration, Consumer<ProcessorAdapter> deregistration,
                Predicate<ProcessorAdapter> existence) {
            this.registration = registration;
            this.deregistration = deregistration;
            this.existence = existence;
        }

        public void register(ProcessorAdapter adapter) {
            registration.accept(adapter);
        }

        public void deregister(ProcessorAdapter adapter) {
            deregistration.accept(adapter);
        }

        public boolean exists(ProcessorAdapter adapter) {
            return existence.test(adapter);
        }
    }
}
