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

import org.openksavi.sponge.core.engine.EngineBuilder;

/**
 * SpringEngine builder.
 */
public class SpringEngineBuilder extends EngineBuilder<SpringEngine> {

    public SpringEngineBuilder(SpringEngine engine) {
        super(engine);
    }

    /**
     * Sets {@code autoStartup} meaning that if it is {@code true} (the default value), the engine will be automatically started up in the
     * {@code afterPropertiesSet} Spring callback method. Moreover the engine will be shut down in the {@code destroy} Spring callback
     * method.
     *
     * @param autoStartup the autoStartup flag.
     * @return this builder.
     */
    public SpringEngineBuilder autoStartup(boolean autoStartup) {
        engine.setAutoStartup(autoStartup);
        return this;
    }
}
