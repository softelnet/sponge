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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.EventClonePolicy;
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
            assertEquals("source value", engine.getConfigurationManager().getProperty("resultProperty"));
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
            assertEquals("Descriptive engine", engine.getDisplayName());
            assertEquals("Engine description", engine.getDescription());
            assertEquals("Sponge (descriptiveEngine) " + engine.getVersion(), engine.getInfo());

            KnowledgeBase kb = engine.getKnowledgeBaseManager().getKnowledgeBase("sampleKb");
            assertEquals("sampleKb", kb.getName());
            assertEquals("Sample knowledge base", kb.getDisplayName());
            assertEquals("Sample knowledge base description", kb.getDescription());

            Plugin plugin = engine.getPluginManager().getPlugin("echoPlugin");
            assertEquals("echoPlugin", plugin.getName());
            assertEquals("Echo plugin", plugin.getDisplayName());
            assertEquals("Echo plugin description", plugin.getDescription());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
