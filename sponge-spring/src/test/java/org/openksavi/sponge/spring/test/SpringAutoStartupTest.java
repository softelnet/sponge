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

package org.openksavi.sponge.spring.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.spring.SpringEngine;
import org.openksavi.sponge.spring.SpringPlugin;

public class SpringAutoStartupTest {

    @Configuration
    public static class TestConfigAutoStartupTrue {

        @Bean
        public Engine spongeEngine() {
            return SpringEngine.builder().autoStartup(true).plugin(springPlugin()).knowledgeBase("kb", "examples/spring/spring.py").build();
        }

        @Bean
        public SpringPlugin springPlugin() {
            return new SpringPlugin();
        }

        @Bean
        public String testBean() {
            return "OK";
        }
    }

    @Test
    public void testSpringAutoStartupTrue() throws InterruptedException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TestConfigAutoStartupTrue.class);
        ctx.start();

        try {
            Engine engine = ctx.getBean(Engine.class);

            assertTrue(engine.isRunning());
            assertFalse(engine.isError());
        } finally {
            ctx.close();
        }
    }

    @Configuration
    public static class TestConfigAutoStartupFalse {

        @Bean
        public Engine spongeEngine() {
            return SpringEngine.builder().autoStartup(false).plugin(springPlugin()).knowledgeBase("kb", "examples/spring/spring.py")
                    .build();
        }

        @Bean
        public SpringPlugin springPlugin() {
            return new SpringPlugin();
        }
    }

    @Test
    public void testSpringAutoStartupFalse() throws InterruptedException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TestConfigAutoStartupFalse.class);
        ctx.start();

        try {
            Engine engine = ctx.getBean(Engine.class);

            assertFalse(engine.isRunning());
            assertFalse(engine.isError());
        } finally {
            ctx.close();
        }
    }
}
