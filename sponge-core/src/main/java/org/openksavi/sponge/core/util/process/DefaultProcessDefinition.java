/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.core.util.process;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.util.process.ProcessConfiguration;
import org.openksavi.sponge.util.process.ProcessDefinition;
import org.openksavi.sponge.util.process.ProcessInstance;

/**
 * A default process definition.
 */
public class DefaultProcessDefinition implements ProcessDefinition {

    private ProcessInstance process;

    public DefaultProcessDefinition(SpongeEngine engine, ProcessConfiguration configuration) {
        process = new DefaultProcessInstance(engine, configuration);
    }

    @Override
    public ProcessInstance run() throws InterruptedException {
        return process.run();
    }

    @Override
    public ProcessInstance getProcess() {
        return process;
    }
}
