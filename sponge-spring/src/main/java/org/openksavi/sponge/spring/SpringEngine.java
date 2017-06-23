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

package org.openksavi.sponge.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.openksavi.sponge.core.engine.BaseEngine;
import org.openksavi.sponge.core.engine.EngineBuilder;

/**
 * A Spring aware engine. Startup and shutdown is managed by Spring.
 */
public class SpringEngine extends BaseEngine implements ApplicationContextAware, InitializingBean, DisposableBean {

    private ApplicationContext applicationContext;

    /**
     * Creates a new engine.
     */
    public SpringEngine() {
        //
    }

    /**
     * Builder for creating Engine instances.
     *
     * @return builder.
     */
    public static EngineBuilder<SpringEngine> builder() {
        return new EngineBuilder<>(new SpringEngine());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        startup();
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }
}
