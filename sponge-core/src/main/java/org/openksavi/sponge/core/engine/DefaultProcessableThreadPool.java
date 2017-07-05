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

package org.openksavi.sponge.core.engine;

import java.util.concurrent.ExecutorService;

import org.openksavi.sponge.engine.ProcessableThreadPool;
import org.openksavi.sponge.util.Processable;

public class DefaultProcessableThreadPool extends DefaultThreadPool implements ProcessableThreadPool {

    private Processable processable;

    public DefaultProcessableThreadPool(ExecutorService executor, Processable processable) {
        super(processable.toString(), executor);

        this.processable = processable;
    }

    @Override
    public Processable getProcessable() {
        return processable;
    }

    public void setProcessable(Processable processable) {
        this.processable = processable;
    }
}
