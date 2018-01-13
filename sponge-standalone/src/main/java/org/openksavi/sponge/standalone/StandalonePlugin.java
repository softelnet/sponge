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

package org.openksavi.sponge.standalone;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JPlugin;

/**
 * A plugin for standalone command-line application.
 */
public class StandalonePlugin extends JPlugin {

    public static final String SPRING_CONTEXT_CONFIG = SpongeUtils.getPackagePath(StandalonePlugin.class) + "/spring-context.xml";

    public static final String CAMEL_CONTEXT_CONFIG = SpongeUtils.getPackagePath(StandalonePlugin.class) + "/camel-context.xml";

    public static final String DEFAULT_ENGINE_BEAN_NAME = "spongeEngine";

    public static final String NAME = "standalonePlugin";

    public static final String CONFIG_SPRING_CONFIGURATION = "spring";

    public static final String CONFIG_SPRING_CONFIGURATION_ATTR_ENGINE_BEAN_NAME = "engineBeanName";

    public static final String CONFIG_SPRING_CONFIGURATION_ATTR_CAMEL = "camel";

    public static final String CONFIG_SPRING_CONFIGUTARION_FILE = "file";

    /** Spring configuration files. */
    private List<String> springConfigurationFiles;

    /** A bean name for the engine. */
    private String engineBeanName = DEFAULT_ENGINE_BEAN_NAME;

    /** Equals to {@code true} if a Camel context should be created. */
    private boolean camel = false;

    public StandalonePlugin() {
        setName(NAME);
    }

    public StandalonePlugin(String name) {
        super(name);
    }

    /**
     * Returns complete Spring configuration files.
     *
     * @return complete Spring configuration files.
     */
    public List<String> getCompleteSpringConfigurationFiles() {
        List<String> result = new ArrayList<>();

        if (springConfigurationFiles != null) {
            result.add(SPRING_CONTEXT_CONFIG);
            result.addAll(springConfigurationFiles);
        }

        return result;
    }

    /**
     * Returns a bean name for the engine.
     *
     * @return a bean name for the engine.
     */
    public String getEngineBeanName() {
        return engineBeanName;
    }

    /**
     * Sets a bean name for the engine.
     *
     * @param engineBeanName a bean name for the engine.
     */
    public void setEngineBeanName(String engineBeanName) {
        this.engineBeanName = engineBeanName;
    }

    /**
     * Sets Spring configuration files.
     *
     * @param springConfigurationFiles Spring configuration files.
     */
    public void setSpringConfigurationFiles(List<String> springConfigurationFiles) {
        this.springConfigurationFiles = springConfigurationFiles;
    }

    /**
     * Returns {@code true} if a Camel context should be created.
     *
     * @return {@code true} if a Camel context should be created.
     */
    public boolean isCamel() {
        return camel;
    }

    /**
     * If {@code camel} is {@code true}, then a Camel context will be created.
     *
     * @param camel should Camel context be created.
     */
    public void setCamel(boolean camel) {
        this.camel = camel;
    }

    @Override
    public void onConfigure(Configuration configuration) {
        if (configuration.hasChildConfiguration(CONFIG_SPRING_CONFIGURATION)) {
            engineBeanName = configuration.getChildConfiguration(CONFIG_SPRING_CONFIGURATION)
                    .getAttribute(CONFIG_SPRING_CONFIGURATION_ATTR_ENGINE_BEAN_NAME, engineBeanName);

            camel = configuration.getChildConfiguration(CONFIG_SPRING_CONFIGURATION)
                    .getBooleanAttribute(CONFIG_SPRING_CONFIGURATION_ATTR_CAMEL, camel);

            springConfigurationFiles =
                    Stream.of(configuration.getConfigurationsAt(CONFIG_SPRING_CONFIGURATION + "." + CONFIG_SPRING_CONFIGUTARION_FILE))
                            .map(conf -> conf.getValue()).filter(file -> !StringUtils.isEmpty(file)).collect(Collectors.toList());
        }
    }
}
