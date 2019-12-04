/*
 * Copyright 2016-2019 The Sponge authors.
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

public class StandaloneSettings {

    public static final String VARIABLE_NAME = "standaloneSettings";

    public static final String DEFAULT_ENGINE_BEAN_NAME = "spongeEngine";

    /** Spring configuration files. */
    private List<String> springConfigurationFiles;

    /** A bean name for the engine. */
    private String engineBeanName = DEFAULT_ENGINE_BEAN_NAME;

    /** Equals to {@code true} if a Camel context should be created. */
    private boolean camel = false;

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

    public List<String> getSpringConfigurationFiles() {
        return springConfigurationFiles;
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
}
