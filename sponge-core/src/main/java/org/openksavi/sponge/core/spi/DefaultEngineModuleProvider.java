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

package org.openksavi.sponge.core.spi;

import org.openksavi.sponge.core.engine.DefaultActionManager;
import org.openksavi.sponge.core.engine.DefaultConfigurationManager;
import org.openksavi.sponge.core.engine.DefaultEventQueueManager;
import org.openksavi.sponge.core.engine.DefaultKnowledgeBaseManager;
import org.openksavi.sponge.core.engine.DefaultPluginManager;
import org.openksavi.sponge.core.engine.DefaultProcessingUnitManager;
import org.openksavi.sponge.core.engine.DefaultProcessorManager;
import org.openksavi.sponge.core.engine.DefaultStatisticsManager;
import org.openksavi.sponge.core.engine.DefaultThreadPoolManager;
import org.openksavi.sponge.core.engine.event.QuartzEventScheduler;
import org.openksavi.sponge.engine.ActionManager;
import org.openksavi.sponge.engine.ConfigurationManager;
import org.openksavi.sponge.engine.EventQueueManager;
import org.openksavi.sponge.engine.KnowledgeBaseManager;
import org.openksavi.sponge.engine.PluginManager;
import org.openksavi.sponge.engine.ProcessingUnitManager;
import org.openksavi.sponge.engine.ProcessorManager;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.StatisticsManager;
import org.openksavi.sponge.engine.ThreadPoolManager;
import org.openksavi.sponge.engine.event.EventQueue;
import org.openksavi.sponge.engine.event.EventScheduler;
import org.openksavi.sponge.spi.EngineModuleProvider;

/**
 * Default provider for engine modules.
 */
public class DefaultEngineModuleProvider implements EngineModuleProvider {

    @Override
    public ConfigurationManager createConfigurationManager(SpongeEngine engine, String configurationFilename) {
        return new DefaultConfigurationManager(engine, configurationFilename);
    }

    @Override
    public PluginManager createPluginManager(SpongeEngine engine) {
        return new DefaultPluginManager(engine);
    }

    @Override
    public KnowledgeBaseManager createKnowledgeBaseManager(SpongeEngine engine) {
        return new DefaultKnowledgeBaseManager(engine);
    }

    @Override
    public ThreadPoolManager createThreadPoolManager(SpongeEngine engine) {
        return new DefaultThreadPoolManager(engine);
    }

    @Override
    public ProcessorManager createProcessorManager(SpongeEngine engine) {
        return new DefaultProcessorManager(engine);
    }

    @Override
    public ActionManager createActionManager(SpongeEngine engine) {
        return new DefaultActionManager(engine);
    }

    @Override
    public StatisticsManager createStatisticsManager(SpongeEngine engine) {
        return new DefaultStatisticsManager(engine);
    }

    @Override
    public ProcessingUnitManager createProcessingUnitManager(SpongeEngine engine) {
        return new DefaultProcessingUnitManager(engine);
    }

    @Override
    public EventQueueManager createEventQueueManager(SpongeEngine engine) {
        return new DefaultEventQueueManager(engine);
    }

    @Override
    public EventScheduler createEventScheduler(SpongeEngine engine, EventQueue queue) {
        return new QuartzEventScheduler(engine, queue);
    }
}
