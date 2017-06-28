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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.examples.TestStatus;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.test.TestUtils;
import org.openksavi.sponge.test.script.util.ScriptTestUtils;

public class KnowledgeBaseTestTemplate {

    public static void testCallbacks(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithKnowledgeBase(type, "knowledge_base_callbacks");

        try {
            await().atMost(2, TimeUnit.SECONDS).until(() -> ((AtomicBoolean) engine.getOperations().getVariable("onInitCalled")).get());
            await().atMost(2, TimeUnit.SECONDS).until(() -> ((AtomicBoolean) engine.getOperations().getVariable("onStartupCalled")).get());
            await().pollDelay(4, TimeUnit.SECONDS).atMost(10, TimeUnit.SECONDS)
                    .until(() -> ((AtomicBoolean) engine.getOperations().getVariable("onBeforeReloadCalled")).get());
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> ((AtomicBoolean) engine.getOperations().getVariable("onAfterReloadCalled")).get());
            await().atMost(2, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "onLoadCalled").intValue() == 2);
            TestStatus.onShutdownCalled = false;
        } finally {
            engine.shutdown();
        }
        assertTrue(TestStatus.onShutdownCalled);
    }

    public static void testLoad(KnowledgeBaseType type) {
        Engine engine = DefaultEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, ScriptTestUtils.getScriptKnowledgeBaseFileName(type, "knowledge_base_load")).build();
        engine.getConfigurationManager().setAutoEnable(false);
        engine.startup();

        try {
            await().atMost(10, TimeUnit.SECONDS).until(() -> TestUtils.getEventCounter(engine, "Trigger1, file3") >= 20);

            assertTrue(TestUtils.getEventCounter(engine, "Trigger1, file1") > 1);
            assertTrue(TestUtils.getEventCounter(engine, "Trigger2, file1") > 1);
            assertTrue(TestUtils.getEventCounter(engine, "Trigger1, file2") > 1);
            assertTrue(TestUtils.getEventCounter(engine, "Trigger2, file2") > 1);

            assertTrue(TestUtils.getEventCounter(engine, "Trigger1, file2") > TestUtils.getEventCounter(engine, "Trigger1, file1"));
            assertTrue(TestUtils.getEventCounter(engine, "Trigger1, file3") > TestUtils.getEventCounter(engine, "Trigger1, file2"));

            assertEquals(0, TestUtils.getEventCounter(engine, "Trigger3, file3"));
        } finally {
            engine.shutdown();
        }
    }

    public static void testManager(KnowledgeBaseType type) {
        Engine engine = DefaultEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, ScriptTestUtils.getScriptKnowledgeBaseFileName(type, "knowledge_base_manager"))
                .build();
        engine.getConfigurationManager().setAutoEnable(false);
        engine.startup();

        try {
            await().atMost(5, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "verificationDone").get());

            assertTrue(engine.getOperations().getVariable(AtomicBoolean.class, "verifyTriggerEnabled").get());
            assertTrue(engine.getOperations().getVariable(AtomicBoolean.class, "verifyTriggerDisabled").get());
        } finally {
            engine.shutdown();
        }
    }

    public static void testAutoEnable(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithKnowledgeBase(type, "knowledge_base_auto_enable");

        try {
            await().atMost(5, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(Number.class, "counter").intValue() >= 5);

            assertEquals(5, engine.getOperations().getVariable(Number.class, "counter").intValue());
        } finally {
            engine.shutdown();
        }
    }

    private static String getConcurrencyTestValue(Engine engine) {
        return (String) engine.getOperations().getVariable(AtomicReference.class, "value").get();
    }

    public static void testConcurrency(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithConfig(type, "knowledge_base_concurrency");

        try {
            await().atMost(5, TimeUnit.SECONDS).until(() -> "A1".equals(getConcurrencyTestValue(engine)));
            await().atMost(10, TimeUnit.SECONDS).until(() -> "B1".equals(getConcurrencyTestValue(engine)));
            await().atMost(15, TimeUnit.SECONDS).until(() -> "A2".equals(getConcurrencyTestValue(engine)));
            await().atMost(20, TimeUnit.SECONDS).until(() -> "B2".equals(getConcurrencyTestValue(engine)));
            await().atMost(25, TimeUnit.SECONDS).until(() -> "C1".equals(getConcurrencyTestValue(engine)));
            await().atMost(30, TimeUnit.SECONDS).until(() -> "C2".equals(getConcurrencyTestValue(engine)));
        } finally {
            engine.shutdown();
        }
    }

    @SuppressWarnings("unchecked")
    public static void testLibrary(KnowledgeBaseType type) {
        Engine engine = ScriptTestUtils.startWithConfig(type, "knowledge_base_library");

        try {
            await().pollDelay(5, TimeUnit.SECONDS).atMost(60, TimeUnit.SECONDS)
                    .until(() -> ((Map<String, String>) engine.getOperations().getVariable("hostStatus")).size() >= 2);

            assertEquals("ERROR",
                    ((Map<String, String>) engine.getOperations().getVariable("hostStatus")).get("www.wikipedia.org.unknown"));
            assertEquals("200", ((Map<String, String>) engine.getOperations().getVariable("hostStatus")).get("www.wikipedia.org"));
        } finally {
            engine.shutdown();
        }
    }

    public static void testScriptOverriding(KnowledgeBaseType type) {
        Engine engine =
                DefaultEngine.builder().config(ScriptTestUtils.getScriptConfigFileName(type, "knowledge_base_script_overriding")).build();
        engine.getConfigurationManager().setAutoEnable(false);
        engine.startup();

        try {
            await().atMost(5, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "receivedEventA2").intValue() == 2);

            assertEquals(0, engine.getOperations().getVariable(Number.class, "receivedEventA1").intValue());

            assertEquals(0, engine.getOperations().getVariable(Number.class, "functionA1").intValue());
            assertEquals(2, engine.getOperations().getVariable(Number.class, "functionA2").intValue());
        } finally {
            engine.shutdown();
        }
    }
}
