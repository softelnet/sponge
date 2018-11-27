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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.configuration2.resolver.DefaultEntityResolver;
import org.apache.commons.configuration2.tree.MergeCombiner;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.config.PropertyEntry;
import org.openksavi.sponge.core.config.CommonsConfiguration;
import org.openksavi.sponge.core.config.FallbackBasePathLocationStrategy;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.ConfigurationManager;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.EventClonePolicy;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.plugin.Plugin;

/**
 * Default configuration manager. It contains methods for accessing configuration parameters.
 */
public class DefaultConfigurationManager extends BaseEngineModule implements ConfigurationManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConfigurationManager.class);

    /** Configuration filename. */
    private String configurationFilename;

    /** The engine home directory. */
    private String home;

    /** The configuration file URL. */
    private URL configurationFileUrl;

    /** The engine name. */
    private String engineName;

    /** The engine display name. */
    private String engineDisplayName;

    /** The engine description. */
    private String engineDescription;

    /** Root configuration. */
    private CommonsConfiguration rootConfig;

    /** Engine configuration. */
    private CommonsConfiguration engineConfig;

    /** Properties from the properties file. */
    private PropertiesConfiguration propertiesConfig;

    /** The number of the Main Processing Unit worker threads. */
    private Integer mainProcessingUnitThreadCount;

    /** Event clone policy. */
    private EventClonePolicy eventClonePolicy;

    /** Event queue capacity. */
    private Integer eventQueueCapacity;

    /** The number of duration executor threads. */
    private Integer durationThreadCount;

    /** The number of threads used by an event set processor asynchronous executor. */
    private Integer asyncEventSetProcessorExecutorThreadCount;

    /** The event set processor default synchronous flag. */
    private Boolean eventSetProcessorDefaultSynchronous;

    /** Auto-enable processors. */
    private Boolean autoEnable;

    /** Executor shutdown timeout (in milliseconds). */
    private Long executorShutdownTimeout;

    /** Properties map. */
    private Map<String, PropertyEntry> properties = new LinkedHashMap<>();

    /** Plugins added manually before the config file. */
    protected List<Plugin> preConfigPlugins = new ArrayList<>();

    /** Plugins added manually after the config file. */
    protected List<Plugin> postConfigPlugins = new ArrayList<>();

    /** Knowledge bases added manually before the config file. */
    protected List<KnowledgeBase> preConfigKnowledgeBases = new ArrayList<>();

    /** Knowledge bases added manually after the config file. */
    protected List<KnowledgeBase> postConfigKnowledgeBases = new ArrayList<>();

    /**
     * Creates a new configuration manager.
     *
     * @param engine the engine.
     * @param configurationFilename configuration file name.
     */
    public DefaultConfigurationManager(SpongeEngine engine, String configurationFilename) {
        super("ConfigurationManager", engine);
        this.configurationFilename = configurationFilename;
    }

    /**
     * Starts up this managed entity.
     */
    @Override
    public void doStartup() {
        home = System.getProperty(ConfigurationConstants.PROP_HOME);

        readConfiguration();

        // Add properties from an optional properties file (has the same base name as the configuration file).
        setupPropertiesFromPropertiesFile();
        // Add manually predefined properties.
        setupPredefinedProperties();
        // Add all system properties.
        rootConfig.setVariables(System.getProperties());
        // Add properties defined in the XML configuration.
        setupXmlProperties();
        // Apply system properties if there are any.
        applySystemProperties();
        // Apply variable properties if there are any.
        applyVariableProperties();

        engineConfig = rootConfig.getChildConfiguration(ConfigurationConstants.TAG_ENGINE_CONFIG);
        engineName = engineConfig.getAttribute(ConfigurationConstants.ENGINE_ATTRIBUTE_NAME, null);
        engineDisplayName = engineConfig.getAttribute(ConfigurationConstants.ENGINE_ATTRIBUTE_DISPLAY_NAME, null);
        engineDescription = engineConfig.getString(ConfigurationConstants.TAG_ENGINE_DESCRIPTION, null);
        setupEngineParameters();

        if (home == null) {
            home = getProperty(ConfigurationConstants.PROP_HOME);
        }

        logger.debug(toString());
    }

    /**
     * Initializes cached configuration parameters.
     */
    private void setupEngineParameters() {
        if (mainProcessingUnitThreadCount == null) {
            mainProcessingUnitThreadCount = engineConfig.getInteger(ConfigurationConstants.TAG_ENGINE_MAIN_PROCESSING_UNIT_THREAD_COUNT,
                    getEngine().getDefaultParameters().getMainProcessingUnitThreadCount());
        }

        if (eventClonePolicy == null) {
            eventClonePolicy = EventClonePolicy.valueOf(engineConfig.getString(ConfigurationConstants.TAG_ENGINE_EVENT_CLONE_POLICY,
                    getEngine().getDefaultParameters().getEventClonePolicy().name()).toUpperCase());
        }

        if (eventQueueCapacity == null) {
            eventQueueCapacity = engineConfig.getInteger(ConfigurationConstants.TAG_ENGINE_EVENT_QUEUE_CAPACITY,
                    getEngine().getDefaultParameters().getEventQueueCapacity());
        }

        if (durationThreadCount == null) {
            durationThreadCount = engineConfig.getInteger(ConfigurationConstants.TAG_ENGINE_DURATION_THREAD_COUNT,
                    getEngine().getDefaultParameters().getDurationThreadCount());
        }

        if (asyncEventSetProcessorExecutorThreadCount == null) {
            asyncEventSetProcessorExecutorThreadCount =
                    engineConfig.getInteger(ConfigurationConstants.TAG_ENGINE_ASYNC_EVENT_SET_PROCESSOR_EXECUTOR_THREAD_COUNT,
                            getEngine().getDefaultParameters().getAsyncEventSetProcessorExecutorThreadCount());
        }

        if (eventSetProcessorDefaultSynchronous == null) {
            eventSetProcessorDefaultSynchronous =
                    engineConfig.getBoolean(ConfigurationConstants.TAG_ENGINE_EVENT_SET_PROCESSOR_DEFAULT_SYNCHRONOUS,
                            getEngine().getDefaultParameters().getEventSetProcessorDefaultSynchronous());
        }

        if (autoEnable == null) {
            autoEnable = engineConfig.getBoolean(ConfigurationConstants.TAG_ENGINE_AUTO_ENABLE,
                    getEngine().getDefaultParameters().getAutoEnable());
        }

        if (executorShutdownTimeout == null) {
            executorShutdownTimeout = engineConfig.getLong(ConfigurationConstants.TAG_ENGINE_EXECUTOR_SHUTDOWN_TIMEOUT,
                    getEngine().getDefaultParameters().getExecutorShutdownTimeout());
        }
    }

    private void setupPropertiesFromPropertiesFile() {
        if (propertiesConfig != null) {
            propertiesConfig.getKeys().forEachRemaining(key -> setProperty(key, propertiesConfig.getString(key)));
        }
    }

    /**
     * Sets up manually predefined properties.
     */
    private void setupPredefinedProperties() {
        properties.forEach((name, entry) -> {
            Object value = entry.getValue();
            if (value != null) {
                rootConfig.setVariable(name, value);
            }
        });
    }

    /**
     * Sets system properties defined in the configuration.
     */
    private void applySystemProperties() {
        properties.forEach((name, entry) -> {
            Object value = entry.getValue();

            // Set system property only if explicitly stated in the configuration
            if (entry.isSystem() && value != null) {
                String alreadySetValue = System.getProperty(name);
                System.setProperty(name, value.toString());
                if (alreadySetValue != null && !alreadySetValue.equals(value)) {
                    logger.debug("Overriding already set system property {} to {} (previous value was {})", name, value, alreadySetValue);
                }
            }
        });
    }

    private void applyVariableProperties() {
        properties.forEach((name, entry) -> {
            if (entry.isVariable()) {
                getEngine().getOperations().setVariable(name, entry.getValue());
            }
        });
    }

    private void setupXmlProperties() {
        for (Configuration configuration : rootConfig.getChildConfigurationsOf(ConfigurationConstants.TAG_PROPERTIES)) {
            String name = configuration.getAttribute(ConfigurationConstants.PROP_ATTRIBUTE_NAME, null);
            Object value = configuration.getValue();
            String systemProperty = System.getProperty(name);

            PropertyEntry entry = properties.get(name);
            if (systemProperty != null) {
                value = systemProperty;
            } else if (entry == null) {
                properties.put(name, new GenericPropertyEntry(value, isPropertyVariable(configuration), isPropertySystem(configuration)));
            } else {
                value = entry.getValue();
            }

            rootConfig.setVariable(name, value);
        }
    }

    /**
     * Resolves XSD file during XML configuration validation.
     */
    private static class ResourceSchemaResolver extends DefaultEntityResolver {

        @Override
        public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException {
            if (ConfigurationConstants.CONFIG_SCHEMA.equals(systemId)) {
                InputStream stream = ConfigurationManager.class.getResourceAsStream(ConfigurationConstants.CONFIG_SCHEMA_LOCATION);
                if (stream != null) {
                    InputSource source = new InputSource(stream);
                    source.setPublicId(publicId);
                    source.setSystemId(systemId);

                    return source;
                } else {
                    throw new SAXException("Cannot find schema " + ConfigurationConstants.CONFIG_SCHEMA);
                }
            }

            return super.resolveEntity(publicId, systemId);
        }
    }

    protected Triple<XMLConfiguration, URL, PropertiesConfiguration> createXmlConfiguration(String fileName) {
        FallbackBasePathLocationStrategy locationStrategy =
                new FallbackBasePathLocationStrategy(FileLocatorUtils.DEFAULT_LOCATION_STRATEGY, home);
        FileBasedConfigurationBuilder<XMLConfiguration> builder = new FileBasedConfigurationBuilder<>(XMLConfiguration.class)
                .configure(new Parameters().xml().setLocationStrategy(locationStrategy).setFileName(fileName).setSchemaValidation(true)
                        .setEntityResolver(new ResourceSchemaResolver()));

        XMLConfiguration xmlConfiguration = null;
        try {
            xmlConfiguration = builder.getConfiguration();
        } catch (ConfigurationException e) {
            throw new ConfigException(locationStrategy.isNotFound() ? "Configuration file " + fileName + " not found"
                    : "Error reading configuration file " + fileName, e);
        }

        String propertiesFileName =
                FilenameUtils.concat(FilenameUtils.getFullPath(fileName), FilenameUtils.getBaseName(fileName)) + ".properties";
        PropertiesConfiguration propertiesConfiguration = null;
        FallbackBasePathLocationStrategy propertiesLocationStrategy =
                new FallbackBasePathLocationStrategy(FileLocatorUtils.DEFAULT_LOCATION_STRATEGY, home);
        try {
            propertiesConfiguration =
                    new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                            .configure(new Parameters().properties().setLocationStrategy(propertiesLocationStrategy)
                                    .setFileName(propertiesFileName).setEncoding(Charsets.UTF_8.name()).setThrowExceptionOnMissing(false))
                            .getConfiguration();
        } catch (ConfigurationException e) {
            // Ignoring the exception if the optional properties file hasn't been found.
            if (propertiesLocationStrategy.isNotFound()) {
                propertiesConfiguration = new PropertiesConfiguration();
            } else {
                throw new ConfigException("Error reading configuration properties file " + propertiesFileName, e);
            }
        }

        return new ImmutableTriple<>(xmlConfiguration, locationStrategy.getLocatedUrl(), propertiesConfiguration);
    }

    protected void readConfiguration() {
        MergeCombiner combiner = new MergeCombiner();
        combiner.addListNode(PluginManagerConstants.CFG_PLUGIN);

        CombinedConfiguration cc = new CombinedConfiguration(combiner);

        XMLConfiguration defaultConfiguration = createXmlConfiguration(ConfigurationConstants.DEFAULT_CONFIG).getLeft();

        // Try to add explicit configuration.
        if (configurationFilename != null) {
            logger.info("Loading the configuration file {}...", configurationFilename);
            Triple<XMLConfiguration, URL, PropertiesConfiguration> configurationResult = createXmlConfiguration(configurationFilename);

            XMLConfiguration xmlConfiguration = configurationResult.getLeft();
            configurationFileUrl = configurationResult.getMiddle();
            propertiesConfig = configurationResult.getRight();

            cc.addConfiguration(xmlConfiguration);
            xmlConfiguration.setProperty(ConfigurationConstants.PROP_CONFIG_DIR, SpongeUtils.getFileDir(configurationFileUrl));
        }

        // Add default configuration.
        cc.addConfiguration(defaultConfiguration);

        if (configurationFilename != null && logger.isDebugEnabled()) {
            logger.debug("Initial XML configuration:\n{}", SpongeUtils.dumpConfiguration(cc));
        }

        rootConfig = new CommonsConfiguration(cc);
    }

    /**
     * Checks if the property should be saved to system properties.
     *
     * @param configuration property configuration.
     * @return {@code true} if the property should be saved to system properties.
     */
    private boolean isPropertySystem(Configuration configuration) {
        return configuration.getBooleanAttribute(ConfigurationConstants.PROP_ATTRIBUTE_SYSTEM, false);
    }

    private boolean isPropertyVariable(Configuration configuration) {
        return configuration.getBooleanAttribute(ConfigurationConstants.PROP_ATTRIBUTE_VARIABLE, false);
    }

    /**
     * Returns the root configuration.
     *
     * @return the root configuration.
     */
    @Override
    public Configuration getRootConfig() {
        return rootConfig;
    }

    /**
     * Returns the engine configuration.
     *
     * @return the engine configuration.
     */
    @Override
    public Configuration getEngineConfig() {
        return engineConfig;
    }

    @Override
    public String getEngineName() {
        return engineName;
    }

    @Override
    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    @Override
    public String getEngineDisplayName() {
        return engineDisplayName;
    }

    @Override
    public void setEngineDisplayName(String engineDisplayName) {
        this.engineDisplayName = engineDisplayName;
    }

    @Override
    public String getEngineDescription() {
        return engineDescription;
    }

    @Override
    public void setEngineDescription(String engineDescription) {
        this.engineDescription = engineDescription;
    }

    /**
     * Returns the number of the Main Processing Unit worker threads.
     *
     * @return the number of the Main Processing Unit worker threads.
     */
    @Override
    public int getMainProcessingUnitThreadCount() {
        return mainProcessingUnitThreadCount;
    }

    /**
     * Returns the event queue capacity.
     *
     * @return the event queue capacity.
     */
    @Override
    public int getEventQueueCapacity() {
        return eventQueueCapacity;
    }

    @Override
    public int getDurationThreadCount() {
        return durationThreadCount;
    }

    @Override
    public int getAsyncEventSetProcessorExecutorThreadCount() {
        return asyncEventSetProcessorExecutorThreadCount;
    }

    /**
     * Return event clone policy.
     *
     * @return eventClonePolicy
     */
    @Override
    public EventClonePolicy getEventClonePolicy() {
        return eventClonePolicy;
    }

    @Override
    public long getExecutorShutdownTimeout() {
        return executorShutdownTimeout;
    }

    /**
     * Returns the engine home directory.
     *
     * @return the engine home directory.
     */
    @Override
    public String getHome() {
        return home;
    }

    @Override
    public URL getConfigurationFileUrl() {
        return configurationFileUrl;
    }

    @Override
    public String toString() {
        //@formatter:off
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("home", home)
                .append("configurationFilename", configurationFilename)
                .append("configurationFileUrl", configurationFileUrl)
                .append("engineName", engineName)
                .append("mainProcessingUnitThreadCount", mainProcessingUnitThreadCount)
                .append("asyncEventSetProcessorExecutorThreadCount", asyncEventSetProcessorExecutorThreadCount)
                .append("eventQueueCapacity", eventQueueCapacity)
                .append("eventSetProcessorDefaultSynchronous", eventSetProcessorDefaultSynchronous)
                .append("eventClonePolicy", eventClonePolicy)
                .append("autoEnable", autoEnable)
                .append("durationThreadCount", durationThreadCount)
                .append("executorShutdownTimeout", executorShutdownTimeout)
                .append("properties", properties)
                .toString();
        //@formatter:on
    }

    @Override
    public String getProperty(String name) {
        String systemProperty = System.getProperty(name);
        if (systemProperty != null) {
            return systemProperty;
        }

        PropertyEntry entry = properties.get(name);
        if (entry == null) {
            return null;
        }

        Object value = entry.getValue();

        return value != null ? String.valueOf(value) : null;
    }

    @Override
    public void setEventClonePolicy(EventClonePolicy eventClonePolicy) {
        this.eventClonePolicy = eventClonePolicy;
    }

    @Override
    public void setMainProcessingUnitThreadCount(int mainProcessingUnitThreadCount) {
        this.mainProcessingUnitThreadCount = mainProcessingUnitThreadCount;
    }

    @Override
    public void setEventQueueCapacity(int eventQueueCapacity) {
        this.eventQueueCapacity = eventQueueCapacity;
    }

    @Override
    public void setDurationThreadCount(int durationThreadCount) {
        this.durationThreadCount = durationThreadCount;
    }

    @Override
    public void setAsyncEventSetProcessorExecutorThreadCount(int asyncEventSetProcessorExecutorThreadCount) {
        this.asyncEventSetProcessorExecutorThreadCount = asyncEventSetProcessorExecutorThreadCount;
    }

    @Override
    public void setExecutorShutdownTimeout(long executorShutdownTimeout) {
        this.executorShutdownTimeout = executorShutdownTimeout;
    }

    @Override
    public void setProperty(String key, Object value, boolean variable, boolean system) {
        properties.put(key, new GenericPropertyEntry(value, variable, system));
    }

    @Override
    public void setProperty(String key, Object value) {
        setProperty(key, value, false, false);
    }

    @Override
    public void setSystemProperty(String key, Object value) {
        setProperty(key, value, false, true);
    }

    @Override
    public void setVariableProperty(String key, Object value) {
        setProperty(key, value, true, false);
    }

    @Override
    public void setProperties(Map<String, Object> properties) {
        properties.forEach((key, value) -> setProperty(key, value));
    }

    @Override
    public void setSystemProperties(Map<String, String> systemProperties) {
        systemProperties.forEach((key, value) -> setSystemProperty(key, value));
    }

    @Override
    public void setVariableProperties(Map<String, String> variableProperties) {
        variableProperties.forEach((key, value) -> setVariableProperty(key, value));
    }

    @Override
    public void setPropertyEntries(Map<String, PropertyEntry> propertyEntries) {
        properties.putAll(propertyEntries);
    }

    @Override
    public boolean getEventSetProcessorDefaultSynchronous() {
        return eventSetProcessorDefaultSynchronous;
    }

    @Override
    public void setEventSetProcessorDefaultSynchronous(boolean eventSetProcessorDefaultSynchronous) {
        this.eventSetProcessorDefaultSynchronous = eventSetProcessorDefaultSynchronous;
    }

    @Override
    public boolean getAutoEnable() {
        return autoEnable;
    }

    @Override
    public void setAutoEnable(boolean autoEnable) {
        this.autoEnable = autoEnable;
    }

    @Override
    public List<Plugin> getPreConfigPlugins() {
        return preConfigPlugins;
    }

    @Override
    public List<Plugin> getPostConfigPlugins() {
        return postConfigPlugins;
    }

    @Override
    public void addPreConfigPlugins(List<Plugin> plugins) {
        preConfigPlugins.addAll(plugins);
    }

    @Override
    public void addPostConfigPlugins(List<Plugin> plugins) {
        postConfigPlugins.addAll(plugins);
    }

    @Override
    public List<KnowledgeBase> getPreConfigKnowledgeBases() {
        return preConfigKnowledgeBases;
    }

    @Override
    public List<KnowledgeBase> getPostConfigKnowledgeBases() {
        return postConfigKnowledgeBases;
    }

    @Override
    public void addPreConfigKnowledgeBases(List<KnowledgeBase> knowledgeBases) {
        preConfigKnowledgeBases.addAll(knowledgeBases);
    }

    @Override
    public void addPostConfigKnowledgeBases(List<KnowledgeBase> knowledgeBases) {
        postConfigKnowledgeBases.addAll(knowledgeBases);
    }
}
