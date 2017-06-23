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

package org.openksavi.sponge.engine;

import java.util.concurrent.ExecutorService;

import org.openksavi.sponge.util.Processable;

/**
 * Thread Pool Manager.
 */
public interface ThreadPoolManager extends EngineModule {

    /**
     * Adds a processable.
     *
     * @param processable
     *            a processable.
     */
    void addProcessable(Processable processable);

    /**
     * Adds a processable.
     *
     * @param processable
     *            a processable.
     * @param workers
     *            amount of worker threads.
     */
    void addProcessable(Processable processable, int workers);

    /**
     * Pauses this thread pool manager.
     */
    void pause();

    /**
     * Resumes this thread pool manager.
     */
    void resume();

    /**
     * Adds an executor.
     *
     * @return executor.
     */
    ExecutorService addMainProcessingUnitWorkerExecutor();

    /**
     * Adds an executor.
     *
     * @return executor.
     */
    ExecutorService addAsyncEventSetProcessorExecutor();

    /**
     * Returns an executor.
     *
     * @param name
     *            executor name.
     * @return executor.
     */
    ExecutorService getExecutor(String name);
}
