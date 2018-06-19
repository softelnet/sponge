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

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.Type;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.test.util.TestUtils;

public class CoreActionsTest {

    @Test
    public void testActionsMetadata() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata.py").build();
        engine.startup();

        try {
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("scriptActionResult") != null);
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("javaActionResult") != null);

            String scriptResult = engine.getOperations().getVariable(String.class, "scriptActionResult");
            assertEquals("TEST", scriptResult);

            Object[] javaResult = (Object[]) engine.getOperations().getVariable("javaActionResult");
            assertEquals(2, javaResult.length);
            // Note, that different scripting engines may map numbers to different types.
            assertEquals(2, ((Number) javaResult[0]).intValue());
            assertEquals("TEST", javaResult[1]);

            ActionAdapter upperActionAdapter = engine.getActionManager().getActionAdapter("UpperEchoAction");
            assertEquals("Echo Action", upperActionAdapter.getDisplayName());
            assertEquals("Returns the upper case string", upperActionAdapter.getDescription());

            ArgMeta[] argMeta = upperActionAdapter.getArgsMeta();
            assertEquals(1, argMeta.length);
            assertEquals("arg1", argMeta[0].getName());
            assertEquals(Type.STRING, argMeta[0].getType());
            assertEquals(true, argMeta[0].isRequired());
            assertEquals("Argument 1", argMeta[0].getDisplayName());
            assertEquals("Argument 1 description", argMeta[0].getDescription());

            assertEquals(Type.STRING, upperActionAdapter.getResultMeta().getType());
            assertEquals("Upper case string", upperActionAdapter.getResultMeta().getDisplayName());
            assertEquals("Result description", upperActionAdapter.getResultMeta().getDescription());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testActionsCallError() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_call_error.py").build();
        engine.startup();

        try {
            engine.getOperations().call("ErrorAction");

            assertFalse(engine.isError());
        } catch (SpongeException e) {
            // Jython-specific error message copying.
            assertTrue(e.getMessage().contains("global name 'Nooone' is not defined"));
        } finally {
            engine.shutdown();
        }
    }
}
