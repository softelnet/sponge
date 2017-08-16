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

package org.openksavi.sponge.spi;

import org.openksavi.sponge.engine.ActionManager;
import org.openksavi.sponge.engine.ConfigurationManager;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.EventQueueManager;
import org.openksavi.sponge.engine.KnowledgeBaseManager;
import org.openksavi.sponge.engine.PluginManager;
import org.openksavi.sponge.engine.ProcessingUnitManager;
import org.openksavi.sponge.engine.ProcessorManager;
import org.openksavi.sponge.engine.StatisticsManager;
import org.openksavi.sponge.engine.ThreadPoolManager;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.engine.event.EventScheduler;

/**
 * Provider for engine modules.
 */
public interface EngineModuleProvider {

    /**
     * Creates a new configuration manager.
     *
     * @param engine the engine.
     * @param configurationFilename configuration file name.
     * @return a new configuration manager.
     */
    ConfigurationManager createConfigurationManager(Engine engine, String configurationFilename);

    /**
     * Creates a new plugin manager.
     *
     * @param engine the engine.
     * @return new plugin manager.
     */
    PluginManager createPluginManager(Engine engine);

    /**
     * Creates a new knowledge base manager.
     *
     * @param engine the engine.
     * @return new knowledge base manager.
     */
    KnowledgeBaseManager createKnowledgeBaseManager(Engine engine);

    /**
     * Creates a new thread pool manager.
     *
     * @param engine the engine.
     * @return new thread pool manager.
     */
    ThreadPoolManager createThreadPoolManager(Engine engine);

    /**
     * Creates a new processors manager.
     *
     * @param engine the engine.
     * @return new processors manager.
     */
    ProcessorManager createProcessorManager(Engine engine);

    /**
     * Creates a new action manager.
     *
     * @param engine the engine.
     * @return new action manager.
     */
    ActionManager createActionManager(Engine engine);

    /**
     * Creates a new statistics manager.
     *
     * @param engine the engine.
     * @return new statistics manager.
     */
    StatisticsManager createStatisticsManager(Engine engine);

    /**
     * Creates a new processing unit manager.
     *
     * @param engine the engine.
     * @return new processing unit manager.
     */
    ProcessingUnitManager createProcessingUnitManager(Engine engine);

    /**
     * Creates a new event queue manager.
     *
     * @param engine the engine.
     * @return new event queue manager.
     */
    EventQueueManager createEventQueueManager(Engine engine);

    /**
     * Creates a new event scheduler.
     *
     * @param engine the engine.
     * @param queue event queue where generated events will be sent to.
     * @return new event scheduler.
     */
    EventScheduler createEventScheduler(Engine engine, EventQueue queue);
}
