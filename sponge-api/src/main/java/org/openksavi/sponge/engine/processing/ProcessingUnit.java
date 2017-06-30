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

package org.openksavi.sponge.engine.processing;

import java.util.Map;

import org.openksavi.sponge.EventProcessorAdapter;
import org.openksavi.sponge.engine.EngineModule;
import org.openksavi.sponge.util.Processable;

/**
 * Processing Unit.
 */
public interface ProcessingUnit<T extends EventProcessorAdapter<?>> extends EngineModule, Processable {

    /**
     * Returns registered processor adapter map.
     *
     * @return registered processor adapter map.
     */
    Map<String, T> getRegisteredProcessorAdapterMap();

    /**
     * Adds a new processor to this processing unit.
     *
     * @param processor processor.
     */
    void addProcessor(T processor);

    /**
     * Removes processor specified by its name.
     *
     * @param name processor name.
     */
    void removeProcessor(String name);

    /**
     * Removes all processors.
     */
    void removeAllProcessors();

    /**
     * Returns {@code true} if a processor named {@code name} exists.
     *
     * @param name processor name.
     * @return {@code true} if a processor named {@code name} exists.
     */
    boolean existsProcessor(String name);

    /**
     * Returns {@code true} if this processing unit supports thread pool to listen to an event queue concurrently instead of only one
     * thread.
     *
     * @return {@code true} if this processing unit supports thread pool to listen to an event queue concurrently.
     */
    boolean supportsConcurrentListenerThreadPool();
}
