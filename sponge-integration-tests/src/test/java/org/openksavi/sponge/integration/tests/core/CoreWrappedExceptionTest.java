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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;

public class CoreWrappedExceptionTest {

    @Test
    public void testWrappedException() {
        SpongeEngine engine = DefaultSpongeEngine.builder().knowledgeBase("kb", "examples/core/wrapped_exception.py").build();
        engine.startup();

        try {
            engine.getOperations().call("WrappedExceptionAction");

            fail("Exception expected");
        } catch (Exception e) {
            assertTrue(e.getMessage()
                    .startsWith("String index out of range: 10 in examples/core/wrapped_exception.py at line number 11 at kb.WrappedExceptionAction.onCall"));
        } finally {
            engine.shutdown();
        }
    }
}
