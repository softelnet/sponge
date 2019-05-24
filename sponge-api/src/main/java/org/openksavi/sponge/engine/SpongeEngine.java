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

package org.openksavi.sponge.engine;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.CategoryMeta;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.correlator.CorrelatorAdapterGroup;
import org.openksavi.sponge.correlator.CorrelatorMeta;
import org.openksavi.sponge.engine.event.EventScheduler;
import org.openksavi.sponge.engine.processing.EventSetProcessorDurationStrategy;
import org.openksavi.sponge.filter.FilterAdapter;
import org.openksavi.sponge.filter.FilterMeta;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBaseFileProvider;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.rule.RuleAdapterGroup;
import org.openksavi.sponge.rule.RuleMeta;
import org.openksavi.sponge.spi.EngineModuleProvider;
import org.openksavi.sponge.spi.EventQueueProvider;
import org.openksavi.sponge.spi.KnowledgeBaseInterpreterFactoryProvider;
import org.openksavi.sponge.spi.ProcessingUnitProvider;
import org.openksavi.sponge.trigger.TriggerAdapter;
import org.openksavi.sponge.trigger.TriggerMeta;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.util.DataTypeSupplier;
import org.openksavi.sponge.util.PatternMatcher;
import org.openksavi.sponge.util.ProcessorPredicate;

/**
 * The Sponge engine. This is the main interface of the system. An instance of this interface manages all main components.
 */
public interface SpongeEngine extends EngineModule {

    /**
     * Returns this engine name. May be {@code null}.
     *
     * @return this engine name.
     */
    @Override
    String getName();

    /**
     * Initializes the engine (before starting up).
     */
    void init();

    /**
     * Returns the Action Manager.
     *
     * @return the Action Manager.
     */
    ActionManager getActionManager();

    /**
     * Returns the Plugin Manager.
     *
     * @return the Plugin Manager.
     */
    PluginManager getPluginManager();

    /**
     * Returns the Knowledge Base Manager.
     *
     * @return the Knowledge Base Manager.
     */
    KnowledgeBaseManager getKnowledgeBaseManager();

    /**
     * Returns the Configuration Manager.
     *
     * @return the Configuration Manager.
     */
    ConfigurationManager getConfigurationManager();

    /**
     * Returns the Processing Unit Manager.
     *
     * @return the Processing Unit Manager.
     */
    ProcessingUnitManager getProcessingUnitManager();

    /**
     * Returns the Event Queue Manager.
     *
     * @return the Event Queue Manager.
     */
    EventQueueManager getEventQueueManager();

    /**
     * Returns the Event Scheduler.
     *
     * @return the Event Scheduler.
     */
    EventScheduler getEventScheduler();

    /**
     * Returns the Thread Pool Manager.
     *
     * @return the Thread Pool Manager.
     */
    ThreadPoolManager getThreadPoolManager();

    /**
     * Returns the Processor Manager.
     *
     * @return the Processor Manager.
     */
    ProcessorManager getProcessorManager();

    /**
     * Returns the Statistics Manager.
     *
     * @return the Statistics Manager.
     */
    StatisticsManager getStatisticsManager();

    /**
     * Returns the list of filter adapters.
     *
     * @return the list of filter adapters.
     */
    List<FilterAdapter> getFilters();

    /**
     * Returns the list of trigger adapters.
     *
     * @return the list of trigger adapters.
     */
    List<TriggerAdapter> getTriggers();

    /**
     * Returns the list of rule adapter groups.
     *
     * @return the list of rule adapter groups.
     */
    List<RuleAdapterGroup> getRuleGroups();

    /**
     * Returns the list of correlator adapter groups.
     *
     * @return the list of correlator adapter groups.
     */
    List<CorrelatorAdapterGroup> getCorrelatorGroups();

    /**
     * Returns the list of action adapters.
     *
     * @return the list of action adapters.
     */
    List<ActionAdapter> getActions();

    @SuppressWarnings("rawtypes")
    List<ProcessorAdapter> getProcessors();

    /**
     * Handles the error.
     *
     * @param processorAdapter the processor adapter.
     * @param e the exception.
     */
    void handleError(ProcessorAdapter<?> processorAdapter, Throwable e);

    /**
     * Handles the error.
     *
     * @param sourceName the source name.
     * @param e the exception.
     */
    void handleError(String sourceName, Throwable e);

    /**
     * Returns the list of plugins.
     *
     * @return the list of plugins.
     */
    List<Plugin> getPlugins();

    /**
     * Reloads script-based knowledge bases.
     */
    void reload();

    /**
     * Reloads script-based knowledge bases using another thread.
     */
    void requestReload();

    /**
     * Returns the engine version.
     *
     * @return the engine version.
     */
    String getVersion();

    /**
     * Returns the engine info.
     *
     * @return the engine info.
     */
    String getInfo();

    /**
     * Shuts down the engine using another thread.
     */
    void requestShutdown();

    /**
     * Checks if an error occurred during engine processing.
     *
     * @return {@code true} if an error occurred during engine processing.
     */
    boolean isError();

    /**
     * Returns the error that occurred during engine processing or {@code null} if no error occurred.
     *
     * @return the error.
     */
    Throwable getError();

    /**
     * Clears the error that occurred during engine processing.
     */
    void clearError();

    /**
     * Returns the engine operations.
     *
     * @return the engine operations.
     */
    KnowledgeBaseEngineOperations getOperations();

    /**
     * Returns the knowledge base file provider.
     *
     * @return the knowledge base file provider.
     */
    KnowledgeBaseFileProvider getKnowledgeBaseFileProvider();

    /**
     * Adds the OnStartup listener.
     *
     * @param onStartupListener the OnStartup listener.
     */
    void addOnStartupListener(OnStartupListener onStartupListener);

    /**
     * Returns all OnStartup listeners.
     *
     * @return all OnStartup listeners.
     */
    List<OnStartupListener> getOnStartupListeners();

    /**
     * Removes the OnStartup listener.
     *
     * @param onStartupListener the OnStartup listener.
     * @return {@code true} if there was such OnStartup listener.
     */
    boolean removeOnStartupListener(OnStartupListener onStartupListener);

    /**
     * Adds the OnShutdown listener.
     *
     * @param onShutdownListener the OnShutdown listener.
     */
    void addOnShutdownListener(OnShutdownListener onShutdownListener);

    /**
     * Returns all OnShutdown listeners.
     *
     * @return all OnShutdown listeners.
     */
    List<OnShutdownListener> getOnShutdownListeners();

    /**
     * Removes the OnShutdown listener.
     *
     * @param onShutdownListener the OnShutdown listener.
     * @return {@code true} if there was such OnShutdown listener.
     */
    boolean removeOnShutdownListener(OnShutdownListener onShutdownListener);

    /**
     * Returns the exception handler.
     *
     * @return the exception handler.
     */
    public ExceptionHandler getExceptionHandler();

    /**
     * Sets the exception handler.
     *
     * @param exceptionHandler the new exception handler.
     */
    void setExceptionHandler(ExceptionHandler exceptionHandler);

    /**
     * Returns default parameters. Notice that they are only default values. Current parameter values are provided by
     * {@link org.openksavi.sponge.engine.ConfigurationManager}.
     *
     * @return default parameters.
     */
    EngineParameters getDefaultParameters();

    // /**
    // * Sets an event ID generator.
    // *
    // * @param eventIdGenerator an event ID generator.
    // */
    // void setEventIdGenerator(EventIdGenerator eventIdGenerator);

    /**
     * Sets the configuration filename.
     *
     * @param configurationFilename the configuration filename.
     */
    void setConfigurationFilename(String configurationFilename);

    /**
     * Sets the processing unit provider.
     *
     * @param processingUnitProvider the processing unit provider.
     */
    void setProcessingUnitProvider(ProcessingUnitProvider processingUnitProvider);

    /**
     * Sets the knowledge base interpreter factory providers.
     *
     * @param knowledgeBaseInterpreterFactoryProviders the knowledge base interpreter factory providers.
     */
    void setKnowledgeBaseInterpreterFactoryProviders(
            List<KnowledgeBaseInterpreterFactoryProvider> knowledgeBaseInterpreterFactoryProviders);

    /**
     * Sets the module provider.
     *
     * @param moduleProvider the module provider.
     */
    void setModuleProvider(EngineModuleProvider moduleProvider);

    /**
     * Sets the event queue provider.
     *
     * @param eventQueueProvider the event queue provider.
     */
    void setEventQueueProvider(EventQueueProvider eventQueueProvider);

    /**
     * Sets the knowledge base file provider.
     *
     * @param knowledgeBaseFileProvider the knowledge base file provider.
     */
    void setKnowledgeBaseFileProvider(KnowledgeBaseFileProvider knowledgeBaseFileProvider);

    /**
     * Returns the strategy for handling an event set processor duration timeout.
     *
     * @return the duration strategy.
     */
    EventSetProcessorDurationStrategy getEventSetProcessorDurationStrategy();

    /**
     * Sets the strategy for handling an event set processor duration timeout.
     *
     * @param durationStrategy the duration strategy.
     */
    void setEventSetProcessorDurationStrategy(EventSetProcessorDurationStrategy durationStrategy);

    /**
     * Returns the pattern matcher.
     *
     * @return the pattern matcher.
     */
    PatternMatcher getPatternMatcher();

    /**
     * Sets the pattern matcher.
     *
     * @param patternMatcher the pattern matcher.
     */
    void setPatternMatcher(PatternMatcher patternMatcher);

    /**
     * Adds or updates a category.
     *
     * @param categoryMeta the category metadata.
     */
    void addCategory(CategoryMeta categoryMeta);

    /**
     * Adds or updates categories.
     *
     * @param categoryMeta the category metadata array.
     */
    void addCategories(CategoryMeta... categoryMeta);

    /**
     * Returns a category.
     *
     * @param categoryName the category name.
     * @return the category metadata or {@code null}.
     */
    CategoryMeta getCategory(String categoryName);

    /**
     * Returns {@code true} if the category is registered.
     *
     * @param categoryName the category name.
     * @return {@code true} if the category is registered.
     */
    boolean hasCategory(String categoryName);

    /**
     * Removes a category. Throws an exception if this category is being used.
     *
     * @param categoryName the category name.
     * @return the removed category metadata or {@code null}.
     */
    CategoryMeta removeCategory(String categoryName);

    /**
     * Returns all categories.
     *
     * @return the categories.
     */
    List<CategoryMeta> getCategories();

    /**
     * Dynamically selects processors that are to assigned to the category.
     *
     * @param categoryName the category name.
     * @param processorType the processor type. If {@code null}, all processor types will be considered.
     * @param predicate the processor predicate.
     */
    void selectCategory(String categoryName, ProcessorType processorType, ProcessorPredicate predicate);

    /**
     * Dynamically selects processors of all types that are to assigned to the category.
     *
     * @param categoryName the category name.
     * @param predicate the processor predicate.
     */
    void selectCategory(String categoryName, ProcessorPredicate predicate);

    /**
     * Returns the action metadata.
     *
     * @param actionName the action name.
     * @return the action metadata.
     */
    ActionMeta getActionMeta(String actionName);

    /**
     * Returns the filter metadata.
     *
     * @param filterName the filter name.
     * @return the filter metadata.
     */
    FilterMeta getFilterMeta(String filterName);

    /**
     * Returns the trigger metadata.
     *
     * @param triggerName the trigger name.
     * @return the trigger metadata.
     */
    TriggerMeta getTriggerMeta(String triggerName);

    /**
     * Returns the correlator metadata.
     *
     * @param correlatorName the correlator name.
     * @return the correlator metadata.
     */
    CorrelatorMeta getCorrelatorMeta(String correlatorName);

    /**
     * Returns the rule metadata.
     *
     * @param ruleName the rule name.
     * @return the rule metadata.
     */
    RuleMeta getRuleMeta(String ruleName);

    /**
     * Registers a data type.
     *
     * @param <T> a data type.
     * @param registeredTypeName the registered type name.
     * @param typeSupplier the type supplier.
     */
    <T extends DataType<?>> void addType(String registeredTypeName, DataTypeSupplier<T> typeSupplier);

    /**
     * Returns a new instance of the registered data type. Throws exception if not found.
     *
     * @param <T> a data type.
     * @param registeredTypeName the registered type name.
     * @return the data type.
     */
    <T extends DataType<?>> T getType(String registeredTypeName);

    /**
     * Returns a new instance of the registered data type. Throws exception if not found.
     *
     * @param <T> a data type.
     * @param registeredTypeName the registered type name.
     * @param locationName the returned type location name (corresponds to the {@code withName} type method).
     * @return the data type.
     */
    <T extends DataType<?>> T getType(String registeredTypeName, String locationName);

    /**
     * Returns the unmodifiable map of registered data types.
     *
     * @return the unmodifiable map of registered data types.
     */
    @SuppressWarnings("rawtypes")
    Map<String, DataType> getTypes();

    /**
     * Returns {@code true} if the type is registered.
     *
     * @param registeredTypeName the type name.
     * @return {@code true} if the type is registered.
     */
    boolean hasType(String registeredTypeName);

    /**
     * Removes the registered data type.
     *
     * @param registeredTypeName the registered type name.
     * @return {@code true} if the data type was registered.
     */
    boolean removeType(String registeredTypeName);

    /**
     * Registers an event type as a record.
     *
     * @param eventTypeName the registered event type name.
     * @param dataType the data type.
     */
    void addEventType(String eventTypeName, RecordType dataType);

    /**
     * Returns an event type. Throws exception if not found.
     *
     * @param eventTypeName the registered event type name.
     * @return the event type.
     */
    RecordType getEventType(String eventTypeName);

    /**
     * Returns {@code true} if the event type is registered.
     *
     * @param eventTypeName the event type name.
     * @return {@code true} if the event type is registered.
     */
    boolean hasEventType(String eventTypeName);

    /**
     * Returns all registered event types.
     *
     * @return the registered event types.
     */
    Map<String, RecordType> getEventTypes();

    /**
     * Removes the registered event type.
     *
     * @param eventTypeName the registered event type name.
     * @return {@code true} if the event type was registered.
     */
    boolean removeEventType(String eventTypeName);
}
