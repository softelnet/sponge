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

package org.openksavi.sponge.integration.tests.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.core.engine.ConfigurationConstants;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.EventClonePolicy;
import org.openksavi.sponge.examples.EchoPlugin;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;

public class ConfigurationTest {

    @Test
    public void testEngineParameters() {
        SpongeEngine engine = DefaultSpongeEngine.builder().config("examples/core/configuration/engine_parameters.xml").build();
        engine.startup();

        try {
            assertEquals("EngineParametersTest", engine.getName());
            assertEquals(21, engine.getConfigurationManager().getMainProcessingUnitThreadCount());
            assertEquals(engine.getConfigurationManager().getMainProcessingUnitThreadCount(),
                    engine.getConfigurationManager().getAsyncEventSetProcessorExecutorThreadCount());
            assertEquals(EventClonePolicy.DEEP, engine.getConfigurationManager().getEventClonePolicy());
            assertEquals(false, engine.getConfigurationManager().getAutoEnable());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testPropertySubstitution() {
        SpongeEngine engine = DefaultSpongeEngine.builder().property("sourceProperty", "source")
                .config("examples/core/configuration/property_substitution.xml").build();
        engine.startup();

        try {
            assertEquals("source value", engine.getOperations().getProperty("resultProperty"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testRelativeFiles() {
        SpongeEngine engine = DefaultSpongeEngine.builder().config("examples/core/configuration/relative_files.xml").build();
        engine.startup();

        try {
            assertTrue(engine.getOperations().getVariable(Boolean.class, "loaded"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKbFileInClasspathInOtherArtifact() {
        SpongeEngine engine = SpringSpongeEngine.builder().config("examples/core/configuration/kb_file_in_classpath.xml").build();
        engine.startup();

        try {
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testDescriptive() {
        SpongeEngine engine = SpringSpongeEngine.builder().config("examples/core/configuration/descriptive.xml").build();
        engine.startup();

        try {
            assertEquals("descriptiveEngine", engine.getName());
            assertEquals("Descriptive engine", engine.getLabel());
            assertEquals("Engine description", engine.getDescription());
            assertEquals("Sponge (descriptiveEngine) " + engine.getVersion(), engine.getInfo());
            assertEquals("Engine license", engine.getLicense());

            KnowledgeBase kb = engine.getKnowledgeBaseManager().getKnowledgeBase("sampleKb");
            assertEquals("sampleKb", kb.getName());
            assertEquals("Sample knowledge base", kb.getLabel());
            assertEquals("Sample knowledge base description", kb.getDescription());

            Plugin plugin = engine.getOperations().getPlugin("echoPlugin");
            assertEquals("echoPlugin", plugin.getName());
            assertEquals("Echo plugin", plugin.getLabel());
            assertEquals("Echo plugin description", plugin.getDescription());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testConfigProperties() {
        String home = ".";
        SpongeEngine engine = DefaultSpongeEngine.builder().config("examples/core/configuration/config_file_properties.xml")
                .property(ConfigurationConstants.PROP_HOME, home).build();
        engine.startup();

        try {
            String configurationFileDir = SpongeUtils.getFileDir(engine.getConfigurationManager().getConfigurationFileUrl());
            assertNotNull(configurationFileDir);
            assertEquals("Config directory: " + configurationFileDir,
                    engine.getOperations().getPlugin(EchoPlugin.class, "echoPlugin1").getEcho());

            assertEquals("Config directory text: " + configurationFileDir, engine.getOperations().getProperty("configDirectoryText"));

            assertEquals("Home directory: " + home, engine.getOperations().getPlugin(EchoPlugin.class, "echoPlugin2").getEcho());

            assertEquals("Config directory text: " + configurationFileDir,
                    engine.getOperations().getPlugin(EchoPlugin.class, "echoPlugin3").getEcho());

            assertEquals("${nonExistingPropertyValue}", engine.getOperations().getPlugin(EchoPlugin.class, "echoPlugin4").getEcho());

            assertEquals("${nonExistingPropertyValue}", engine.getOperations().getVariable("nonExistingProperty"));

            assertEquals(System.getProperty("java.home"), engine.getOperations().getVariable("javaHome"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testConfigurationNonExisting() {
        String file = "examples/core/configuration_non_existing/non_existing_configuration_file.xml";
        SpongeEngine engine = SpringSpongeEngine.builder().config(file).build();
        try {
            engine.startup();

            fail("ConfigException expected");
        } catch (ConfigException e) {
            assertEquals("Configuration file " + file + " not found", e.getMessage());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testPropertiesFile() {
        String home = ".";
        SpongeEngine engine = DefaultSpongeEngine.builder().config("examples/core/configuration/config_with_properties_file.xml")
                .property(ConfigurationConstants.PROP_HOME, home).build();
        engine.startup();

        try {
            String configurationFileDir = SpongeUtils.getFileDir(engine.getConfigurationManager().getConfigurationFileUrl());
            assertNotNull(configurationFileDir);
            assertEquals("Config directory: " + configurationFileDir, engine.getOperations().getProperty("configDirectoryText"));
            assertEquals("TEST", engine.getOperations().getProperty("property.value"));

            assertNull(engine.getOperations().getProperty("nonExistingProperty", null));

            try {
                engine.getOperations().getProperty("nonExistingProperty");
                fail("Exception expected");
            } catch (IllegalArgumentException e) {
                assertEquals("Property 'nonExistingProperty' not found or empty", e.getMessage());
            }

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testConfigurationFileInHomeDirectory() {
        String home = "examples/core/configuration/config_in_home";
        SpongeEngine engine =
                DefaultSpongeEngine.builder().config("config_in_home.xml").property(ConfigurationConstants.PROP_HOME, home).build();
        engine.startup();

        try {
            assertEquals(home, engine.getConfigurationManager().getHome());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testConfigurationFileInHomeDirectoryNoHome() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().config("config_in_home.xml").property(ConfigurationConstants.PROP_HOME, null).build();

        assertEquals("Configuration file config_in_home.xml not found",
                assertThrows(ConfigException.class, () -> engine.startup()).getMessage());

        try {
            assertTrue(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
