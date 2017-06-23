/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.examples.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.examples.script.template.RulesTestTemplate;
import org.openksavi.sponge.jython.PythonConstants;

public class SyncAsyncEventSetProcessorsTest {

    @Test
    public void testAsyncEventSetProcessors() throws InterruptedException {
        Engine engine = DefaultEngine.builder().config("examples/core/sync_async_event_set_processors.xml").build();
        engine.startup();

        assertTrue(engine.getConfigurationManager().getEventSetProcessorDefaultSynchronous());

        RulesTestTemplate.testRulesSyncAsync(PythonConstants.TYPE, engine);
    }

    @Test
    public void testSyncEventSetProcessorsConfig() throws InterruptedException {
        Engine engine = DefaultEngine.builder().knowledgeBase("kb", "examples/script/py/rules_sync_async.py").build();
        engine.startup();

        assertFalse(engine.getConfigurationManager().getEventSetProcessorDefaultSynchronous());

        RulesTestTemplate.testRulesSyncAsync(PythonConstants.TYPE, engine);
    }
}
