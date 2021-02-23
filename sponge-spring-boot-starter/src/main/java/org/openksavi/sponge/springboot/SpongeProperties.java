/*
 * Copyright 2016-2021 The Sponge authors.
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

package org.openksavi.sponge.springboot;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.openksavi.sponge.event.EventClonePolicy;

@ConfigurationProperties(prefix = "sponge")
public class SpongeProperties {

    private String home;

    private String configFile;

    private Boolean ignoreConfigurationFileNotFound;

    private String name;

    private String label;

    private String description;

    private String license;

    private Map<String, Object> properties = new LinkedHashMap<>();

    private Map<String, String> systemProperties = new LinkedHashMap<>();

    private Map<String, String> variableProperties = new LinkedHashMap<>();

    private String defaultKnowledgeBaseName;

    private Boolean autoStartup;

    private Integer phase;

    private String processorBeansKnowledgeBaseName;

    private final EngineProperties engine = new EngineProperties();

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public Boolean getIgnoreConfigurationFileNotFound() {
        return ignoreConfigurationFileNotFound;
    }

    public void setIgnoreConfigurationFileNotFound(Boolean ignoreConfigurationFileNotFound) {
        this.ignoreConfigurationFileNotFound = ignoreConfigurationFileNotFound;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Map<String, String> getSystemProperties() {
        return systemProperties;
    }

    public Map<String, String> getVariableProperties() {
        return variableProperties;
    }

    public String getDefaultKnowledgeBaseName() {
        return defaultKnowledgeBaseName;
    }

    public void setDefaultKnowledgeBaseName(String defaultKnowledgeBaseName) {
        this.defaultKnowledgeBaseName = defaultKnowledgeBaseName;
    }

    public String getProcessorBeansKnowledgeBaseName() {
        return processorBeansKnowledgeBaseName;
    }

    public void setProcessorBeansKnowledgeBaseName(String processorBeansKnowledgeBaseName) {
        this.processorBeansKnowledgeBaseName = processorBeansKnowledgeBaseName;
    }

    public Boolean getAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(Boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public Integer getPhase() {
        return phase;
    }

    public void setPhase(Integer phase) {
        this.phase = phase;
    }

    public EngineProperties getEngine() {
        return engine;
    }

    public static class EngineProperties {

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

        public Integer getMainProcessingUnitThreadCount() {
            return mainProcessingUnitThreadCount;
        }

        public void setMainProcessingUnitThreadCount(Integer mainProcessingUnitThreadCount) {
            this.mainProcessingUnitThreadCount = mainProcessingUnitThreadCount;
        }

        public EventClonePolicy getEventClonePolicy() {
            return eventClonePolicy;
        }

        public void setEventClonePolicy(EventClonePolicy eventClonePolicy) {
            this.eventClonePolicy = eventClonePolicy;
        }

        public Integer getEventQueueCapacity() {
            return eventQueueCapacity;
        }

        public void setEventQueueCapacity(Integer eventQueueCapacity) {
            this.eventQueueCapacity = eventQueueCapacity;
        }

        public Integer getDurationThreadCount() {
            return durationThreadCount;
        }

        public void setDurationThreadCount(Integer durationThreadCount) {
            this.durationThreadCount = durationThreadCount;
        }

        public Integer getAsyncEventSetProcessorExecutorThreadCount() {
            return asyncEventSetProcessorExecutorThreadCount;
        }

        public void setAsyncEventSetProcessorExecutorThreadCount(Integer asyncEventSetProcessorExecutorThreadCount) {
            this.asyncEventSetProcessorExecutorThreadCount = asyncEventSetProcessorExecutorThreadCount;
        }

        public Boolean getEventSetProcessorDefaultSynchronous() {
            return eventSetProcessorDefaultSynchronous;
        }

        public void setEventSetProcessorDefaultSynchronous(Boolean eventSetProcessorDefaultSynchronous) {
            this.eventSetProcessorDefaultSynchronous = eventSetProcessorDefaultSynchronous;
        }

        public Boolean getAutoEnable() {
            return autoEnable;
        }

        public void setAutoEnable(Boolean autoEnable) {
            this.autoEnable = autoEnable;
        }

        public Long getExecutorShutdownTimeout() {
            return executorShutdownTimeout;
        }

        public void setExecutorShutdownTimeout(Long executorShutdownTimeout) {
            this.executorShutdownTimeout = executorShutdownTimeout;
        }
    }
}
