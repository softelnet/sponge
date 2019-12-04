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

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericGroovyApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.camel.CamelPlugin;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JPlugin;
import org.openksavi.sponge.spring.SpringPlugin;

/**
 * A plugin for standalone command-line application.
 */
public class StandalonePlugin extends JPlugin {

    private static final Logger logger = LoggerFactory.getLogger(StandalonePlugin.class);

    public static final String SPRING_CONTEXT_CONFIG = SpongeUtils.getPackagePath(StandalonePlugin.class) + "/spring-context.xml";

    public static final String CAMEL_CONTEXT_CONFIG = SpongeUtils.getPackagePath(StandalonePlugin.class) + "/camel-context.xml";

    public static final String NAME = "standalone";

    public static final String CONFIG_SPRING_CONFIGURATION = "spring";

    public static final String CONFIG_SPRING_CONFIGURATION_ATTR_ENGINE_BEAN_NAME = "engineBeanName";

    public static final String CONFIG_SPRING_CONFIGURATION_ATTR_CAMEL = "camel";

    public static final String CONFIG_SPRING_CONFIGUTARION_FILE = "file";

    /** Spring context providing Groovy and XML configurations. */
    private GenericGroovyApplicationContext context;

    public StandalonePlugin() {
        setName(NAME);
    }

    public StandalonePlugin(String name) {
        super(name);
    }

    @Override
    public void onConfigure(Configuration configuration) {
        if (isStandaloneEnvironment()) {
            StandaloneSettings settings = findSettings();

            if (configuration.hasChildConfiguration(CONFIG_SPRING_CONFIGURATION)) {
                settings.setEngineBeanName(configuration.getChildConfiguration(CONFIG_SPRING_CONFIGURATION)
                        .getAttribute(CONFIG_SPRING_CONFIGURATION_ATTR_ENGINE_BEAN_NAME, settings.getEngineBeanName()));

                settings.setCamel(configuration.getChildConfiguration(CONFIG_SPRING_CONFIGURATION)
                        .getBooleanAttribute(CONFIG_SPRING_CONFIGURATION_ATTR_CAMEL, settings.isCamel()));

                List<String> allSpringConfigurationFiles = new ArrayList<>();
                if (settings.getSpringConfigurationFiles() != null) {
                    allSpringConfigurationFiles.addAll(settings.getSpringConfigurationFiles());
                }

                allSpringConfigurationFiles.addAll(
                        configuration.getConfigurationsAt(CONFIG_SPRING_CONFIGURATION + "." + CONFIG_SPRING_CONFIGUTARION_FILE).stream()
                                .map(conf -> conf.getValue()).filter(file -> !StringUtils.isEmpty(file)).collect(Collectors.toList()));

                settings.setSpringConfigurationFiles(allSpringConfigurationFiles);
            }
        }
    }

    @Override
    public void onStartup() {
        if (isStandaloneEnvironment()) {
            createSpringContext();
        }
    }

    @Override
    public void onShutdown() {
        if (context != null) {
            context.stop();
            context.close();
            context = null;
        }
    }

    public GenericGroovyApplicationContext getContext() {
        return context;
    }

    protected StandaloneSettings findSettings() {
        return Validate.notNull(getEngineOperations().getVariable(StandaloneSettings.class, StandaloneSettings.VARIABLE_NAME),
                "The %s plugin requires a %s variable", getName(), StandaloneSettings.VARIABLE_NAME);
    }

    protected boolean isStandaloneEnvironment() {
        return getEngineOperations().hasVariable(StandaloneSettings.VARIABLE_NAME);
    }

    /**
     * Creates a Spring context.
     */
    protected void createSpringContext() {
        List<Resource> resources = resolveSpringConfigurationResources();
        if (resources.isEmpty()) {
            return;
        }

        logger.debug("Loading Spring configuration from {}", resources);

        context = new GenericGroovyApplicationContext();
        StandaloneSettings settings = findSettings();

        context.getBeanFactory().registerSingleton(
                settings.getEngineBeanName() != null ? settings.getEngineBeanName() : StandaloneSettings.DEFAULT_ENGINE_BEAN_NAME,
                getEngine());
        resources.forEach(resource -> context.load(resource));
        context.refresh();

        setupSpringPlugin();
        setupCamelPlugin();

        context.start();
    }

    /**
     * Returns all Spring configuration files as resources.
     *
     * @return all Spring configuration files as resources.
     */
    protected List<Resource> resolveSpringConfigurationResources() {
        return resolveConfigurationFiles().stream().map(file -> {
            Resource springConfigurationResource = new FileSystemResource(file);
            if (!springConfigurationResource.exists()) {
                String home = getEngine().getConfigurationManager().getHome();
                if (home != null) {
                    springConfigurationResource = new FileSystemResource(Paths.get(home, file).toString());
                }
            }

            if (!springConfigurationResource.exists()) {
                springConfigurationResource = new ClassPathResource(file);
            }

            if (!springConfigurationResource.exists()) {
                throw new SpongeException("Spring configuration file '" + file + "' cannot be found");
            }

            return springConfigurationResource;
        }).collect(Collectors.toList());
    }

    /**
     * Returns all Spring configuration files.
     *
     * @return all Spring configuration files.
     */
    protected List<String> resolveConfigurationFiles() {
        Set<String> result = new LinkedHashSet<>();

        StandaloneSettings settings = findSettings();

        if (settings.getSpringConfigurationFiles() != null && !settings.getSpringConfigurationFiles().isEmpty()) {
            result.add(SPRING_CONTEXT_CONFIG);
            result.addAll(settings.getSpringConfigurationFiles());
        }

        // Add a Camel context configuration file if needed.
        if (settings.isCamel()) {
            result.add(SPRING_CONTEXT_CONFIG);
            result.add(StandalonePlugin.CAMEL_CONTEXT_CONFIG);
        }

        return new ArrayList<>(result);
    }

    /**
     * Sets up a Spring plugin.
     */
    private void setupSpringPlugin() {
        if (context.containsBeanDefinition(SpringPlugin.NAME)) {
            if (!getEngine().getPluginManager().hasPlugin(SpringPlugin.NAME)) {
                getEngine().getPluginManager().addPlugin(context.getBean(SpringPlugin.class, SpringPlugin.NAME));
            }
        }
    }

    /**
     * Sets up a Camel plugin.
     */
    private void setupCamelPlugin() {
        if (context.containsBeanDefinition(CamelPlugin.NAME)) {
            if (!getEngine().getPluginManager().hasPlugin(CamelPlugin.NAME)) {
                getEngine().getPluginManager().addPlugin(context.getBean(CamelPlugin.class, CamelPlugin.NAME));
            }
        }
    }
}
