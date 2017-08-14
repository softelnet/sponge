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

package org.openksavi.sponge.camel;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import org.openksavi.sponge.engine.Engine;

/**
 * Sponge Camel component.
 */
public class SpongeComponent extends DefaultComponent {

    private Engine engine;

    private String action;

    private Boolean managed;

    public SpongeComponent() {
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        SpongeEndpoint endpoint = new SpongeEndpoint(uri, this, engine, action, managed);

        endpoint.setEngineRef(remaining);
        setProperties(endpoint, parameters);

        return endpoint;
    }

    public Engine getEngine() {
        return engine;
    }

    /**
     * To use the given Sponge engine instance.
     *
     * @param engine the engine.
     */
    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getManaged() {
        return managed;
    }

    public void setManaged(Boolean managed) {
        this.managed = managed;
    }
}
