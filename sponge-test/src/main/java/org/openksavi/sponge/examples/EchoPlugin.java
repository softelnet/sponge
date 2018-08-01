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

package org.openksavi.sponge.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.java.JPlugin;

public class EchoPlugin extends JPlugin {

    private static final Logger logger = LoggerFactory.getLogger(EchoPlugin.class);

    private String echo = "noecho";

    private int count = 1;

    public EchoPlugin() {
        //
    }

    public EchoPlugin(String name) {
        super(name);
    }

    @Override
    public void onConfigure(Configuration configuration) {
        echo = configuration.getString("echo", echo);
        count = configuration.getInteger("count", count);
    }

    @Override
    public void onInit() {
        logger.debug("Initializing {}", getName());
    }

    @Override
    public void onStartup() {
        logger.debug("Starting up {}", getName());
    }

    public String getEcho() {
        return echo;
    }

    public void setEcho(String echo) {
        this.echo = echo;
    }

    public int getCount() {
        return count;
    }

    public String getEchoConfig() {
        return echo + " x " + count;
    }

    public void sendEchoEvent() {
        getSponge().event("echoEvent").set("echo", getEcho()).send();
    }
}
