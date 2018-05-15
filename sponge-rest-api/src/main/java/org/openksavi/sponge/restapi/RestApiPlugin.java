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

package org.openksavi.sponge.restapi;

import org.apache.camel.CamelContext;

import org.openksavi.sponge.camel.CamelPlugin;
import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JPlugin;

/**
 * Sponge REST API plugin.
 */
public class RestApiPlugin extends JPlugin {

    public static final String NAME = "restApi";

    private RestApiSettings settings = new RestApiSettings();

    public RestApiPlugin() {
        setName(NAME);
    }

    @Override
    public void onConfigure(Configuration configuration) {
        settings.setRestComponentId(configuration.getString(RestApiConstants.TAG_REST_COMPONENT_ID, settings.getRestComponentId()));
        settings.setHost(configuration.getString(RestApiConstants.TAG_HOST, settings.getHost()));
        settings.setPort(configuration.getInteger(RestApiConstants.TAG_PORT, settings.getPort()));
        settings.setPrettyPrint(configuration.getBoolean(RestApiConstants.TAG_PRETTY_PRINT, settings.isPrettyPrint()));
    }

    public RestApiPlugin(String name) {
        super(name);
    }

    public RestApiSettings getSettings() {
        return settings;
    }

    public void start() {
        CamelPlugin camelPlugin = getEngine().getPluginManager().getPlugin(CamelPlugin.class);
        if (camelPlugin == null) {
            throw new ConfigException("Camel plugin is not registered but it is required by the Sponge REST API");
        }

        start(camelPlugin.getCamelContext());
    }

    public void start(CamelContext camelContext) {
        if (camelContext == null) {
            throw new ConfigException("Camel context is not available");
        }

        try {
            camelContext.addRoutes(new RestApiRouteBuilder(getEngine(), new RestApiService(getEngine()), settings));
        } catch (Exception e) {
            throw SpongeUtils.wrapException(e);
        }
    }
}
