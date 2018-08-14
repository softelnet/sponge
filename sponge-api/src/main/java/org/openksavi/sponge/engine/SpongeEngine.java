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
import org.openksavi.sponge.util.PatternMatcher;

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
     * Sets the configuration file name.
     *
     * @param configurationFilename the configuration file name.
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
}
