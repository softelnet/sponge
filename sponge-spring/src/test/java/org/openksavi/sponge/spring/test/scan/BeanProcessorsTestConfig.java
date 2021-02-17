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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.spring.SpringPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@Configuration
@ComponentScan
public class BeanProcessorsTestConfig {

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

    public static class TestAction extends JAction {

        public String onCall(String text) {
            return text;
        }
    }
}
