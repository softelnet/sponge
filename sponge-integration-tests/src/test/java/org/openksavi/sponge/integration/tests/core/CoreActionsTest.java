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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Test;

import org.openksavi.sponge.DataType;
import org.openksavi.sponge.action.ActionArgMetadata;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.test.util.TestUtils;

public class CoreActionsTest {

    @SuppressWarnings("rawtypes")
    @Test
    public void testActionsMetadata() {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/actions_metadata.py").build();
        engine.startup();

        try {
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("scriptActionResult") != null);
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("javaActionResult") != null);

            Object scriptResultObject = engine.getOperations().getVariable("scriptActionResult");
            List scriptResult = scriptResultObject instanceof List ? (List) scriptResultObject
                    : Arrays.stream((Object[]) scriptResultObject).collect(Collectors.toList());
            assertEquals(2, scriptResult.size());
            // Note, that different scripting engines may map numbers to different types.
            assertEquals("1", scriptResult.get(0));
            assertEquals("TEST", scriptResult.get(1));

            Object[] javaResult = (Object[]) engine.getOperations().getVariable("javaActionResult");
            assertEquals(2, javaResult.length);
            // Note, that different scripting engines may map numbers to different types.
            assertEquals(2, ((Number) javaResult[0]).intValue());
            assertEquals("TEST", javaResult[1]);

            ActionArgMetadata[] argMetadata =
                    engine.getActionManager().getRegisteredActionAdapterMap().get("UpperEchoAction").getArgsMetadata();
            assertEquals(2, argMetadata.length);
            assertEquals("arg1", argMetadata[0].getName());
            assertEquals(DataType.NUMBER, argMetadata[0].getType());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
