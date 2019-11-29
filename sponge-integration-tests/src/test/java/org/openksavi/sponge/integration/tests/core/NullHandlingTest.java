/*
 * Copyright 2016-2019 The Sponge authors.
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.action.ProvideArgsParameters;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.type.provided.ProvidedValue;

public class NullHandlingTest {

    @Test
    public void testNullHandling() {
        SpongeEngine engine = DefaultSpongeEngine.builder().knowledgeBase("kb", "examples/core/null_handling.py").build();
        engine.startup();

        try {
            ActionMeta actionMeta = engine.getActionMeta("NullHandling");
            ProvidedValue<?> provided = engine.getOperations()
                    .provideActionArgs(actionMeta.getName(), new ProvideArgsParameters().withProvide(Arrays.asList("arg1"))).get("arg1");

            assertNull(provided.getValue());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
