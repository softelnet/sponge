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

import java.net.URL;
import java.util.Map;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.config.PropertyEntry;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * Configuration manager interface. It contains methods for accessing configuration parameters.
 */
public interface ConfigurationManager extends EngineModule {

    /**
     * Returns the property value.
     *
     * @param name the property name.
     * @return the property value.
     */
    String getProperty(String name);

    /**
     * Returns the root configuration.
     *
     * @return the root configuration.
     */
    Configuration getRootConfig();

    /**
     * Returns the engine configuration.
     *
     * @return the engine configuration.
     */
    Configuration getEngineConfig();

    /**
     * Returns the home directory for this engine.
     *
     * @return the home directory for this engine.
     */
    String getHome();

    /**
     * Returns the configuration file URL. This method should be used only after reading the configuration.
     *
     * @return the configuration file URL or {@code null} if there is no configuration file.
     */
    URL getConfigurationFileUrl();

    /**
     * Returns the engine name.
     *
     * @return the engine name.
     */
    String getEngineName();

    /**
     * Sets the engine name.
     *
     * @param engineName the engine name.
     */
    void setEngineName(String engineName);

    /**
     * Returns the number of the Main Processing Unit worker threads.
     *
     * @return the number of the Main Processing Unit worker threads.
     */
    int getMainProcessingUnitThreadCount();

    /**
     * Returns the event queue capacity.
     *
     * @return the event queue capacity.
     */
    int getEventQueueCapacity();

    /**
     * Returns the number of threads used by a duration executor.
     *
     * @return the number of threads used by a duration executor.
     */
    int getDurationThreadCount();

    /**
     * Returns the number of threads used by an event set processor asynchronous executor.
     *
     * @return the number of threads used by an event set processor asynchronous executor.
     */
    int getAsyncEventSetProcessorExecutorThreadCount();

    /**
     * Return event clone policy.
     *
     * @return eventClonePolicy
     */
    EventClonePolicy getEventClonePolicy();

    /**
     * Return an event set processor default synchronous flag.
     *
     * @return an event set processor default synchronous flag.
     */
    boolean getEventSetProcessorDefaultSynchronous();

    /**
     * Returns {@code true} if auto-enable of processors is turned on.
     *
     * @return {@code true} if auto-enable of processors is turned on.
     */
    boolean getAutoEnable();

    /**
     * Returns an executor shutdown timeout (in milliseconds).
     *
     * @return an executor shutdown timeout.
     */
    long getExecutorShutdownTimeout();

    /**
     * Sets the number of the Main Processing Unit worker threads.
     *
     * @param mainProcessingUnitThreadCount the number of the Main Processing Unit worker threads.
     */
    void setMainProcessingUnitThreadCount(int mainProcessingUnitThreadCount);

    /**
     * Sets an event set processor default synchronous flag.
     *
     * @param eventSetProcessorDefaultSynchronous an event set processor default synchronous flag.
     */
    void setEventSetProcessorDefaultSynchronous(boolean eventSetProcessorDefaultSynchronous);

    /**
     * Sets event clone policy.
     *
     * @param eventClonePolicy event clone policy.
     */
    void setEventClonePolicy(EventClonePolicy eventClonePolicy);

    /**
     * Sets event queue capacity.
     *
     * @param eventQueueCapacity event queue capacity.
     */
    void setEventQueueCapacity(int eventQueueCapacity);

    /**
     * Sets duration executor thread count.
     *
     * @param durationThreadCount duration executor thread count.
     */
    void setDurationThreadCount(int durationThreadCount);

    /**
     * Sets the number of threads used by an event set processor asynchronous executor.
     *
     * @param asyncEventSetProcessorExecutorThreadCount the number of threads used by an event set processor asynchronous executor.
     */
    void setAsyncEventSetProcessorExecutorThreadCount(int asyncEventSetProcessorExecutorThreadCount);

    /**
     * Sets property.
     *
     * @param key property key.
     * @param value property value.
     * @param variable should this property be set as a variable.
     * @param system is a system property.
     */
    void setProperty(String key, Object value, boolean variable, boolean system);

    /**
     * Sets property.
     *
     * @param key property key.
     * @param value property value.
     */
    void setProperty(String key, Object value);

    /**
     * Sets a property that is also a system property.
     *
     * @param key property key.
     * @param value property value.
     */
    void setSystemProperty(String key, Object value);

    /**
     * Sets a property that is also an engine scope variable.
     *
     * @param key property key.
     * @param value property value.
     */
    void setVariableProperty(String key, Object value);

    /**
     * Sets properties.
     *
     * @param properties property map.
     */
    void setProperties(Map<String, Object> properties);

    /**
     * Sets property entries.
     *
     * @param propertyEntries property entries.
     */
    void setPropertyEntries(Map<String, PropertyEntry> propertyEntries);

    /**
     * Sets system properties.
     *
     * @param systemProperties system property map.
     */
    void setSystemProperties(Map<String, String> systemProperties);

    /**
     * Sets variable properties.
     *
     * @param variableProperties variable property map.
     */
    void setVariableProperties(Map<String, String> variableProperties);

    /**
     * Sets auto-enable of processors.
     *
     * @param autoEnable auto-enable of processors.
     */
    void setAutoEnable(boolean autoEnable);

    /**
     * Sets an executor shutdown timeout (in milliseconds).
     *
     * @param executorShutdownTimeout an executor shutdown timeout.
     */
    void setExecutorShutdownTimeout(long executorShutdownTimeout);
}
