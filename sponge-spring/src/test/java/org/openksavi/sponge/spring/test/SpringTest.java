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

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.spring.SpringEngine;
import org.openksavi.sponge.spring.SpringPlugin;

public class SpringTest {

    private static final String BEAN_VALUE = "OK";

    @Configuration
    public static class TestConfig {

        @Bean
        public Engine spongeEngine() {
            return SpringEngine.builder().plugin(springPlugin()).knowledgeBase("kb", "examples/spring/spring.py").build();
        }

        @Bean
        public SpringPlugin springPlugin() {
            return new SpringPlugin();
        }

        @Bean
        public String testBean() {
            return BEAN_VALUE;
        }
    }

    @Test
    public void testSpring() throws InterruptedException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TestConfig.class);
        ctx.start();

        try {
            Engine engine = ctx.getBean(Engine.class);

            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(String.class, "springBeanValue") != null);

            assertEquals(BEAN_VALUE, engine.getOperations().getVariable(String.class, "springBeanValue"));
            assertFalse(engine.isError());
        } finally {
            ctx.close();
        }
    }
}
