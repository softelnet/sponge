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

package org.openksavi.sponge.camel;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

import org.openksavi.sponge.engine.Engine;

/**
 * Sponge Camel consumer.
 */
public class SpongeConsumer extends DefaultConsumer {

    private final Engine engine;

    private CamelConsumer camelConsumer;

    public SpongeConsumer(SpongeEndpoint endpoint, Processor processor, Engine engine) {
        super(endpoint, processor);

        this.engine = engine;
    }

    protected CamelPlugin getPlugin() {
        return engine.getPluginManager().getPlugin(CamelPlugin.NAME, CamelPlugin.class);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        camelConsumer = new DefaultCamelConsumer((SpongeEndpoint) getEndpoint(), getProcessor());

        getPlugin().addConsumer(camelConsumer);
    }

    @Override
    protected void doStop() throws Exception {
        getPlugin().removeConsumer(camelConsumer);

        camelConsumer = null;

        super.doStop();
    }
}
