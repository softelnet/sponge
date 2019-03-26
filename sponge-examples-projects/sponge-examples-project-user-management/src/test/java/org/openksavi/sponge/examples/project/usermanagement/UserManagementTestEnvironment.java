/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.examples.project.usermanagement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import org.openksavi.sponge.core.engine.ConfigurationConstants;
import org.openksavi.sponge.core.util.SpongeUtils;

public class UserManagementTestEnvironment {

    protected Server server;

    public void init() {
        System.setProperty(ConfigurationConstants.PROP_HOME, ".");
    }

    public void clear() {
    }

    public void start(int port) {
        try {
            server = new Server(port);
            server.setStopAtShutdown(true);
            WebAppContext webAppContext = new WebAppContext();
            webAppContext.setContextPath("/");
            webAppContext.setResourceBase("src/main/webapp");
            webAppContext.setClassLoader(getClass().getClassLoader());
            server.setHandler(webAppContext);
            server.start();
        } catch (Exception e) {
            SpongeUtils.wrapException(e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            SpongeUtils.wrapException(e);
        }
    }
}
