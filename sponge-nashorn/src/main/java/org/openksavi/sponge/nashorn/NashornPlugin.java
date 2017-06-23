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

package org.openksavi.sponge.nashorn;

import org.openksavi.sponge.config.Configuration;

/**
 * JavaScript-specific implementation of the plugin.
 */
public abstract class NashornPlugin extends org.openksavi.sponge.core.plugin.BasePlugin implements NashornScriptObject {

    /** JavaScript processor used to overcome class inheritance limitations in JavaScript and Nashorn. Must be be thread safe. */
    private Object target;

    @Override
    public Object getSelf() {
        return this;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public final void configure(Configuration configuration) {
        configure(getSelf(), configuration);
    }

    @Override
    public final void init() {
        init(getSelf());
    }

    @Override
    public final void onStartup() {
        onStartup(getSelf());
    }

    @Override
    public final void onShutdown() {
        onShutdown(getSelf());
    }

    @Override
    public final void onBeforeReload() {
        onBeforeReload(getSelf());
    }

    @Override
    public final void onAfterReload() {
        onAfterReload(getSelf());
    }

    public void configure(Object self, Configuration configuration) {
        //
    }

    public void init(Object self) {
        //
    }

    public void onStartup(Object self) {
        //
    }

    public void onShutdown(Object self) {
        //
    }

    public void onBeforeReload(Object self) {
        //
    }

    public void onAfterReload(Object self) {
        //
    }
}
