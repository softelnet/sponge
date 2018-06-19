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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.test.util.TestUtils;

public class CoreProcessorsTest {

    @Test
    public void testProcessorMetadata() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/processors_metadata.py").build();
        engine.startup();

        try {
            assertEquals(SpongeUtils.immutableMapOf("visibility", false),
                    engine.getActionManager().getActionAdapter("UpperEchoAction").getMeta());
            assertEquals(SpongeUtils.immutableMapOf("visibility", true),
                    engine.getActionManager().getActionAdapter("PowerEchoMetadataAction").getMeta());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testEnhancedProcessorMetadata() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/processors_metadata_enhanced.py").build();
        engine.startup();

        try {
            ActionAdapter enhActionAdapter = engine.getActionManager().getActionAdapter("EdvancedMetaAction");
            String methodName = (String) enhActionAdapter.getMeta().get("isVisibleMethod");

            assertTrue((Boolean) enhActionAdapter.getKnowledgeBase().getInterpreter().invokeMethod(enhActionAdapter.getProcessor(),
                    methodName, "day"));
            assertFalse((Boolean) enhActionAdapter.getKnowledgeBase().getInterpreter().invokeMethod(enhActionAdapter.getProcessor(),
                    methodName, "night"));
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testProcessorAdditionalInterface() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/processors_interface.py").build();
        engine.startup();

        try {
            ActionAdapter enhActionAdapter = engine.getActionManager().getActionAdapter("EdvancedMetaAction");
            Action action = enhActionAdapter.getProcessor();

            assertTrue(action instanceof TestActionVisibiliy);
            assertTrue(((TestActionVisibiliy) action).isVisible("day"));
            assertFalse(((TestActionVisibiliy) action).isVisible("night"));
        } finally {
            engine.shutdown();
        }
    }
}