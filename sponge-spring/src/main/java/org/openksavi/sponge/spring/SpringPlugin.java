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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.openksavi.sponge.java.JPlugin;

/**
 * Sponge plugin that provides integration with the Spring framework. It should be set up in the Spring configuration as a bean and added to
 * the Engine Builder.
 */
public class SpringPlugin extends JPlugin implements ApplicationContextAware {

    public static final String NAME = "spring";

    private ApplicationContext context;

    public SpringPlugin() {
        setName(NAME);
    }

    public SpringPlugin(String name) {
        super(name);
    }

    /**
     * Returns a Spring application context.
     *
     * @return a Spring application context.
     */
    public ApplicationContext getContext() {
        return context;
    }

    /**
     * Sets a Spring application context.
     *
     * @param context a Spring application context.
     */
    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }
}
