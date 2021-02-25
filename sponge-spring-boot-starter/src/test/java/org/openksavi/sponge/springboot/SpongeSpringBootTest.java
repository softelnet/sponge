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

package org.openksavi.sponge.springboot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import org.openksavi.sponge.engine.ConfigurationManager;
import org.openksavi.sponge.event.EventClonePolicy;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@SpringBootTest
@SpringBootApplication
public class SpongeSpringBootTest {

    @Autowired
    private SpringSpongeEngine engine;

    @Autowired
    private SpongeProperties spongeProperties;

    @Test
    public void springBootProperties() {
        // Properties.
        assertEquals("sponge_config.xml", spongeProperties.getConfigFile());

        ConfigurationManager confMgr = engine.getConfigurationManager();

        // Configuration manager.
        assertEquals("src/test/resources/sponge", confMgr.getHome());

        // Engine.
        assertEquals("Sponge name", engine.getName());
        assertEquals("Sponge label", engine.getLabel());
        assertEquals("Sponge description", engine.getDescription());
        assertEquals("Sponge license", engine.getLicense());

        assertEquals("value1", confMgr.getProperty("property1"));
        assertEquals("value2", confMgr.getProperty("property2"));

        assertEquals("value1", System.getProperty("systemProperty1"));
        assertEquals("value2", System.getProperty("systemProperty2"));

        assertEquals("value1", engine.getOperations().getVariable("variableProperty1"));
        assertEquals("value2", engine.getOperations().getVariable("variableProperty2"));

        assertEquals("defaultKb", engine.getDefaultKnowledgeBaseName());

        assertTrue(engine.isAutoStartup());

        assertEquals(10, engine.getPhase());

        assertEquals("defaultKb", engine.getProcessorBeansKnowledgeBaseName());

        assertEquals(7, confMgr.getMainProcessingUnitThreadCount());
        assertEquals(EventClonePolicy.DEEP, confMgr.getEventClonePolicy());
        assertEquals(1001, confMgr.getEventQueueCapacity());
        assertEquals(16, confMgr.getDurationThreadCount());
        assertEquals(17, confMgr.getAsyncEventSetProcessorExecutorThreadCount());
        assertEquals(true, confMgr.getEventSetProcessorDefaultSynchronous());
        assertEquals(true, confMgr.getAutoEnable());
        assertEquals(1111, confMgr.getExecutorShutdownTimeout());
    }

    @Test
    public void hasActions() {
        assertEquals("TO UPPER", engine.getOperations().call(String.class, "UpperCase", Arrays.asList("to upper")));
        assertEquals("to lower", engine.getOperations().call(String.class, "LowerCase", Arrays.asList("TO LOWER")));
    }
}
