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

package org.openksavi.sponge.examples.script.template;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.examples.script.ScriptTestUtils;
import org.openksavi.sponge.kb.KnowledgeBaseType;

public class RulesHeartbeatTestTemplate {

    public static void testHeartbeat(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithKnowledgeBase(type, "rules_heartbeat");

        try {
            await().atMost(10, TimeUnit.SECONDS).until(() -> ((AtomicBoolean) engine.getOperations().getVariable("soundTheAlarm")).get());
        } finally {
            engine.shutdown();
        }
    }

    public static void testHeartbeat2(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithKnowledgeBase(type, "rules_heartbeat2");

        try {
            await().atMost(10, TimeUnit.SECONDS).until(() -> ((AtomicBoolean) engine.getOperations().getVariable("soundTheAlarm")).get());
        } finally {
            engine.shutdown();
        }
    }
}
