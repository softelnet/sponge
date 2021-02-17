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

package org.openksavi.sponge.spring;

import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.core.engine.BaseSpongeEngine;

/**
 * A Spring aware Sponge engine. Startup and shutdown is managed by Spring.
 */
public class SpringSpongeEngine extends BaseSpongeEngine implements ApplicationContextAware, SmartLifecycle {

    private static final Logger logger = LoggerFactory.getLogger(SpringSpongeEngine.class);

    private ApplicationContext applicationContext;

    private boolean autoStartup = true;

    private int phase = DEFAULT_PHASE;

    /**
     * Creates a new engine.
     */
    public SpringSpongeEngine() {
        //
    }

    /**
     * Builder for creating an engine.
     *
     * @return builder.
     */
    public static SpringEngineBuilder builder() {
        return new SpringEngineBuilder(new SpringSpongeEngine());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public boolean isAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    @Override
    public void start() {
        if (autoStartup) {
            startup();
        }
    }

    @Override
    public void stop() {
        if (autoStartup) {
            shutdown();
        }
    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    @Override
    public int getPhase() {
        return phase;
    }

    public void tryEnableProcessorBeans() {
        if (applicationContext != null) {
            enableProcessorBeans();
        } else {
            logger.warn("A Spring application context is not set. Processor beans will not be scanned.");
        }
    }

    @SuppressWarnings("rawtypes")
    public void enableProcessorBeans() {
        Validate.notNull(applicationContext, "A Spring application context is not set");

        Map<String, Processor> processors = applicationContext.getBeansOfType(Processor.class);

        processors.values().forEach((processor) -> getOperations().enableJava(processor));
    }
}
