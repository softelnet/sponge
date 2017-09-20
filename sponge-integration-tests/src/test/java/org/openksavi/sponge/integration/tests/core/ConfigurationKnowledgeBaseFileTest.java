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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.engine.Engine;

public class ConfigurationKnowledgeBaseFileTest {

    private static final String DIR = "examples/core/configuration/kb_file/";

    private void doTestExisting(String config) {
        Engine engine = DefaultEngine.builder().config(DIR + config).build();
        engine.startup();

        try {
            assertTrue(engine.getOperations().getVariable(Boolean.class, "loaded"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKnowledgeBaseFileOptionalNonExisting() {
        Engine engine = DefaultEngine.builder().config(DIR + "configuration_kb_file_optional_non_existing.xml").build();
        engine.startup();

        try {
            assertFalse(engine.getOperations().existsVariable("loaded"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testKnowledgeBaseFileOptionalExisting() {
        doTestExisting("configuration_kb_file_optional_existing.xml");
    }

    private void doTestNonExistingRequired(String config) {
        Engine engine = DefaultEngine.builder().config(DIR + config).build();

        try {
            engine.startup();
        } finally {
            engine.shutdown();
        }
    }

    @Test(expected = ConfigException.class)
    public void testKnowledgeBaseFileRequiredNonExisting() {
        doTestNonExistingRequired("configuration_kb_file_required_non_existing.xml");
    }

    @Test
    public void testKnowledgeBaseFileRequiredExisting() {
        doTestExisting("configuration_kb_file_required_existing.xml");
    }

    @Test(expected = ConfigException.class)
    public void testKnowledgeBaseFileDefaultNonExisting() {
        doTestNonExistingRequired("configuration_kb_file_default_non_existing.xml");
    }

    @Test
    public void testKnowledgeBaseFileDefaultExisting() {
        doTestExisting("configuration_kb_file_default_existing.xml");
    }
}
