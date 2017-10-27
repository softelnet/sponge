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
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.correlator.CorrelatorAdapter;
import org.openksavi.sponge.correlator.CorrelatorAdapterGroup;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ProcessorManager;
import org.openksavi.sponge.engine.ProcessorType;
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
    protected Map<ProcessorType, RegistrationHandler> registrationHandlers = Utils.immutableMapOf(
        ProcessorType.ACTION, new RegistrationHandler(
            (adapter) -> getEngine().getActionManager().addAction((ActionAdapter) adapter),
            (adapter) -> getEngine().getActionManager().removeAction(adapter.getName()),
            (adapter) -> getEngine().getActionManager().existsAction(adapter.getName())),
        ProcessorType.FILTER, new RegistrationHandler(
            (adapter) -> getEngine().getFilterProcessingUnit().addProcessor((FilterAdapter) adapter),
            (adapter) -> getEngine().getFilterProcessingUnit().removeProcessor(adapter.getName()),
            (adapter) -> getEngine().getFilterProcessingUnit().existsProcessor(adapter.getName())),
        ProcessorType.TRIGGER, new RegistrationHandler(
            (adapter) -> getEngine().getMainProcessingUnit().addProcessor((TriggerAdapter) adapter),
            (adapter) -> getEngine().getMainProcessingUnit().removeProcessor(adapter.getName()),
            (adapter) -> getEngine().getMainProcessingUnit().existsProcessor(adapter.getName(), adapter.getType())),
        ProcessorType.RULE, new RegistrationHandler(
            (adapter) -> getEngine().getMainProcessingUnit().addProcessor(
                        new BaseRuleAdapterGroup((BaseRuleAdapter) adapter,
                        (EventSetProcessorMainProcessingUnitHandler<RuleAdapterGroup, RuleAdapter>) getEngine()
                                .getMainProcessingUnit().getHandler(ProcessorType.RULE_GROUP))),
            (adapter) -> getEngine().getMainProcessingUnit().removeProcessor(adapter.getName()),
            (adapter) -> getEngine().getMainProcessingUnit().existsProcessor(adapter.getName(), ProcessorType.RULE_GROUP)),
        ProcessorType.CORRELATOR, new RegistrationHandler(
            (adapter) -> getEngine().getMainProcessingUnit().addProcessor(
                        new BaseCorrelatorAdapterGroup((BaseCorrelatorAdapter) adapter,
                        (EventSetProcessorMainProcessingUnitHandler<CorrelatorAdapterGroup, CorrelatorAdapter>) getEngine()
                                .getMainProcessingUnit().getHandler(ProcessorType.CORRELATOR_GROUP))),
            (adapter) -> getEngine().getMainProcessingUnit().removeProcessor(adapter.getName()),
            (adapter) -> getEngine().getMainProcessingUnit().existsProcessor(adapter.getName(), ProcessorType.CORRELATOR_GROUP))
        );
    //@formatter:on

    private Lock lock = new ReentrantLock(true);

    /**
     * Creates a new processor manager.
     *
     * @param engine the engine.
     */
    public DefaultProcessorManager(Engine engine) {
        super("ProcessorManager", engine);
    }

    /**
     * Returns the engine.
     *
     * @return the engine.
     */
    @Override
    public BaseEngine getEngine() {
        return (BaseEngine) super.getEngine();
    }

    @Override
    public void enable(KnowledgeBase knowledgeBase, Object processorClass) {
        doEnable(knowledgeBase, processorClass, null);
    }

    protected void doEnable(KnowledgeBase knowledgeBase, Object processorClass, ProcessorType requiredType) {
        lock.lock();
        try {
            InstanceHolder instanceHolder = createProcessorInstanceByProcessorClass(knowledgeBase, processorClass, Processor.class);
            BaseProcessorAdapter adapter = createAdapter(instanceHolder, requiredType);

            bindAdapter(knowledgeBase, instanceHolder.getName(), instanceHolder.getProcessor(), adapter);
            initializeProcessor(instanceHolder, adapter);
            getRegistrationHandler(adapter.getType()).register(adapter);
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
            InstanceHolder instanceHolder = createProcessorInstanceByProcessorClass(knowledgeBase, processorClass, Processor.class);

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
        if (((BaseProcessorDefinition) definition).isJavaDefined()) {
            try {
                return (T) Class.forName(definition.getName()).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw Utils.wrapException("createProcessorInstance", e);
            }
        } else {
            return definition.getKnowledgeBase().getInterpreter().createProcessorInstance(definition.getName(), cls);
        }
    }

    protected InstanceHolder createProcessorInstanceByProcessorClass(KnowledgeBase knowledgeBase, Object processorClass, Class javaClass) {
        Validate.notNull(processorClass, "Processor class cannot be null");

        String name = knowledgeBase.getInterpreter().getScriptKnowledgeBaseProcessorClassName(processorClass);
        if (name != null) {
            // Script-based processor.
            return new InstanceHolder(knowledgeBase.getInterpreter().createProcessorInstance(name, javaClass), name, false);
        } else if (processorClass instanceof Class) {
            // Java-based processor.
            Class<?> destJavaClass = (Class<?>) processorClass;
            if (!javaClass.isAssignableFrom(destJavaClass)) {
                throw new SpongeException(
                        "Unsupported processor specification: " + destJavaClass.getName() + " can't be used as " + javaClass.getName());
            }

            try {
                return new InstanceHolder((Processor) destJavaClass.newInstance(), destJavaClass.getName(), true);
            } catch (Throwable e) {
                throw Utils.wrapException(destJavaClass.getName(), e);
            }
        } else {
            throw new SpongeException("Unsupported processor class: " + processorClass);
        }
    }

    protected BaseProcessorAdapter createAdapter(InstanceHolder instanceHolder, ProcessorType requiredType) {
        Processor processor = instanceHolder.getProcessor();
        Validate.isInstanceOf(ProcessorAdapterFactory.class, processor, "Processor must implement %s", ProcessorAdapterFactory.class);

        ProcessorAdapter adapter = ((ProcessorAdapterFactory) processor).createAdapter();
        Validate.isInstanceOf(BaseProcessorAdapter.class, adapter, "Processor adapter must extend %s", BaseProcessorAdapter.class);

        BaseProcessorAdapter result = (BaseProcessorAdapter) adapter;
        result.getDefinition().setJavaDefined(instanceHolder.isJavaDefined());

        if (requiredType != null) {
            Validate.isTrue(adapter.getType() == requiredType, "% is % but should be %", adapter.getName(),
                    adapter.getType().getDisplayName(), requiredType.getDisplayName());
        }

        return result;
    }

    protected void bindAdapter(KnowledgeBase knowledgeBase, String name, Processor processor, BaseProcessorAdapter adapter) {
        adapter.setKnowledgeBase((BaseKnowledgeBase) knowledgeBase);
        adapter.setProcessor(processor);
        processor.setName(name);
    }

    protected void initializeProcessor(InstanceHolder instanceHolder, BaseProcessorAdapter adapter) {
        Processor processor = instanceHolder.getProcessor();

        processor.onConfigure();

        // Must be verified after onConfigure, because onConfigure may change for example the name of the processor.
        Optional<Map.Entry<ProcessorType, RegistrationHandler>> alreadyRegistered = findAlreadyRegisteredByDifferentType(adapter);
        if (alreadyRegistered.isPresent()) {
            Validate.isTrue(false, "% named '%' has already been registered as % type", adapter.getType().getDisplayName(),
                    adapter.getName(), alreadyRegistered.get().getKey().getDisplayName());
        }

        processor.getAdapter().validate();

        if (processor.getAdapter().getDefinition().isSingleton()) {
            processor.onInit();
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

    protected static class InstanceHolder {

        private Processor processor;

        private String name;

        private boolean javaDefined;

        public InstanceHolder(Processor processor, String name, boolean javaDefined) {
            this.processor = processor;
            this.name = name;
            this.javaDefined = javaDefined;
        }

        public Processor getProcessor() {
            return processor;
        }

        public void setProcessor(Processor processor) {
            this.processor = processor;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isJavaDefined() {
            return javaDefined;
        }

        public void setJavaDefined(boolean javaDefined) {
            this.javaDefined = javaDefined;
        }
    }
}
