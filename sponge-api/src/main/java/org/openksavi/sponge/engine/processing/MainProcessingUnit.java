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

package org.openksavi.sponge.engine.processing;

import java.util.List;

import org.openksavi.sponge.EventProcessorAdapter;
import org.openksavi.sponge.correlator.CorrelatorAdapterGroup;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.ThreadPool;
import org.openksavi.sponge.rule.RuleAdapterGroup;
import org.openksavi.sponge.trigger.TriggerAdapter;

/**
 * Main processing unit that handles triggers, rules and correlators.
 */
public interface MainProcessingUnit extends ProcessingUnit<EventProcessorAdapter<?>> {

    /**
     * Returns handler for the specified processor type.
     *
     * @param type processor type.
     * @return handler.
     */
    MainProcessingUnitHandler getHandler(ProcessorType type);

    /**
     * Returns the list of trigger adapters.
     *
     * @return the list of trigger adapters.
     */
    List<TriggerAdapter> getTriggerAdapters();

    /**
     * Returns the list of rule adapter groups.
     *
     * @return the list of rule adapter groups.
     */
    List<RuleAdapterGroup> getRuleAdapterGroups();

    /**
     * Returns the list of correlator adapter groups.
     *
     * @return the list of correlator adapter groups.
     */
    List<CorrelatorAdapterGroup> getCorrelatorAdapterGroups();

    /**
     * Returns {@code true} if a processor of type {@code type} named {@code name} is registered.
     *
     * @param name processor name.
     * @param type processor type.
     * @return {@code true} if a processor of type {@code type} named {@code name} is registered.
     */
    boolean hasProcessor(String name, ProcessorType type);

    /**
     * Returns the thread pool used by the Main Processing Unit for worker threads.
     *
     * @return the thread pool.
     */
    ThreadPool getWorkerThreadPool();

    /**
     * Returns the thread pool for applying during processing instances in event set processor group.
     *
     * @return the thread pool.
     */
    ThreadPool getAsyncEventSetProcessorThreadPool();
}
