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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.ProcessorNotFoundException;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.java.JRule;
import org.openksavi.sponge.spring.SpringPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;

public class SpringTest {

    private static final String BEAN_VALUE = "OK";

    @Configuration
    public static class TestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugin(springPlugin()).knowledgeBase("kb", "examples/spring/spring.py").build();
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

    @Configuration
    public static class ExistingKnowledgeBaseFileWildcardTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugin(springPlugin()).knowledgeBase("kb", "examples/spring/spring*.py").build();
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

    @Configuration
    public static class NonExistingKnowledgeBaseFileWildcardTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugin(springPlugin()).knowledgeBase("kb", "examples/spring/non_existing*.py").build();
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
            SpongeEngine engine = ctx.getBean(SpongeEngine.class);

            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(String.class, "springBeanValue") != null);

            assertEquals(BEAN_VALUE, engine.getOperations().getVariable(String.class, "springBeanValue"));
            assertFalse(engine.isError());
        } finally {
            ctx.close();
        }
    }

    @Test
    public void testSpringKnowledgeBaseWildcardExisting() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExistingKnowledgeBaseFileWildcardTestConfig.class);
        ctx.start();

        try {
            SpongeEngine engine = ctx.getBean(SpongeEngine.class);

            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(String.class, "springBeanValue") != null);

            assertEquals(BEAN_VALUE, engine.getOperations().getVariable(String.class, "springBeanValue"));
            assertFalse(engine.isError());
        } finally {
            ctx.close();
        }
    }

    @Test
    public void testSpringKnowledgeBaseWildcardNonExisting() {
        AnnotationConfigApplicationContext ctx = null;

        try {
            ctx = new AnnotationConfigApplicationContext(NonExistingKnowledgeBaseFileWildcardTestConfig.class);

            ctx.start();
            fail("Exception expected");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof ConfigException);
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    @Configuration
    public static class BeanProcessorsTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugin(springPlugin()).build();
        }

        @Bean
        public SpringPlugin springPlugin() {
            return new SpringPlugin();
        }

        @Bean
        public TestAction testAction() {
            return new TestAction();
        }
    }

    public static class TestAction extends JAction {

        public String onCall(String text) {
            return text;
        }
    }

    @Test
    public void testSpringProcessorBeans() {
        AnnotationConfigApplicationContext ctx = null;

        try {
            ctx = new AnnotationConfigApplicationContext(BeanProcessorsTestConfig.class);

            ctx.start();

            SpongeEngine engine = ctx.getBean(SpongeEngine.class);

            String result = engine.getOperations().call(String.class, "TestAction", Arrays.asList("text"));

            assertEquals("text", result);
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    @Configuration
    public static class BeanProcessorsErrorTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugin(springPlugin()).build();
        }

        @Bean
        public SpringPlugin springPlugin() {
            return new SpringPlugin();
        }

        @Bean
        public TestRule testRule() {
            return new TestRule();
        }
    }

    public static class TestRule extends JRule {

        @Override
        public void onConfigure() {
            withEvents("e1", "e2");
        }

        @Override
        public void onRun(Event event) {
        }
    }

    @Test
    public void testSpringProcessorBeansError() {
        AnnotationConfigApplicationContext ctx = null;

        try {
            ctx = new AnnotationConfigApplicationContext(BeanProcessorsErrorTestConfig.class);

            ctx.start();
            fail("Exception expected");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof SpongeException);
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }

    @Configuration
    public static class BeanProcessorsDisableConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugin(springPlugin()).build();
        }

        @Bean
        public SpringPlugin springPlugin() {
            return new SpringPlugin();
        }

        @Bean
        public TestDisableAction testAction() {
            return new TestDisableAction();
        }
    }

    public static class TestDisableAction extends JAction {

        public String onCall(String text) {
            return text;
        }
    }

    @Test
    public void testSpringProcessorBeansDisable() {
        AnnotationConfigApplicationContext ctx = null;

        try {
            ctx = new AnnotationConfigApplicationContext(BeanProcessorsDisableConfig.class);

            ctx.start();

            SpongeEngine engine = ctx.getBean(SpongeEngine.class);

            String actionName = "TestDisableAction";

            engine.getOperations().call(String.class, actionName, Arrays.asList("text"));

            engine.getOperations().disableJava(ctx.getBean(TestDisableAction.class));

            engine.getOperations().call(String.class, actionName, Arrays.asList("text"));

            fail("Exception expected");
        } catch (Exception e) {
            assertTrue(e instanceof ProcessorNotFoundException);
            assertEquals("Action TestDisableAction not found", e.getMessage());
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }
}
