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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.CategoryMeta;
import org.openksavi.sponge.EventProcessorAdapter;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.core.VersionInfo;
import org.openksavi.sponge.core.engine.processing.QueuedEventSetProcessorDurationStrategy;
import org.openksavi.sponge.core.kb.BaseKnowledgeBaseEngineOperations;
import org.openksavi.sponge.core.kb.DefaultKnowledgeBaseFileProvider;
import org.openksavi.sponge.core.spi.DefaultEngineModuleProvider;
import org.openksavi.sponge.core.spi.DefaultEventQueueProvider;
import org.openksavi.sponge.core.spi.DefaultProcessingUnitProvider;
import org.openksavi.sponge.core.util.RegexPatternMatcher;
import org.openksavi.sponge.core.util.ServiceLoaderUtils;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.correlator.CorrelatorAdapterGroup;
import org.openksavi.sponge.correlator.CorrelatorMeta;
import org.openksavi.sponge.engine.ActionManager;
import org.openksavi.sponge.engine.ConfigurationManager;
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
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.Session;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.StatisticsManager;
import org.openksavi.sponge.engine.ThreadPoolManager;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.engine.event.EventScheduler;
import org.openksavi.sponge.engine.processing.EventSetProcessorDurationStrategy;
import org.openksavi.sponge.engine.processing.MainProcessingUnit;
import org.openksavi.sponge.engine.processing.ProcessingUnit;
import org.openksavi.sponge.event.EventName;
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
import org.openksavi.sponge.util.SpongeApiUtils;

/**
 * Base Sponge engine implementation.
 */
public class BaseSpongeEngine extends BaseEngineModule implements SpongeEngine {

    private static final Logger logger = LoggerFactory.getLogger(BaseSpongeEngine.class);

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

    /** Configuration filename. */
    private String configurationFilename;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    /** Endless loop mode or run once mode. */
    private AtomicBoolean endlessLoopMode = new AtomicBoolean(true);

    /** Knowledge base specific engine operations. */
    private KnowledgeBaseEngineOperations operations;

    /** Exception handler. */
    private ExceptionHandler exceptionHandler = new LoggingExceptionHandler();

    /** OnStartup listeners. */
    private List<OnStartupListener> onStartupListeners = new CopyOnWriteArrayList<>();

    /** OnShutdown listeners. */
    private List<OnShutdownListener> onShutdownListeners = new CopyOnWriteArrayList<>();

    /** Version info. */
    private VersionInfo versionInfo = new VersionInfo();

    /** Default parameters. */
    private EngineParameters defaultParameters = new DefaultEngineParameters();

    /**
     * Strategy for handling an event set processor duration timeout. Default implementation is
     * {@code QueuedEventSetProcessorDurationStrategy}.
     */
    private EventSetProcessorDurationStrategy durationStrategy = new QueuedEventSetProcessorDurationStrategy();

    /** Pattern matcher. */
    private PatternMatcher patternMatcher = new RegexPatternMatcher();

    /** Registered categories. The underlying map preserves the insertion order. */
    private Map<String, CategoryMeta> categories = Collections.synchronizedMap(new LinkedHashMap<>());

    /** Registered types. */
    @SuppressWarnings("rawtypes")
    private Map<String, DataTypeSupplier> types = new ConcurrentSkipListMap<>();

    /** Registered event types. */
    private Map<String, RecordType> eventTypes = new ConcurrentSkipListMap<>();

    /**
     * Creates a new engine. Engine module provider will be loaded using Java ServiceLoader.
     */
    public BaseSpongeEngine() {
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

    @Override
    public synchronized void startup() {
        // The default is endless loop mode.
        endlessLoopMode.set(true);

        super.startup();

        if (!endlessLoopMode.get()) {
            // Run once mode.
            shutdown();
        }
    }

    /**
     * Starts up the engine.
     */
    @Override
    public void doStartup() {
        lock.lock();
        try {
            logger.info("Starting up {}", getInfo());

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

                // Sends the first event: startup.
                operations.event(EventName.STARTUP).send();

                // Read knowledge bases files and invoke onInit and onLoad for each knowledge base.
                knowledgeBaseManager.startup();

                // Invoke configure, init and invoke onStartup for each plugin.
                pluginManager.startup();

                // Invoke OnStartup listeners.
                onStartupListeners.forEach(listener -> listener.onStartup());

                // Invoke onStartup for each knowledge base.
                knowledgeBaseManager.onStartup();

                if (knowledgeBaseManager.onRun()) {
                    // Starts Thread Pool Manager only. Note that thread pools are not started here yet.
                    threadPoolManager.startup();

                    // Start Main Processing Unit and Filter Processing Unit thread pools. Note that the Filter Processing Unit thread
                    // will be started as the last, because it listens directly to the Input Event Queue and in fact starts all processing.
                    processingUnitManager.startup();

                    logger.debug("Sponge is running");
                } else {
                    endlessLoopMode.set(false);

                    logger.debug("Sponge completed a run once mode");
                }
            } catch (Throwable e) {
                safelyShutdownIfStartupError(processingUnitManager, threadPoolManager, knowledgeBaseManager, pluginManager, eventScheduler,
                        eventQueueManager, configurationManager);
                throw SpongeUtils.wrapException("startup", e);
            }
        } finally {
            lock.unlock();
        }
    }

    protected void safelyShutdownIfStartupError(EngineModule... modules) {
        Stream.of(modules).forEachOrdered(module -> {
            try {
                if (module != null && module.isRunning()) {
                    module.shutdown();
                }
            } catch (Throwable ex) {
                logger.warn(module.getName(), ex);
            }
        });
    }

    /**
     * Setup the name, label and description.
     */
    protected void setupEngineDescriptive() {
        if (getName() == null && configurationManager.getEngineName() != null) {
            setName(configurationManager.getEngineName());
        }

        if (getLabel() == null && configurationManager.getEngineLabel() != null) {
            setLabel(configurationManager.getEngineLabel());
        }

        if (getDescription() == null && configurationManager.getEngineDescription() != null) {
            setDescription(configurationManager.getEngineDescription());
        }
    }

    /**
     * Shuts down the engine.
     */
    @Override
    public void doShutdown() {
        lock.lock();
        try {
            logger.info("Shutting down Sponge");

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

            logger.debug("Sponge is terminated");
        } catch (Throwable e) {
            throw SpongeUtils.wrapException("shutdown", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void requestShutdown() {
        SpongeUtils.executeConcurrentlyOnce(this, () -> shutdown(), "shutdown");
    }

    private void safelyShutdownModule(EngineModule module, AtomicReference<Throwable> exceptionHolder) {
        try {
            if (module != null) {
                module.shutdown();
            }
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
        setupEngineDescriptive();

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

        // Add pre-config plugins as first.
        configurationManager.getPreConfigPlugins().forEach(plugin -> pluginManager.addPlugin(plugin));
        if (configurationManager.getRootConfig() != null) {
            pluginManager.configure(configurationManager.getRootConfig());
        }
        // Add post-config plugins as last.
        configurationManager.getPostConfigPlugins().forEach(plugin -> pluginManager.addPlugin(plugin));

        // Add pre-config knowledge bases as first.
        configurationManager.getPreConfigKnowledgeBases().forEach(knowledgeBase -> knowledgeBaseManager.addKnowledgeBase(knowledgeBase));
        if (configurationManager.getRootConfig() != null) {
            knowledgeBaseManager.configure(configurationManager.getRootConfig());
        }
        // Add post-config knowledge bases as last.
        configurationManager.getPostConfigKnowledgeBases().forEach(knowledgeBase -> knowledgeBaseManager.addKnowledgeBase(knowledgeBase));
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

    @Override
    @SuppressWarnings("rawtypes")
    public List<ProcessorAdapter> getProcessors() {
        List<ProcessorAdapter> adapters = new ArrayList<>();
        adapters.addAll(getActions());
        adapters.addAll(getFilters());
        adapters.addAll(getTriggers());
        adapters.addAll(getRuleGroups());
        adapters.addAll(getCorrelatorGroups());

        return adapters;
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
        SpongeUtils.executeConcurrentlyOnce(this, () -> reload(), "reload");
    }

    /**
     * Processes error.
     *
     * @param processorAdapter processor adapter.
     * @param e exception.
     */
    @Override
    public void handleError(ProcessorAdapter<?> processorAdapter, Throwable e) {
        handleError((processorAdapter != null && processorAdapter.getMeta().getName() != null)
                ? SpongeUtils.getProcessorQualifiedName(processorAdapter).toString() : "unknown", processorAdapter, e);
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

        exceptionHandler.handleException(exception, new GenericExceptionContext(this,
                ObjectUtils.defaultIfNull(SpongeUtils.getSourceName(exception), sourceName), sourceObject));
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
     * Returns the engine info.
     *
     * @return the engine info.
     */
    @Override
    public String getInfo() {
        return versionInfo.getInfo(getName());
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

    @Override
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
    public void clearError() {
        clearRememberedException();
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

    @Override
    public PatternMatcher getPatternMatcher() {
        return patternMatcher;
    }

    @Override
    public void setPatternMatcher(PatternMatcher patternMatcher) {
        this.patternMatcher = patternMatcher;
    }

    @Override
    public void addCategory(CategoryMeta categoryMeta) {
        categories.put(categoryMeta.getName(), categoryMeta);
    }

    @Override
    public void addCategories(CategoryMeta... categoryMeta) {
        Arrays.stream(categoryMeta).forEach(this::addCategory);
    }

    @Override
    public CategoryMeta getCategory(String categoryName) {
        return categories.get(categoryName);
    }

    @Override
    public boolean hasCategory(String categoryName) {
        return categories.containsKey(categoryName);
    }

    @Override
    public CategoryMeta removeCategory(String categoryName) {
        Validate.isTrue(
                !getProcessorManager().getAllProcessorAdapters().stream().map(adapter -> adapter.getMeta().getCategory())
                        .anyMatch(adapterCategory -> adapterCategory != null && adapterCategory.equals(categoryName)),
                "The category %s is being used and can't be removed", categoryName);
        return categories.remove(categoryName);
    }

    @Override
    public List<CategoryMeta> getCategories() {
        return new ArrayList<>(categories.values());
    }

    @Override
    public void selectCategory(String categoryName, ProcessorType processorType, ProcessorPredicate predicate) {
        Validate.notNull(getCategory(categoryName), "Category %s not found", categoryName);

        getProcessors().stream()
                .filter(adapter -> (processorType == null || processorType == adapter.getType()) && predicate.test(adapter.getProcessor()))
                .forEach(adapter -> adapter.getMeta().setCategory(categoryName));
    }

    @Override
    public void selectCategory(String categoryName, ProcessorPredicate predicate) {
        selectCategory(categoryName, null, predicate);
    }

    @Override
    public ActionMeta getActionMeta(String actionName) {
        ActionAdapter adapter = getActionManager().getActionAdapter(actionName);
        Validate.isTrue(adapter != null, "Action %s not found", actionName);

        return adapter.getMeta();
    }

    @Override
    public FilterMeta getFilterMeta(String filterName) {
        FilterAdapter adapter = getFilterProcessingUnit().getRegisteredProcessorAdapterMap().get(filterName);
        Validate.isTrue(adapter != null, "Filter %s not found", filterName);

        return adapter.getMeta();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public TriggerMeta getTriggerMeta(String triggerName) {
        EventProcessorAdapter adapter = getMainProcessingUnit().getRegisteredProcessorAdapterMap().get(triggerName);
        Validate.isTrue(adapter != null, "Trigger %s not found", adapter);
        Validate.isTrue(adapter.getType() == ProcessorType.TRIGGER, "Processor %s is not a trigger", triggerName);

        return (TriggerMeta) adapter.getMeta();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public CorrelatorMeta getCorrelatorMeta(String correlatorName) {
        EventProcessorAdapter adapter = getMainProcessingUnit().getRegisteredProcessorAdapterMap().get(correlatorName);
        Validate.isTrue(adapter != null, "Correlator %s not found", adapter);
        Validate.isTrue(adapter.getType() == ProcessorType.CORRELATOR_GROUP, "Processor %s is not a correlator", correlatorName);

        return (CorrelatorMeta) adapter.getMeta();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public RuleMeta getRuleMeta(String ruleName) {
        EventProcessorAdapter adapter = getMainProcessingUnit().getRegisteredProcessorAdapterMap().get(ruleName);
        Validate.isTrue(adapter != null, "Rule %s not found", adapter);
        Validate.isTrue(adapter.getType() == ProcessorType.RULE_GROUP, "Processor %s is not a rule", ruleName);

        return (RuleMeta) adapter.getMeta();
    }

    @Override
    public <T extends DataType<?>> void addType(String registeredTypeName, DataTypeSupplier<T> typeSupplier) {
        Validate.notNull(typeSupplier, "The supplier for type %s is null", registeredTypeName);

        DataType<?> prototype = typeSupplier.supply();
        Validate.notNull(prototype, "The supplied type %s is null", registeredTypeName);
        SpongeUtils.validateType(prototype, registeredTypeName);

        // For verification only.
        SpongeUtils.setupType(prototype);

        types.put(registeredTypeName, typeSupplier);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataType<?>> T getType(String registeredTypeName) {
        DataTypeSupplier<?> supplier = Validate.notNull(types.get(registeredTypeName), "Type %s is not registered", registeredTypeName);
        DataType<?> type = Validate.notNull(supplier.supply(), "The supplied type %s is null", registeredTypeName);

        return (T) setupRegisteredTypeInstance(registeredTypeName, type);
    }

    protected <T extends DataType<?>> T setupRegisteredTypeInstance(String registeredTypeName, T type) {
        // Set the registered type name in the new instance.
        type.setRegisteredType(registeredTypeName);

        // A type is set up every get to allow dynamic behavior.
        SpongeUtils.setupType(type);

        return type;
    }

    @Override
    public <T extends DataType<?>> T getType(String registeredTypeName, String locationName) {
        T type = getType(registeredTypeName);
        type.setName(locationName);

        return type;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, DataType> getTypes() {
        return Collections.unmodifiableMap(types.entrySet().stream().collect(SpongeApiUtils.collectorToLinkedMap(entry -> entry.getKey(),
                entry -> setupRegisteredTypeInstance(entry.getKey(), entry.getValue().supply()))));
    }

    @Override
    public boolean hasType(String registeredTypeName) {
        return types.containsKey(registeredTypeName);
    }

    @Override
    public boolean removeType(String registeredTypeName) {
        return types.remove(registeredTypeName) != null;
    }

    @Override
    public void addEventType(String eventTypeName, RecordType dataType) {
        eventTypes.put(eventTypeName, dataType);
    }

    @Override
    public RecordType getEventType(String eventTypeName) {
        return Validate.notNull(eventTypes.get(eventTypeName), "Event type %s is not registered", eventTypeName);
    }

    @Override
    public boolean hasEventType(String eventTypeName) {
        return eventTypes.containsKey(eventTypeName);
    }

    @Override
    public Map<String, RecordType> getEventTypes() {
        return Collections.unmodifiableMap(eventTypes);
    }

    @Override
    public boolean removeEventType(String eventTypeName) {
        return eventTypes.remove(eventTypeName) != null;
    }
}
