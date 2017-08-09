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

package org.openksavi.sponge.engine;

import java.util.List;

import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.correlator.CorrelatorAdapterGroup;
import org.openksavi.sponge.engine.event.EventScheduler;
import org.openksavi.sponge.engine.processing.EventSetProcessorDurationStrategy;
import org.openksavi.sponge.filter.FilterAdapter;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBaseFileProvider;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.rule.RuleAdapterGroup;
import org.openksavi.sponge.spi.EngineModuleProvider;
import org.openksavi.sponge.spi.EventQueueProvider;
import org.openksavi.sponge.spi.KnowledgeBaseInterpreterFactoryProvider;
import org.openksavi.sponge.spi.ProcessingUnitProvider;
import org.openksavi.sponge.trigger.TriggerAdapter;

/**
 * The engine. This is the main interface of the system. An instance of this interface manages all main components.
 */
public interface Engine extends EngineModule {

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
     * Returns Plugin Manager.
     *
     * @return Plugin Manager.
     */
    PluginManager getPluginManager();

    /**
     * Returns Knowledge Base Manager.
     *
     * @return Knowledge Base Manager.
     */
    KnowledgeBaseManager getKnowledgeBaseManager();

    /**
     * Returns Configuration Manager.
     *
     * @return Configuration Manager.
     */
    ConfigurationManager getConfigurationManager();

    /**
     * Returns Processing Unit Manager.
     *
     * @return Processing Unit Manager.
     */
    ProcessingUnitManager getProcessingUnitManager();

    /**
     * Returns Event Queue Manager.
     *
     * @return Event Queue Manager.
     */
    EventQueueManager getEventQueueManager();

    /**
     * Returns Event Scheduler.
     *
     * @return Event Scheduler.
     */
    EventScheduler getEventScheduler();

    /**
     * Returns thread pool manager.
     *
     * @return thread pool manager.
     */
    ThreadPoolManager getThreadPoolManager();

    /**
     * Returns processor manager.
     *
     * @return processor manager.
     */
    ProcessorManager getProcessorManager();

    /**
     * Returns Statistics Manager.
     *
     * @return Statistics Manager.
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

    /**
     * Handles an error.
     *
     * @param processorAdapter processor adapter.
     * @param e exception.
     */
    void handleError(ProcessorAdapter<?> processorAdapter, Throwable e);

    /**
     * Handles an error.
     *
     * @param sourceName source name.
     * @param e exception.
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
     * Returns the engine description.
     *
     * @return the engine description.
     */
    String getDescription();

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
     * Returns engine operations.
     *
     * @return engine operations.
     */
    KnowledgeBaseEngineOperations getOperations();

    /**
     * Returns a knowledge base file provider.
     *
     * @return a knowledge base file provider.
     */
    KnowledgeBaseFileProvider getKnowledgeBaseFileProvider();

    /**
     * Adds OnStartup listener.
     *
     * @param onStartupListener OnStartup listener.
     */
    void addOnStartupListener(OnStartupListener onStartupListener);

    /**
     * Returns all OnStartup listeners.
     *
     * @return all OnStartup listeners.
     */
    List<OnStartupListener> getOnStartupListeners();

    /**
     * Removes OnStartup listener.
     *
     * @param onStartupListener OnStartup listener.
     * @return {@code true} if there was such OnStartup listener.
     */
    boolean removeOnStartupListener(OnStartupListener onStartupListener);

    /**
     * Adds OnShutdown listener.
     *
     * @param onShutdownListener OnShutdown listener.
     */
    void addOnShutdownListener(OnShutdownListener onShutdownListener);

    /**
     * Returns all OnShutdown listeners.
     *
     * @return all OnShutdown listeners.
     */
    List<OnShutdownListener> getOnShutdownListeners();

    /**
     * Removes OnShutdown listener.
     *
     * @param onShutdownListener OnShutdown listener.
     * @return {@code true} if there was such OnShutdown listener.
     */
    boolean removeOnShutdownListener(OnShutdownListener onShutdownListener);

    /**
     * Returns an exception handler.
     *
     * @return an exception handler.
     */
    public ExceptionHandler getExceptionHandler();

    /**
     * Sets an exception handler.
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
     * Sets a configuration file name.
     *
     * @param configurationFilename a configuration file name.
     */
    void setConfigurationFilename(String configurationFilename);

    /**
     * Sets a processing unit provider.
     *
     * @param processingUnitProvider a processing unit provider.
     */
    void setProcessingUnitProvider(ProcessingUnitProvider processingUnitProvider);

    /**
     * Sets a knowledge base interpreter factory providers.
     *
     * @param knowledgeBaseInterpreterFactoryProviders a knowledge base interpreter factory providers.
     */
    void setKnowledgeBaseInterpreterFactoryProviders(
            List<KnowledgeBaseInterpreterFactoryProvider> knowledgeBaseInterpreterFactoryProviders);

    /**
     * Sets a module provider.
     *
     * @param moduleProvider a module provider.
     */
    void setModuleProvider(EngineModuleProvider moduleProvider);

    /**
     * Sets an event queue provider.
     *
     * @param eventQueueProvider an event queue provider.
     */
    void setEventQueueProvider(EventQueueProvider eventQueueProvider);

    /**
     * Sets a knowledge base file provider.
     *
     * @param knowledgeBaseFileProvider a knowledge base file provider.
     */
    void setKnowledgeBaseFileProvider(KnowledgeBaseFileProvider knowledgeBaseFileProvider);

    /**
     * Returns a strategy for handling an event set processor duration timeout.
     *
     * @return a duration strategy.
     */
    EventSetProcessorDurationStrategy getEventSetProcessorDurationStrategy();

    /**
     * Sets a strategy for handling an event set processor duration timeout.
     *
     * @param durationStrategy a duration strategy.
     */
    void setEventSetProcessorDurationStrategy(EventSetProcessorDurationStrategy durationStrategy);
}
