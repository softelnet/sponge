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

package org.openksavi.sponge.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.CategoryMeta;
import org.openksavi.sponge.EngineOperations;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.action.IsActionActiveContext;
import org.openksavi.sponge.action.ProvideArgsParameters;
import org.openksavi.sponge.core.engine.BaseSpongeEngine;
import org.openksavi.sponge.core.event.DefaultEventDefinition;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.correlator.CorrelatorMeta;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;
import org.openksavi.sponge.event.EventDefinition;
import org.openksavi.sponge.event.EventSchedulerEntry;
import org.openksavi.sponge.filter.FilterMeta;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.rule.RuleMeta;
import org.openksavi.sponge.trigger.TriggerMeta;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.util.DataTypeSupplier;
import org.openksavi.sponge.util.ProcessorPredicate;
import org.openksavi.sponge.util.ValueHolder;
import org.openksavi.sponge.util.process.ProcessConfiguration;
import org.openksavi.sponge.util.process.ProcessInstanceBuilder;

public class BaseEngineOperations implements EngineOperations {

    protected BaseSpongeEngine engine;

    public BaseEngineOperations(BaseSpongeEngine engine) {
        this.engine = engine;
    }

    @Override
    public BaseSpongeEngine getEngine() {
        return engine;
    }

    @Override
    public Object call(String actionName, List<Object> args) {
        return engine.getActionManager().callAction(actionName, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T call(Class<T> resultClass, String actionName, List<Object> args) {
        Object result = call(actionName, args);

        Validate.isTrue(result == null || resultClass.isInstance(result), "Action result cannot be cast to expected class %s", resultClass);

        return (T) result;
    }

    @Override
    public Object call(String actionName) {
        return call(actionName, Collections.emptyList());
    }

    @Override
    public <T> T call(Class<T> resultClass, String actionName) {
        return call(resultClass, actionName, Collections.emptyList());
    }

    @Override
    public ValueHolder<Object> callIfExists(String actionName, List<Object> args) {
        return engine.getActionManager().callActionIfExists(actionName, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ValueHolder<T> callIfExists(Class<T> resultClass, String actionName, List<Object> args) {
        ValueHolder<Object> resultHolder = callIfExists(actionName, args);

        if (resultHolder == null) {
            return null;
        }

        Object result = resultHolder.getValue();
        Validate.isTrue(result == null || resultClass.isInstance(result), "Action result cannot be cast to expected class %s", resultClass);

        return new ValueHolder<>((T) result);
    }

    @Override
    public ValueHolder<Object> callIfExists(String actionName) {
        return callIfExists(actionName, Collections.emptyList());
    }

    @Override
    public <T> ValueHolder<T> callIfExists(Class<T> resultClass, String actionName) {
        return callIfExists(resultClass, actionName, Collections.emptyList());
    }

    @Override
    public boolean isActionActive(String actionName, IsActionActiveContext context) {
        ActionAdapter actionAdapter =
                Validate.notNull(engine.getActionManager().getActionAdapter(actionName), "Action '%s' not found", actionName);

        try {
            return actionAdapter.isActive(context);
        } catch (Throwable e) {
            throw SpongeUtils.wrapException(actionAdapter.getProcessor(), e);
        }
    }

    @Override
    public Map<String, ProvidedValue<?>> provideActionArgs(String actionName, ProvideArgsParameters parameters) {
        ActionAdapter actionAdapter =
                Validate.notNull(engine.getActionManager().getActionAdapter(actionName), "Action '%s' not found", actionName);
        try {
            return actionAdapter.provideArgs(parameters);
        } catch (Throwable e) {
            throw SpongeUtils.wrapException(actionAdapter.getProcessor(), e);
        }
    }

    /**
     * Removes event from Event Scheduler.
     *
     * @param entry scheduled event entry.
     * @return informs whether the specified event has been scheduled.
     */
    @Override
    public boolean removeEvent(EventSchedulerEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Event entry cannot be null");
        }
        return engine.getEventScheduler().remove(entry);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Plugin> T getPlugin(String name) {
        return Validate.notNull((T) engine.getPluginManager().getPlugin(name), "Plugin named %s is not registered", name);
    }

    @Override
    public <T extends Plugin> T getPlugin(Class<T> cls, String name) {
        return Validate.notNull(engine.getPluginManager().getPlugin(cls, name), "Plugin named %s of class %s is not registered", name, cls);
    }

    @Override
    public <T extends Plugin> T getPlugin(Class<T> cls) {
        return Validate.notNull(engine.getPluginManager().getPlugin(cls), "Plugin of class %s is not registered", cls);
    }

    @Override
    public boolean hasPlugin(String name) {
        return engine.getPluginManager().getPlugin(name) != null;
    }

    @Override
    public <T extends Plugin> boolean hasPlugin(Class<T> cls, String name) {
        return engine.getPluginManager().getPlugin(cls, name) != null;
    }

    @Override
    public <T extends Plugin> boolean hasPlugin(Class<T> cls) {
        return engine.getPluginManager().getPlugin(cls) != null;
    }

    /**
     * Returns the engine version.
     *
     * @return the engine version.
     */
    @Override
    public String getVersion() {
        return engine.getVersion();
    }

    @Override
    public String getName() {
        return engine.getName();
    }

    @Override
    public String getLabel() {
        return engine.getLabel();
    }

    @Override
    public String getDescription() {
        return engine.getDescription();
    }

    @Override
    public String getLicense() {
        return engine.getLicense();
    }

    /**
     * Returns the engine info.
     *
     * @return the engine info.
     */
    @Override
    public String getInfo() {
        return engine.getInfo();
    }

    /**
     * Returns the engine statistics summary.
     *
     * @return the engine statistics summary.
     */
    @Override
    public String getStatisticsSummary() {
        return engine.getStatisticsManager().getSummary();
    }

    @Override
    public EventDefinition event(String name) {
        return new DefaultEventDefinition(this, name, engine.getConfigurationManager().getEventClonePolicy());
    }

    @Override
    public EventDefinition event(String name, EventClonePolicy policy) {
        return new DefaultEventDefinition(this, name, policy);
    }

    @Override
    public EventDefinition event(Event event) {
        return new DefaultEventDefinition(this, event);
    }

    @Override
    public void shutdown() {
        engine.shutdown();
    }

    @Override
    public void requestShutdown() {
        engine.requestShutdown();
    }

    @Override
    public void reload() {
        engine.reload();
    }

    @Override
    public void requestReload() {
        engine.requestReload();
    }

    @Override
    public boolean hasAction(String name) {
        return engine.getActionManager().hasAction(name);
    }

    @Override
    public boolean hasFilter(String name) {
        return engine.getFilterProcessingUnit().hasProcessor(name);
    }

    @Override
    public boolean hasTrigger(String name) {
        return engine.getMainProcessingUnit().hasProcessor(name);
    }

    @Override
    public boolean hasRule(String name) {
        return engine.getMainProcessingUnit().hasProcessor(name);
    }

    @Override
    public boolean hasCorrelator(String name) {
        return engine.getMainProcessingUnit().hasProcessor(name);
    }

    @Override
    public void setVariable(String name, Object value) {
        engine.getSession().setVariable(name, value);
    }

    @Override
    public <T> T getVariable(String name) {
        return engine.getSession().getVariable(name);
    }

    @Override
    public <T> T getVariable(Class<T> cls, String name) {
        return engine.getSession().getVariable(cls, name);
    }

    @Override
    public <T> T getVariable(String name, T defaultValue) {
        return engine.getSession().getVariable(name, defaultValue);
    }

    @Override
    public <T> T getVariable(Class<T> cls, String name, T defaultValue) {
        return engine.getSession().getVariable(cls, name, defaultValue);
    }

    @Override
    public boolean hasVariable(String name) {
        return engine.getSession().hasVariable(name);
    }

    @Override
    public void removeVariable(String name) {
        engine.getSession().removeVariable(name);
    }

    @Override
    public <T> void setVariableIfNone(String name, Supplier<T> supplier) {
        engine.getSession().setVariableIfNone(name, supplier);
    }

    @Override
    public void updateVariable(String name) {
        engine.getSession().updateVariable(name);
    }

    protected String getProperty(String name, String defaultValue, boolean throwException) {
        String result = engine.getConfigurationManager().getProperty(name);
        Validate.isTrue(result != null || !throwException, "Property '%s' not found or empty", name);

        return result != null ? result : defaultValue;
    }

    @Override
    public String getProperty(String name) {
        return getProperty(name, null, true);
    }

    @Override
    public String getProperty(String name, String defaultValue) {
        return getProperty(name, defaultValue, false);
    }

    @Override
    public boolean hasProperty(String name) {
        return engine.getConfigurationManager().getProperty(name) != null;
    }

    @Override
    public String getHome() {
        return engine.getConfigurationManager().getHome();
    }

    @Override
    public ProcessInstanceBuilder process(ProcessConfiguration configuration) {
        return new ProcessInstanceBuilder(engine, configuration);
    }

    @Override
    public ProcessInstanceBuilder process(String executable, String... arguments) {
        return new ProcessInstanceBuilder(engine, executable).arguments(arguments);
    }

    @Override
    public void addCategory(CategoryMeta categoryMeta) {
        engine.addCategory(categoryMeta);
    }

    @Override
    public void addCategories(CategoryMeta... categoryMeta) {
        engine.addCategories(categoryMeta);
    }

    @Override
    public CategoryMeta getCategory(String categoryName) {
        return engine.getCategory(categoryName);
    }

    @Override
    public boolean hasCategory(String categoryName) {
        return engine.hasCategory(categoryName);
    }

    @Override
    public CategoryMeta removeCategory(String categoryName) {
        return engine.removeCategory(categoryName);
    }

    @Override
    public List<CategoryMeta> getCategories() {
        return engine.getCategories();
    }

    @Override
    public void selectCategory(String categoryName, ProcessorType processorType, ProcessorPredicate predicate) {
        engine.selectCategory(categoryName, processorType, predicate);
    }

    @Override
    public void selectCategory(String categoryName, ProcessorPredicate predicate) {
        engine.selectCategory(categoryName, predicate);
    }

    @Override
    public ActionMeta getActionMeta(String actionName) {
        return engine.getActionMeta(actionName);
    }

    @Override
    public FilterMeta getFilterMeta(String filterName) {
        return engine.getFilterMeta(filterName);
    }

    @Override
    public TriggerMeta getTriggerMeta(String triggerName) {
        return engine.getTriggerMeta(triggerName);
    }

    @Override
    public CorrelatorMeta getCorrelatorMeta(String correlatorName) {
        return engine.getCorrelatorMeta(correlatorName);
    }

    @Override
    public RuleMeta getRuleMeta(String ruleName) {
        return engine.getRuleMeta(ruleName);
    }

    @Override
    public <T extends DataType<?>> void addType(String registeredTypeName, DataTypeSupplier<T> typeSupplier) {
        engine.addType(registeredTypeName, typeSupplier);
    }

    @Override
    public <T extends DataType<?>> T getType(String registeredTypeName) {
        return engine.getType(registeredTypeName);
    }

    @Override
    public <T extends DataType<?>> T getType(String registeredTypeName, String locationName) {
        return engine.getType(registeredTypeName, locationName);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, DataType> getTypes() {
        return engine.getTypes();
    }

    @Override
    public boolean hasType(String registeredTypeName) {
        return engine.hasType(registeredTypeName);
    }

    @Override
    public boolean removeType(String registeredTypeName) {
        return engine.removeType(registeredTypeName);
    }

    @Override
    public void addEventType(String eventTypeName, RecordType dataType) {
        engine.addEventType(eventTypeName, dataType);
    }

    @Override
    public RecordType getEventType(String eventTypeName) {
        return engine.getEventType(eventTypeName);
    }

    @Override
    public boolean hasEventType(String eventTypeName) {
        return engine.hasEventType(eventTypeName);
    }

    @Override
    public Map<String, RecordType> getEventTypes() {
        return engine.getEventTypes();
    }

    @Override
    public boolean removeEventType(String eventTypeName) {
        return engine.removeEventType(eventTypeName);
    }
}
