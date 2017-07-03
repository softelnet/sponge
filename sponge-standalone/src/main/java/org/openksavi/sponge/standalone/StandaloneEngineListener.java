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

package org.openksavi.sponge.standalone;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericGroovyApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.camel.CamelPlugin;
import org.openksavi.sponge.engine.OnShutdownListener;
import org.openksavi.sponge.engine.OnStartupListener;
import org.openksavi.sponge.signal.SystemSignal;
import org.openksavi.sponge.signal.SystemSignalListener;
import org.openksavi.sponge.spring.SpringPlugin;

/**
 * Standalone onStartup/onShutdown listener.
 */
public class StandaloneEngineListener implements OnStartupListener, OnShutdownListener {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneEngineListener.class);

    /** Operating system signals. */
    private static List<String> SIGNALS =
            Arrays.asList(SystemSignal.SIGTERM, SystemSignal.SIGINT, SystemSignal.SIGABRT, SystemSignal.SIGHUP);

    private StandaloneEngine engine;

    /** Spring context providing Groovy and XML configurations. */
    private GenericGroovyApplicationContext context;

    /** Spring configuration files. */
    private List<String> springConfigurations = new ArrayList<>();

    /** Should create a Camel context. */
    private boolean camel = false;

    public StandaloneEngineListener(StandaloneEngine engine) {
        this.engine = engine;
    }

    @Override
    public void onStartup() {
        initSystemSignals();
        addShutdownHook();
        createSpringContext();
    }

    /**
     * Initializes system signal handlers.
     */
    protected void initSystemSignals() {
        SystemSignalListener systemSignalListener = (signal) -> {
            try {
                if (signal.isSignal(SystemSignal.SIGHUP)) {
                    engine.reload();
                } else {
                    engine.shutdown();
                    System.exit(0);
                }
            } catch (Exception e) {
                logger.error("Signal handler failed", e);
            }
        };

        SIGNALS.forEach(signal -> {
            try {
                SystemSignal.setSystemSignalListener(signal, systemSignalListener);
            } catch (Exception e) {
                logger.warn("Init signal handlers: {}", e.getMessage());
            }
        });
    }

    /**
     * Registers a shutdown hook.
     */
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (engine != null) {
                    engine.shutdown();
                }
            } catch (Throwable e) {
                logger.error("Shutdown hook error", e);
            }
        }));
    }

    public List<String> getSpringConfigurations() {
        return springConfigurations;
    }

    public void setSpringConfigurations(List<String> springConfigurations) {
        this.springConfigurations = springConfigurations;
    }

    public boolean isCamel() {
        return camel;
    }

    public void setCamel(boolean camel) {
        this.camel = camel;
    }

    private StandalonePlugin getStandalonePlugin() {
        return engine.getPluginManager().getPlugin(StandalonePlugin.NAME, StandalonePlugin.class);
    }

    /**
     * Sets up a Spring plugin.
     */
    private void setupSpringPlugin() {
        if (context.containsBeanDefinition(SpringPlugin.NAME)) {
            engine.getPluginManager().addPlugin(context.getBean(SpringPlugin.class, SpringPlugin.NAME));
        }
    }

    /**
     * Sets up a Camel plugin.
     */
    private void setupCamelPlugin() {
        if (context.containsBeanDefinition(CamelPlugin.NAME)) {
            if (!engine.getPluginManager().existsPlugin(CamelPlugin.NAME)) {
                engine.getPluginManager().addPlugin(context.getBean(CamelPlugin.class, CamelPlugin.NAME));
            }
        }
    }

    /**
     * Returns all Spring configuration files.
     *
     * @return all Spring configuration files.
     */
    protected List<String> resolveConfigurationFiles() {
        List<String> result = new ArrayList<>();

        StandalonePlugin standalonePlugin = getStandalonePlugin();
        // Add Spring configuration files defined in a StandalonePlugin.
        if (standalonePlugin != null) {
            result.addAll(standalonePlugin.getCompleteSpringConfigurationFiles());
        }

        // Add CLI Spring configuration files.
        if (springConfigurations != null) {
            result.addAll(springConfigurations);
        }

        // Add a Camel context configuration file if needed.
        if (!result.isEmpty() && (isCamel() || standalonePlugin != null && standalonePlugin.isCamel())) {
            result.add(StandalonePlugin.CAMEL_CONTEXT_CONFIG);
        }

        return result;
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
                String home = engine.getConfigurationManager().getHome();
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
     * Creates a Spring context.
     */
    protected void createSpringContext() {
        List<Resource> resources = resolveSpringConfigurationResources();
        if (resources.isEmpty()) {
            return;
        }

        logger.debug("Loading Spring configuration from {}", resources);

        context = new GenericGroovyApplicationContext();
        StandalonePlugin standalonePlugin = getStandalonePlugin();
        context.getBeanFactory().registerSingleton(
                standalonePlugin != null ? standalonePlugin.getEngineBeanName() : StandalonePlugin.DEFAULT_ENGINE_BEAN_NAME, engine);
        resources.forEach(resource -> context.load(resource));
        context.refresh();

        context.start();

        setupSpringPlugin();
        setupCamelPlugin();
    }

    @Override
    public void onShutdown() {
        if (context != null) {
            context.stop();
            context.close();
            context = null;
        }
    }
}
