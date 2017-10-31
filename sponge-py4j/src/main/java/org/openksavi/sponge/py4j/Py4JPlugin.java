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

package org.openksavi.sponge.py4j;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.java.JavaPlugin;

/**
 * Sponge plugin that provides integration with CPython using Py4J.
 */
public abstract class Py4JPlugin<T> extends JavaPlugin {

    public static final String DEFAULT_NAME = "py4j";

    private String facadeInterfaceName;

    private T facade;

    public Py4JPlugin() {
        setName(DEFAULT_NAME);
    }

    public T getFacade() {
        return facade;
    }

    public void setFacade(T facade) {
        this.facade = facade;
    }

    public String getFacadeInterfaceName() {
        return facadeInterfaceName;
    }

    public void setFacadeInterfaceName(String facadeInterfaceName) {
        this.facadeInterfaceName = facadeInterfaceName;
    }

    @Override
    public void onConfigure(Configuration configuration) {
        facadeInterfaceName = configuration.getString("facadeInterface", facadeInterfaceName);
    }
}
