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

package org.openksavi.sponge.core.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.openksavi.sponge.engine.ThreadPool;

/**
 * A default thread pool.
 */
public class DefaultThreadPool implements ThreadPool {

    private String name;

    private ExecutorService executor;

    private List<Future<?>> futures = Collections.synchronizedList(new ArrayList<>());

    public DefaultThreadPool(String name, ExecutorService executor) {
        this.name = name;
        this.executor = executor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Sets an executor.
     *
     * @param executor the executor to set.
     */
    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public List<Future<?>> getFutures() {
        return futures;
    }

    @Override
    public void clear() {
        executor = null;
        futures.clear();
    }
}
