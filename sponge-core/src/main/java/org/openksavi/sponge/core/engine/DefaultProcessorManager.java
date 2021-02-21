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
import org.openksavi.sponge.ProcessorBuilder;
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
import org.openksavi.sponge.engine.InitialProcessorInstance;
import org.openksavi.sponge.engine.ProcessorManager;
import org.openksavi.sponge.engine.ProcessorProvider;
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
            (name) -> getEngine().getActionManager().removeAction(name),
            (name) -> getEngine().getActionManager().hasAction(name)),
        ProcessorType.FILTER, new RegistrationHandler(
            (adapter) -> getEngine().getFilterProcessingUnit().addProcessor((FilterAdapter) adapter),
            (name) -> getEngine().getFilterProcessingUnit().removeProcessor(name),
            (name) -> getEngine().getFilterProcessingUnit().hasProcessor(name)),
        ProcessorType.TRIGGER, new RegistrationHandler(
            (adapter) -> getEngine().getMainProcessingUnit().addProcessor((TriggerAdapter) adapter),
            (name) -> getEngine().getMainProcessingUnit().removeProcessor(name),
            (name) -> getEngine().getMainProcessingUnit().hasProcessor(name, ProcessorType.TRIGGER)),
        ProcessorType.RULE, new RegistrationHandler(
            (adapter) -> getEngine().getMainProcessingUnit().addProcessor(
                        new BaseRuleAdapterGroup((BaseRuleAdapter) adapter,
                        (EventSetProcessorMainProcessingUnitHandler<RuleAdapterGroup, RuleAdapter>) getEngine()
                                .getMainProcessingUnit().getHandler(ProcessorType.RULE_GROUP))),
            (name) -> getEngine().getMainProcessingUnit().removeProcessor(name),
            (name) -> getEngine().getMainProcessingUnit().hasProcessor(name, ProcessorType.RULE_GROUP)),
        ProcessorType.CORRELATOR, new RegistrationHandler(
            (adapter) -> getEngine().getMainProcessingUnit().addProcessor(
                        new BaseCorrelatorAdapterGroup((BaseCorrelatorAdapter) adapter,
                        (EventSetProcessorMainProcessingUnitHandler<CorrelatorAdapterGroup, CorrelatorAdapter>) getEngine()
                                .getMainProcessingUnit().getHandler(ProcessorType.CORRELATOR_GROUP))),
            (name) -> getEngine().getMainProcessingUnit().removeProcessor(name),
            (name) -> getEngine().getMainProcessingUnit().hasProcessor(name, ProcessorType.CORRELATOR_GROUP))
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
        Validate.notNull(processorClass, "Processor class cannot be null");
        doEnable(knowledgeBase, createProcessorProvider(processorClass), null);
    }

    @Override
    public <T extends Processor<?>> void enable(KnowledgeBase knowledgeBase, ProcessorBuilder<T> processorBuilder) {
        Validate.notNull(processorBuilder, "Processor builder cannot be null");
        doEnable(knowledgeBase, new BuilderProcessorProvider(processorBuilder), null);
    }

    protected ProcessorProvider createProcessorProvider(Object processorClassOrInstance) {
        if (processorClassOrInstance instanceof Processor) {
            return new SingleInstanceProcessorProvider((Processor) processorClassOrInstance);
        }

        return new ClassProcessorProvider(processorClassOrInstance);
    }

    protected void doEnable(KnowledgeBase knowledgeBase, ProcessorProvider processorProvider, ProcessorType requiredType) {
        lock.lock();
        try {
            SpongeUtils.doInWrappedException(knowledgeBase, () -> {
                InitialProcessorInstance initialInstance = processorProvider.createInitialProcessorInstance(knowledgeBase, Processor.class);
                BaseProcessorAdapter adapter = createAdapter(initialInstance.getProcessor(), requiredType, processorProvider);
                bindAdapter(knowledgeBase, initialInstance.getName(), initialInstance.getProcessor(), adapter);
                initializeProcessor(initialInstance.getProcessor(), adapter);

                getRegistrationHandler(adapter.getType()).register(adapter);
            }, "enable");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void disable(KnowledgeBase knowledgeBase, String processorName) {
        lock.lock();
        try {
            registrationHandlers.forEach((type, handler) -> {
                if (handler.exists(processorName)) {
                    handler.deregister(processorName);
                }
            });
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void disable(KnowledgeBase knowledgeBase, Object processorClass) {
        doDisable(knowledgeBase, processorClass, null);
    }

    protected void doDisable(KnowledgeBase knowledgeBase, Object processorClassOrInstance, ProcessorType requiredType) {
        lock.lock();
        try {
            if (processorClassOrInstance instanceof Processor) {
                Processor processor = (Processor) processorClassOrInstance;

                getRegistrationHandler(processor.getAdapter().getType()).deregister(processor.getAdapter().getMeta().getName());
            } else {
                ProcessorProvider processorProvider = new ClassProcessorProvider(processorClassOrInstance);
                // Creating temporary instance of a processor to resolve its type.
                InitialProcessorInstance initialInstance = processorProvider.createInitialProcessorInstance(knowledgeBase, Processor.class);

                BaseProcessorAdapter adapter = createAdapter(initialInstance.getProcessor(), requiredType, processorProvider);
                bindAdapter(knowledgeBase, initialInstance.getName(), initialInstance.getProcessor(), adapter);

                getRegistrationHandler(adapter.getType()).deregister(adapter.getMeta().getName());
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <T extends Processor> T createProcessorInstance(ProcessorDefinition definition, Class<T> cls) {
        Validate.isInstanceOf(BaseProcessorDefinition.class, definition, "Processor definition must be or extend %s",
                BaseProcessorDefinition.class);
        BaseProcessorDefinition baseDefinition = (BaseProcessorDefinition) definition;

        return (T) baseDefinition.getProcessorProvider().createAdditionalProcessorInstance(baseDefinition, cls);
    }

    protected BaseProcessorAdapter createAdapter(Processor processor, ProcessorType requiredType, ProcessorProvider processorProvider) {
        Validate.isInstanceOf(ProcessorAdapterFactory.class, processor, "Processor must implement %s", ProcessorAdapterFactory.class);

        ProcessorAdapter adapter = ((ProcessorAdapterFactory) processor).createAdapter();
        Validate.isInstanceOf(BaseProcessorAdapter.class, adapter, "Processor adapter must extend %s", BaseProcessorAdapter.class);

        BaseProcessorAdapter baseAdapter = (BaseProcessorAdapter) adapter;

        baseAdapter.getDefinition().setProcessorProvider(processorProvider);

        if (requiredType != null) {
            Validate.isTrue(adapter.getType() == requiredType, "%s is %s but should be %s", adapter.getMeta().getName(),
                    adapter.getType().getLabel(), requiredType.getLabel());
        }

        return baseAdapter;
    }

    protected void bindAdapter(KnowledgeBase knowledgeBase, String name, Processor processor, BaseProcessorAdapter adapter) {
        adapter.setKnowledgeBase((BaseKnowledgeBase) knowledgeBase);
        adapter.setProcessor(processor);
        processor.getMeta().setName(name);
    }

    protected void initializeProcessor(Processor processor, BaseProcessorAdapter adapter) {
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

        if (processor.getAdapter().getMeta().getCategory() == null) {
            SpongeUtils.applyCategory(adapter);
        }
    }

    protected Optional<Map.Entry<ProcessorType, RegistrationHandler>> findAlreadyRegisteredByDifferentType(ProcessorAdapter adapter) {
        return registrationHandlers.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(adapter.getType()) && entry.getValue().exists(adapter.getMeta().getName()))
                .findFirst();
    }

    protected RegistrationHandler getRegistrationHandler(ProcessorType type) {
        return Validate.notNull(registrationHandlers.get(type), "Unsupported processor type %s", type);
    }

    @Override
    public void enableAction(KnowledgeBase knowledgeBase, Object actionClass) {
        doEnable(knowledgeBase, createProcessorProvider(actionClass), ProcessorType.ACTION);
    }

    @Override
    public void enableFilter(KnowledgeBase knowledgeBase, Object filterClass) {
        doEnable(knowledgeBase, createProcessorProvider(filterClass), ProcessorType.FILTER);
    }

    @Override
    public void enableTrigger(KnowledgeBase knowledgeBase, Object triggerClass) {
        doEnable(knowledgeBase, createProcessorProvider(triggerClass), ProcessorType.TRIGGER);
    }

    @Override
    public void enableRule(KnowledgeBase knowledgeBase, Object ruleClass) {
        doEnable(knowledgeBase, createProcessorProvider(ruleClass), ProcessorType.RULE);
    }

    @Override
    public void enableCorrelator(KnowledgeBase knowledgeBase, Object correlatorClass) {
        doEnable(knowledgeBase, createProcessorProvider(correlatorClass), ProcessorType.CORRELATOR);
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

        private Consumer<String> deregistration;

        private Predicate<String> existence;

        public RegistrationHandler(Consumer<ProcessorAdapter> registration, Consumer<String> deregistration, Predicate<String> existence) {
            this.registration = registration;
            this.deregistration = deregistration;
            this.existence = existence;
        }

        public void register(ProcessorAdapter adapter) {
            registration.accept(adapter);
        }

        public void deregister(String name) {
            deregistration.accept(name);
        }

        public boolean exists(String name) {
            return existence.test(name);
        }
    }
}
