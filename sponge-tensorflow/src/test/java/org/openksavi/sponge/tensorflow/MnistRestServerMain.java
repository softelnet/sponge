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

package org.openksavi.sponge.tensorflow;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import org.openksavi.sponge.camel.SpongeCamelConfiguration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.logging.LoggingUtils;
import org.openksavi.sponge.spring.SpringSpongeEngine;

public class MnistRestServerMain {

    private GenericApplicationContext context;

    @Configuration
    public static class Config extends SpongeCamelConfiguration {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin()).config("examples/tensorflow/mnist/mnist_rest_server.xml").build();
        }
    }

    public void run() {
        LoggingUtils.initLoggingBridge();

        context = new AnnotationConfigApplicationContext(Config.class);
        SpongeUtils.registerShutdownHook(context.getBean(SpongeEngine.class));
        context.start();
    }

    /**
     * Main method.
     *
     * @param args arguments.
     */
    public static void main(String... args) {
        new MnistRestServerMain().run();
    }
}
