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

package org.openksavi.sponge.integration.tests.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.kb.FileKnowledgeBaseScript;
import org.openksavi.sponge.engine.SpongeEngine;

public class ConfigurationKnowledgeBaseFileWildcardsTest {

    @Test
    public void testKnowledgeBaseFileWildcardBuilderSelected() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase("kb", "examples/core/wildcards/knowledge_base_file_wildcards_*.py").build();
        engine.startup();

        try {
            assertEquals(3, engine.getActions().size());
            assertTrue(engine.getOperations().hasAction("Action1"));
            assertTrue(engine.getOperations().hasAction("Action2"));
            assertTrue(engine.getOperations().hasAction("Action3"));
            assertFalse(engine.getOperations().hasAction("Action0"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKnowledgeBaseFileWildcardBuilderAll() {
        SpongeEngine engine = DefaultSpongeEngine.builder().knowledgeBase("kb", "examples/core/wildcards/*.py").build();
        engine.startup();

        try {
            assertEquals(4, engine.getActions().size());
            assertTrue(engine.getOperations().hasAction("Action1"));
            assertTrue(engine.getOperations().hasAction("Action2"));
            assertTrue(engine.getOperations().hasAction("Action3"));
            assertTrue(engine.getOperations().hasAction("Action0"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKnowledgeBaseFileWildcardBuilderOptionalNonExistingDirectory() {
        SpongeEngine engine = DefaultSpongeEngine.builder().knowledgeBase("kb", new FileKnowledgeBaseScript("examples/core/wildcards/*.py"),
                new FileKnowledgeBaseScript("examples/core/wildcards_nonexisting/*.py", false)).build();
        engine.startup();

        try {
            assertEquals(4, engine.getActions().size());
            assertTrue(engine.getOperations().hasAction("Action1"));
            assertTrue(engine.getOperations().hasAction("Action2"));
            assertTrue(engine.getOperations().hasAction("Action3"));
            assertTrue(engine.getOperations().hasAction("Action0"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKnowledgeBaseFileWildcardBuilderNonOptionalNonExistingFile() {
        SpongeEngine engine = DefaultSpongeEngine.builder().knowledgeBase("kb", "examples/core/wildcards/nonexisting_*.py").build();

        try {
            assertThrows(ConfigException.class, () -> engine.startup());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKnowledgeBaseFileWildcardBuilderOptionalNonExistingFile() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase("kb", new FileKnowledgeBaseScript("examples/core/wildcards/nonexisting_*.py", false)).build();

        try {
            engine.startup();

            assertEquals(0, engine.getActions().size());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKnowledgeBaseFileWildcardBuilderOptionalNonExistingFileUnsupportedExtension() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase("kb", new FileKnowledgeBaseScript("examples/core/wildcards/nonexisting_*.*", false)).build();

        try {
            assertThrows(SpongeException.class, () -> engine.startup());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKnowledgeBaseFileWildcardConfigFile() {
        SpongeEngine engine = DefaultSpongeEngine.builder().config("examples/core/wildcards/knowledge_base_file_wildcards.xml").build();
        engine.startup();

        try {
            assertEquals(3, engine.getActions().size());
            assertTrue(engine.getOperations().hasAction("Action1"));
            assertTrue(engine.getOperations().hasAction("Action2"));
            assertTrue(engine.getOperations().hasAction("Action3"));
            assertFalse(engine.getOperations().hasAction("Action0"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKnowledgeBaseFileWildcardConfigFileHome() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().config("examples/core/wildcards/knowledge_base_file_wildcards_home.xml").build();
        engine.startup();

        try {
            assertEquals(3, engine.getActions().size());
            assertTrue(engine.getOperations().hasAction("Action1"));
            assertTrue(engine.getOperations().hasAction("Action2"));
            assertTrue(engine.getOperations().hasAction("Action3"));
            assertFalse(engine.getOperations().hasAction("Action0"));

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

}
