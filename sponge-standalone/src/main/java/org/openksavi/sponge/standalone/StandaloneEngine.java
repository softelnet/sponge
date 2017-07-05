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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.openksavi.sponge.core.engine.BaseEngine;
import org.openksavi.sponge.core.engine.InteractiveMode;

/**
 * A standalone engine.
 */
public class StandaloneEngine extends BaseEngine implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private InteractiveMode interactiveMode;

    public StandaloneEngine() {
        //
    }

    public InteractiveMode getInteractiveMode() {
        return interactiveMode;
    }

    public void setInteractiveMode(InteractiveMode interactiveMode) {
        this.interactiveMode = interactiveMode;
    }

    @Override
    public void doStartup() {
        super.doStartup();

        if (interactiveMode != null) {
            interactiveMode.loop();
            shutdown();
        }
    }

    public static StandaloneEngineBuilder builder() {
        return new StandaloneEngineBuilder(new StandaloneEngine());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
