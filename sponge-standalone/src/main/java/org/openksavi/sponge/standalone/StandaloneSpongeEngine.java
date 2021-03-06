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

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.openksavi.sponge.core.engine.BaseSpongeEngine;
import org.openksavi.sponge.engine.interactive.InteractiveMode;

/**
 * A standalone Sponge engine.
 */
public class StandaloneSpongeEngine extends BaseSpongeEngine implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private InteractiveMode interactiveMode;

    private List<String> args;

    public StandaloneSpongeEngine() {
        //
    }

    public InteractiveMode getInteractiveMode() {
        return interactiveMode;
    }

    public void setInteractiveMode(InteractiveMode interactiveMode) {
        this.interactiveMode = interactiveMode;
    }

    public static StandaloneEngineBuilder builder() {
        return new StandaloneEngineBuilder(new StandaloneSpongeEngine());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }
}
