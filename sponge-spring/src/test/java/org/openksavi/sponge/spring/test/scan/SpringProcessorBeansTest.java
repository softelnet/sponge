/*
 * Copyright 2016-2021 The Sponge authors.
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

package org.openksavi.sponge.spring.test.scan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.openksavi.sponge.engine.SpongeEngine;

public class SpringProcessorBeansTest {

    @Test
    public void testSpringProcessorBeans() {
        AnnotationConfigApplicationContext ctx = null;

        try {
            ctx = new AnnotationConfigApplicationContext(BeanProcessorsTestConfig.class);

            ctx.start();

            SpongeEngine engine = ctx.getBean(SpongeEngine.class);

            assertEquals("text", engine.getOperations().call(String.class, "TestAction", Arrays.asList("text")));

            assertEquals("SERVICE_NAME text", engine.getOperations().call(String.class, "Test2Action", Arrays.asList("text")));
        } finally {
            if (ctx != null) {
                ctx.close();
            }
        }
    }
}
