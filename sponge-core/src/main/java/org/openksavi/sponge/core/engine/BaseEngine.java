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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.core.VersionInfo;
import org.openksavi.sponge.core.engine.processing.QueuedEventSetProcessorDurationStrategy;
import org.openksavi.sponge.core.kb.BaseKnowledgeBaseEngineOperations;
import org.openksavi.sponge.core.kb.DefaultKnowledgeBaseFileProvider;
import org.openksavi.sponge.core.spi.DefaultEngineModuleProvider;
import org.openksavi.sponge.core.spi.DefaultEventQueueProvider;
import org.openksavi.sponge.core.spi.DefaultProcessingUnitProvider;
import org.openksavi.sponge.core.util.ServiceLoaderUtils;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.correlator.CorrelatorAdapterGroup;
import org.openksavi.sponge.engine.ActionManager;
import org.openksavi.sponge.engine.ConfigurationManager;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.EngineModule;
import org.openksavi.sponge.engine.EngineParameters;
import org.openksavi.sponge.engine.EventQueueManager;
import org.openksavi.sponge.engine.ExceptionHandler;
import org.openksavi.sponge.engine.KnowledgeBaseManager;
import org.openksavi.sponge.engine.OnShutdownListener;
import org.openksavi.sponge.engine.OnStartupListener;
import org.openksavi.sponge.engine.PluginManager;
import org.openksavi.sponge.engine.ProcessingUnitManager;
import org.openksavi.sponge.engine.ProcessorManager;
import org.openksavi.sponge.engine.Session;
import org.openksavi.sponge.engine.StatisticsManager;
import org.openksavi.sponge.engine.ThreadPoolManager;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.engine.event.EventScheduler;
import org.openksavi.sponge.engine.processing.EventSetProcessorDurationStrategy;
import org.openksavi.sponge.engine.processing.MainProcessingUnit;
import org.openksavi.sponge.engine.processing.ProcessingUnit;
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
 * Base engine implementation.
 */
public class BaseEngine extends BaseEngineModule implements Engine {

    private static final Logger logger = LoggerFactory.getLogger(BaseEngine.class);

    /** Engine module provider. */
    protected EngineModuleProvider moduleProvider;

    /** Knowledge base interpreter factory providers. */
    protected List<KnowledgeBaseInterpreterFactoryProvider> knowledgeBaseInterpreterFactoryProviders;

    /** Event queue provider. */
    protected EventQueueProvider eventQueueProvider;

    /** Processing unit provider. */
    protected ProcessingUnitProvider processingUnitProvider;

    /** Knowledge base file provider. */
    protected KnowledgeBaseFileProvider knowledgeBaseFileProvider;

    /** Event scheduler. */
    protected EventScheduler eventScheduler;

    /** Event queue manager. */
    protected EventQueueManager eventQueueManager;

    /** Thread pool manager. */
    protected ThreadPoolManager threadPoolManager;

    /** Knowledge base manager. */
    protected KnowledgeBaseManager knowledgeBaseManager;

    /** Plugin manager. */
    protected PluginManager pluginManager;

    /** Configuration manager. */
    protected ConfigurationManager configurationManager;

    /** Statistics manager. */
    protected StatisticsManager statisticsManager;

    /** Session. In the current implementation there is only one session for one engine. */
    protected Session session = DefaultSession.createNewSession();

    /** Lock. */
    protected Lock lock = new ReentrantLock(true);

    /** Processor manager. */
    protected ProcessorManager processorManager;

    /** Processing Unit manager. */
    protected ProcessingUnitManager processingUnitManager;

    /** Action manager. */
    protected ActionManager actionManager;

    /** Input event queue. */
    protected EventQueue inputQueue;

    /** Remembered exception. */
    private AtomicReference<Throwable> rememberedException = new AtomicReference<>();

    /** Configuration file name. */
    private String configurationFilename;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    /** Knowledge base specific engine operations. */
    private KnowledgeBaseEngineOperations operations;

    /** Exception handler. */
    private ExceptionHandler exceptionHandler = new LoggingExceptionHandler();

    /** OnStartup listeners. */
    private List<OnStartupListener> onStartupListeners = new ArrayList<>();

    /** OnShutdown listeners. */
    private List<OnShutdownListener> onShutdownListeners = new ArrayList<>();

    /** Version info. */
    private VersionInfo versionInfo = new VersionInfo();

    /** Default parameters. */
    private EngineParameters defaultParameters = new DefaultEngineParameters();

    /**
     * Strategy for handling an event set processor duration timeout. Default implementation is
     * {@code QueuedEventSetProcessorDurationStrategy}.
     */
    private EventSetProcessorDurationStrategy durationStrategy = new QueuedEventSetProcessorDurationStrategy();

    /**
     * Creates a new engine. Engine module provider will be loaded using Java ServiceLoader.
     */
    public BaseEngine() {
        setEngine(this);
    }

    /**
     * Returns the module provider.
     *
     * @return the moduleProvider
     */
    public EngineModuleProvider getModuleProvider() {
        return moduleProvider;
    }

    /**
     * Sets the module provider.
     *
     * @param moduleProvider the moduleProvider to set.
     */
    @Override
    public void setModuleProvider(EngineModuleProvider moduleProvider) {
        this.moduleProvider = moduleProvider;
    }

    public List<KnowledgeBaseInterpreterFactoryProvider> getKnowledgeBaseInterpreterFactoryProviders() {
        return knowledgeBaseInterpreterFactoryProviders;
    }

    @Override
    public void setKnowledgeBaseInterpreterFactoryProviders(
            List<KnowledgeBaseInterpreterFactoryProvider> knowledgeBaseInterpreterFactoryProviders) {
        this.knowledgeBaseInterpreterFactoryProviders = knowledgeBaseInterpreterFactoryProviders;
    }

    @Override
    public void setEventQueueProvider(EventQueueProvider eventQueueProvider) {
        this.eventQueueProvider = eventQueueProvider;
    }

    @Override
    public void setProcessingUnitProvider(ProcessingUnitProvider processingUnitProvider) {
        this.processingUnitProvider = processingUnitProvider;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public String getConfigurationFilename() {
        return configurationFilename;
    }

    @Override
    public void setConfigurationFilename(String configurationFilename) {
        this.configurationFilename = configurationFilename;
    }

    /**
     * Initializes the engine by creating providers and engine modules.
     */
    @Override
    public void init() {
        lock.lock();
        try {
            if (initialized.get()) {
                return;
            }

            ensureProvidersSet();

            configurationManager = moduleProvider.createConfigurationManager(this, configurationFilename);
            pluginManager = moduleProvider.createPluginManager(this);
            knowledgeBaseManager = moduleProvider.createKnowledgeBaseManager(this);
            threadPoolManager = moduleProvider.createThreadPoolManager(this);
            processorManager = moduleProvider.createProcessorManager(this);
            actionManager = moduleProvider.createActionManager(this);
            statisticsManager = moduleProvider.createStatisticsManager(this);
            processingUnitManager = moduleProvider.createProcessingUnitManager(this);
            eventQueueManager = moduleProvider.createEventQueueManager(this);

            operations = new BaseKnowledgeBaseEngineOperations(this, knowledgeBaseManager.getDefaultKnowledgeBase());

            initialized.set(true);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Ensures that all providers are set.
     */
    protected void ensureProvidersSet() {
        if (moduleProvider == null) {
            moduleProvider = ServiceLoaderUtils.loadService(EngineModuleProvider.class);

            if (moduleProvider == null) {
                moduleProvider = new DefaultEngineModuleProvider();
            }
        }

        if (knowledgeBaseInterpreterFactoryProviders == null) {
            knowledgeBaseInterpreterFactoryProviders = ServiceLoaderUtils.loadServices(KnowledgeBaseInterpreterFactoryProvider.class);
        }

        if (eventQueueProvider == null) {
            eventQueueProvider = new DefaultEventQueueProvider();
        }

        if (processingUnitProvider == null) {
            processingUnitProvider = new DefaultProcessingUnitProvider();
        }

        if (knowledgeBaseFileProvider == null) {
            knowledgeBaseFileProvider = new DefaultKnowledgeBaseFileProvider();
        }
    }

    /**
     * Starts up the engine.
     */
    @Override
    public void doStartup() {
        lock.lock();
        try {
            logger.info("Starting up {}", getDescription());

            init();

            try {
                clearRememberedException();

                // Read the configuration.
                configurationManager.startup();

                // Bind and configure engine modules (including plugins).
                configureEngineModules();

                // Create event queues. They are not read from yet. Note that here the Input Event Queue is created.
                eventQueueManager.startup();

                // Creates an event scheduler.
                eventScheduler.startup();

                // Read knowledge bases files and invoke onInit and onLoad for each knowledge base.
                knowledgeBaseManager.startup();

                // Invoke configure, init and invoke onStartup for each plugin.
                pluginManager.startup();

                // Invoke OnStartup listeners.
                onStartupListeners.forEach(listener -> listener.onStartup());

                // Invoke onStartup for each knowledge base.
                knowledgeBaseManager.onStartup();

                // Starts Thread Pool Manager only. Note that thread pools are not started here yet.
                threadPoolManager.startup();

                // Start Main Processing Unit and Filter Processing Unit thread pools. Note that the Filter Processing Unit thread
                // will be started as the last, because it listens directly to the Input Event Queue and in fact starts all processing.
                processingUnitManager.startup();

                logger.info("{} is running", getDescription());
            } catch (Throwable e) {
                safelyShutdownIfStartupError(eventScheduler, threadPoolManager, processingUnitManager);
                throw Utils.wrapException("startup", e);
            }
        } finally {
            lock.unlock();
        }
    }

    protected void safelyShutdownIfStartupError(EngineModule... modules) {
        Stream.of(modules).forEach(module -> {
            try {
                if (module != null) {
                    module.shutdown();
                }
            } catch (Throwable ex) {
                logger.warn(module.getName(), ex);
            }
        });
    }

    /**
     * Setup engine name.
     */
    protected void setupEngineName() {
        if (getName() == null && configurationManager.getEngineName() != null) {
            setName(configurationManager.getEngineName());
        }
    }

    /**
     * Shuts down the engine.
     */
    @Override
    public void doShutdown() {
        lock.lock();
        try {
            logger.info("Shutting down {}", getDescription());

            AtomicReference<Throwable> exceptionHolder = new AtomicReference<>(null);

            safelyShutdownModule(processingUnitManager, exceptionHolder);
            safelyShutdownModule(threadPoolManager, exceptionHolder);

            knowledgeBaseManager.onShutdown();

            onShutdownListeners.forEach(listener -> listener.onShutdown());

            safelyShutdownModule(pluginManager, exceptionHolder);
            safelyShutdownModule(knowledgeBaseManager, exceptionHolder);
            safelyShutdownModule(eventScheduler, exceptionHolder);
            safelyShutdownModule(eventQueueManager, exceptionHolder);

            if (exceptionHolder.get() != null) {
                throw exceptionHolder.get();
            }

            logger.info("{} is terminated", getDescription());
        } catch (Throwable e) {
            throw Utils.wrapException("shutdown", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void requestShutdown() {
        Utils.executeConcurrentlyOnce(() -> shutdown());
    }

    private void safelyShutdownModule(EngineModule module, AtomicReference<Throwable> exceptionHolder) {
        try {
            module.shutdown();
        } catch (Throwable e) {
            if (exceptionHolder.get() == null) {
                exceptionHolder.set(e);
            }
        }
    }

    /**
     * Configures engine modules.
     */
    protected void configureEngineModules() {
        // Setup engine name.
        setupEngineName();

        // Register interpreter factories for scripting languages.
        knowledgeBaseManager.setKnowledgeBaseInterpreterFactoryProviders(knowledgeBaseInterpreterFactoryProviders);

        // Create event queues.
        inputQueue = eventQueueProvider.getInputQueue();
        EventQueue mainProcessingQueue = eventQueueProvider.getMainQueue();
        EventQueue outputQueue = eventQueueProvider.getOutputQueue();

        // Add event queues to the queue manager.
        eventQueueManager.setInputEventQueue(inputQueue);
        eventQueueManager.setMainEventQueue(mainProcessingQueue);
        eventQueueManager.setOutputEventQueue(outputQueue);

        // Set input event queue capacity.
        inputQueue.setCapacity(configurationManager.getEventQueueCapacity());
        mainProcessingQueue.setCapacity(getDefaultParameters().getMainEventQueueCapacity());

        // Create event scheduler.
        eventScheduler = moduleProvider.createEventScheduler(this, inputQueue);

        // Create processing units.
        processingUnitManager
                .setFilterProcessingUnit(processingUnitProvider.createFilterProcessingUnit(this, inputQueue, mainProcessingQueue));
        processingUnitManager
                .setMainProcessingUnit(processingUnitProvider.createMainProcessingUnit(this, mainProcessingQueue, outputQueue));

        if (configurationManager.getRootConfig() != null) {
            pluginManager.configure(configurationManager.getRootConfig());
        }

        if (configurationManager.getRootConfig() != null) {
            knowledgeBaseManager.configure(configurationManager.getRootConfig());
        }
    }

    /**
     * Returns Plugin Manager.
     *
     * @return Plugin Manager.
     */
    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * Returns knowledge base manager.
     *
     * @return knowledge base Manager.
     */
    @Override
    public KnowledgeBaseManager getKnowledgeBaseManager() {
        return knowledgeBaseManager;
    }

    /**
     * Returns Configuration Manager.
     *
     * @return Configuration Manager.
     */
    @Override
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    /**
     * Returns Event Scheduler.
     *
     * @return Event Scheduler.
     */
    @Override
    public EventScheduler getEventScheduler() {
        return eventScheduler;
    }

    /**
     * Returns Statistics Manager.
     *
     * @return Statistics Manager.
     */
    @Override
    public StatisticsManager getStatisticsManager() {
        return statisticsManager;
    }

    /**
     * Returns Processing Unit Manager.
     *
     * @return Processing Unit Manager.
     */
    @Override
    public ProcessingUnitManager getProcessingUnitManager() {
        return processingUnitManager;
    }

    /**
     * Returns Thread Pool Manager.
     *
     * @return Thread Pool Manager.
     */
    @Override
    public ThreadPoolManager getThreadPoolManager() {
        return threadPoolManager;
    }

    /**
     * Returns the list of filter adapters.
     *
     * @return the list of filter adapters.
     */
    @Override
    public List<FilterAdapter> getFilters() {
        return new ArrayList<>(getFilterProcessingUnit().getRegisteredProcessorAdapterMap().values());
    }

    /**
     * Returns the list of trigger adapters.
     *
     * @return the list of trigger adapters.
     */
    @Override
    public List<TriggerAdapter> getTriggers() {
        return getMainProcessingUnit().getTriggerAdapters();
    }

    /**
     * Returns the list of rule adapter groups.
     *
     * @return the list of rule adapter groups.
     */
    @Override
    public List<RuleAdapterGroup> getRuleGroups() {
        return getMainProcessingUnit().getRuleAdapterGroups();
    }

    /**
     * Returns the list of correlator adapter groups.
     *
     * @return the list of correlator adapter groups.
     */
    @Override
    public List<CorrelatorAdapterGroup> getCorrelatorGroups() {
        return getMainProcessingUnit().getCorrelatorAdapterGroups();
    }

    /**
     * Returns the list of action adapters.
     *
     * @return the list of action adapters.
     */
    @Override
    public List<ActionAdapter> getActions() {
        return new ArrayList<>(actionManager.getRegisteredActionAdapterMap().values());
    }

    /**
     * Reloads script-based knowledge bases.
     */
    @Override
    public void reload() {
        lock.lock();
        try {
            pluginManager.onBeforeReload();
            knowledgeBaseManager.reload();
            pluginManager.onAfterReload();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void requestReload() {
        Utils.executeConcurrentlyOnce(() -> reload());
    }

    /**
     * Processes error.
     *
     * @param processorAdapter processor adapter.
     * @param e exception.
     */
    @Override
    public void handleError(ProcessorAdapter<?> processorAdapter, Throwable e) {
        handleError((processorAdapter != null && processorAdapter.getName() != null) ? processorAdapter.getName() : "unknown",
                processorAdapter, e);
    }

    /**
     * Processes error.
     *
     * @param sourceName source name.
     * @param e exception.
     */
    @Override
    public void handleError(String sourceName, Throwable e) {
        handleError(sourceName, null, e);
    }

    protected void handleError(String sourceName, Object sourceObject, Throwable exception) {
        tryRememberException(exception);

        exceptionHandler.handleException(exception,
                new GenericExceptionContext(this, ObjectUtils.defaultIfNull(Utils.getSourceName(exception), sourceName), sourceObject));
    }

    /**
     * Returns the list of plugins.
     *
     * @return the list of plugins.
     */
    @Override
    public List<Plugin> getPlugins() {
        return pluginManager.getPlugins();
    }

    /**
     * Returns the engine version.
     *
     * @return the engine version.
     */
    @Override
    public String getVersion() {
        return versionInfo.getVersion();
    }

    /**
     * Returns the engine description.
     *
     * @return the engine description.
     */
    @Override
    public String getDescription() {
        return versionInfo.getDescription(getName());
    }

    /**
     * Remembers the first exception.
     *
     * @param e exception.
     */
    protected void tryRememberException(Throwable e) {
        rememberedException.updateAndGet(previous -> previous != null ? previous : e);
    }

    /**
     * Clears remembered exception.
     */
    public void clearRememberedException() {
        rememberedException.set(null);
    }

    @Override
    public EventQueueManager getEventQueueManager() {
        return eventQueueManager;
    }

    public ProcessingUnit<FilterAdapter> getFilterProcessingUnit() {
        return processingUnitManager.getFilterProcessingUnit();
    }

    public MainProcessingUnit getMainProcessingUnit() {
        return processingUnitManager.getMainProcessingUnit();
    }

    @Override
    public ProcessorManager getProcessorManager() {
        return processorManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public EventQueue getInputQueue() {
        return inputQueue;
    }

    public AtomicReference<Throwable> getRememberedException() {
        return rememberedException;
    }

    @Override
    public boolean isError() {
        return rememberedException.get() != null;
    }

    @Override
    public Throwable getError() {
        return rememberedException.get();
    }

    @Override
    public KnowledgeBaseEngineOperations getOperations() {
        return operations;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public KnowledgeBaseFileProvider getKnowledgeBaseFileProvider() {
        return knowledgeBaseFileProvider;
    }

    @Override
    public void setKnowledgeBaseFileProvider(KnowledgeBaseFileProvider knowledgeBaseFileProvider) {
        this.knowledgeBaseFileProvider = knowledgeBaseFileProvider;
    }

    @Override
    public void addOnStartupListener(OnStartupListener onStartupListener) {
        onStartupListeners.add(onStartupListener);
    }

    @Override
    public boolean removeOnStartupListener(OnStartupListener onStartupListener) {
        return onStartupListeners.remove(onStartupListener);
    }

    @Override
    public List<OnStartupListener> getOnStartupListeners() {
        return onStartupListeners;
    }

    @Override
    public void addOnShutdownListener(OnShutdownListener onShutdownListener) {
        onShutdownListeners.add(onShutdownListener);
    }

    @Override
    public boolean removeOnShutdownListener(OnShutdownListener onShutdownListener) {
        return onShutdownListeners.remove(onShutdownListener);
    }

    @Override
    public List<OnShutdownListener> getOnShutdownListeners() {
        return onShutdownListeners;
    }

    @Override
    public EngineParameters getDefaultParameters() {
        return defaultParameters;
    }

    @Override
    public EventSetProcessorDurationStrategy getEventSetProcessorDurationStrategy() {
        return durationStrategy;
    }

    @Override
    public void setEventSetProcessorDurationStrategy(EventSetProcessorDurationStrategy durationStrategy) {
        this.durationStrategy = durationStrategy;
    }
}
