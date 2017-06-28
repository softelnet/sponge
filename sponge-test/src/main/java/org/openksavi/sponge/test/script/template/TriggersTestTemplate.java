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

package org.openksavi.sponge.test.script.template;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.test.script.util.ScriptTestUtils;

public class TriggersTestTemplate {

    public static void testTriggers(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithConfig(type, "triggers");

        try {
            await().pollDelay(1, TimeUnit.SECONDS).atMost(2, TimeUnit.SECONDS)
                    .until(() -> ((AtomicBoolean) engine.getOperations().getVariable("receivedEventA")).get());
            await().atMost(5, TimeUnit.SECONDS)
                    .until(() -> ((Number) engine.getOperations().getVariable("receivedEventBCount")).intValue() > 2);
            await().atMost(5, TimeUnit.SECONDS)
                    .until(() -> ((Number) engine.getOperations().getVariable("receivedEventTestJavaCount")).intValue() == 1);
        } finally {
            engine.shutdown();
        }
    }

    public static void testHelloWorld(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithConfig(type, "hello_world");

        try {
            TimeUnit.SECONDS.sleep(1);
            assertNull(engine.getError());
        } catch (InterruptedException e) {
            throw new SpongeException(e);
        } finally {
            engine.shutdown();
        }
    }
}
