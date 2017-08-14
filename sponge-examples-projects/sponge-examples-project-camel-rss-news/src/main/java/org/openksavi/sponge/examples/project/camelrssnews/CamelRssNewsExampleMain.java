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

package org.openksavi.sponge.examples.project.camelrssnews;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import org.openksavi.sponge.engine.Engine;

/**
 * Example class containing main method.
 */
public class CamelRssNewsExampleMain {

    /** Spring context. */
    private GenericApplicationContext context;

    /**
     * Starts up Spring context (with the engine) manually.
     */
    public void startup() {
        if (context != null) {
            return;
        }

        // Starting Spring context.
        context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        context.registerShutdownHook();
        context.start();
    }

    public Engine getEngine() {
        return context.getBean(Engine.class);
    }

    /**
     * Shutdown Spring context.
     */
    public void shutdown() {
        if (context != null) {
            context.stop();
            context.close();
            context = null;
        }
    }

    /**
     * Main method. Arguments are ignored.
     */
    public static void main(String... args) {
        new CamelRssNewsExampleMain().startup();
    }
}
