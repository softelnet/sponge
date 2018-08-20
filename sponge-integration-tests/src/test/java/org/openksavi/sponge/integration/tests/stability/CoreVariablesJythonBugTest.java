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

package org.openksavi.sponge.integration.tests.stability;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.test.util.TestUtils;

@net.jcip.annotations.NotThreadSafe
public class CoreVariablesJythonBugTest {

    /**
     * This test detects a Jython 2.7.1 bug: http://bugs.jython.org/issue2487.
     */
    @Test
    public void testGlobalVariablesJythonBug() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/stability/global_variable_access_jython_bug.py").build();
        engine.startup();

        try {
            await().atMost(30, TimeUnit.MINUTES)
                    .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "stopped").get() || engine.isError());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testGlobalVariablesRubyNoBug() {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, "examples/core/stability/global_variable_access.rb").build();
        engine.startup();

        try {
            await().atMost(30, TimeUnit.MINUTES)
                    .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "stopped").get() || engine.isError());

            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
