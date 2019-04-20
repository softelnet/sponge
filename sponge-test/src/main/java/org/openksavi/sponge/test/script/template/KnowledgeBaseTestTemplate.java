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

package org.openksavi.sponge.test.script.template;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.test.util.ScriptTestUtils;
import org.openksavi.sponge.test.util.TestStatus;
import org.openksavi.sponge.test.util.TestUtils;

public class KnowledgeBaseTestTemplate {

    public static void testCallbacks(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "knowledge_base_callbacks");

        try {
            await().atMost(30, TimeUnit.SECONDS).until(() -> ((AtomicBoolean) engine.getOperations().getVariable("onInitCalled")).get());
            await().atMost(30, TimeUnit.SECONDS).until(() -> ((AtomicBoolean) engine.getOperations().getVariable("onStartupCalled")).get());
            await().pollDelay(5, TimeUnit.SECONDS).atMost(30, TimeUnit.SECONDS)
                    .until(() -> ((AtomicBoolean) engine.getOperations().getVariable("onBeforeReloadCalled")).get());
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> ((AtomicBoolean) engine.getOperations().getVariable("onAfterReloadCalled")).get());
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "onBeforeLoadCalled").intValue() == 2);
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "onLoadCalled").intValue() == 2);
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "onAfterLoadCalled").intValue() == 2);
            TestStatus.onShutdownCalled = false;
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
        assertTrue(TestStatus.onShutdownCalled);
    }

    public static void testLoad(KnowledgeBaseType type) {
        SpongeEngine engine = DefaultSpongeEngine.builder()
                .knowledgeBase(TestUtils.DEFAULT_KB, ScriptTestUtils.getScriptKnowledgeBaseFilename(type, "knowledge_base_load")).build();
        engine.getConfigurationManager().setAutoEnable(false);
        engine.startup();

        try {
            await().atMost(60, TimeUnit.SECONDS).until(() -> TestUtils.getEventCounter(engine, "Trigger1, file3") >= 20);

            assertTrue(TestUtils.getEventCounter(engine, "Trigger1, file1") > 1);
            assertTrue(TestUtils.getEventCounter(engine, "Trigger2, file1") > 1);
            assertTrue(TestUtils.getEventCounter(engine, "Trigger1, file2") > 1);
            assertTrue(TestUtils.getEventCounter(engine, "Trigger2, file2") > 1);

            assertEquals(0, TestUtils.getEventCounter(engine, "Trigger3, file3"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    public static void testManager(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.buildWithKnowledgeBase(type, "knowledge_base_manager");
        engine.getConfigurationManager().setAutoEnable(false);
        engine.startup();

        try {
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "verificationDone").get());

            assertTrue(engine.getOperations().getVariable(AtomicBoolean.class, "verifyTriggerEnabled").get());
            assertTrue(engine.getOperations().getVariable(AtomicBoolean.class, "verifyTriggerDisabled").get());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    public static void testAutoEnable(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "knowledge_base_auto_enable");

        try {
            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(Number.class, "counter").intValue() >= 5);

            assertEquals(5, engine.getOperations().getVariable(Number.class, "counter").intValue());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    private static String getConcurrencyTestValue(SpongeEngine engine) {
        return (String) engine.getOperations().getVariable(AtomicReference.class, "value").get();
    }

    public static void testConcurrency(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithConfig(type, "knowledge_base_concurrency");

        try {
            await().atMost(30, TimeUnit.SECONDS).until(() -> "A1".equals(getConcurrencyTestValue(engine)));
            await().atMost(10, TimeUnit.SECONDS).until(() -> "B1".equals(getConcurrencyTestValue(engine)));
            await().atMost(10, TimeUnit.SECONDS).until(() -> "A2".equals(getConcurrencyTestValue(engine)));
            await().atMost(10, TimeUnit.SECONDS).until(() -> "B2".equals(getConcurrencyTestValue(engine)));
            await().atMost(10, TimeUnit.SECONDS).until(() -> "C1".equals(getConcurrencyTestValue(engine)));
            await().atMost(10, TimeUnit.SECONDS).until(() -> "C2".equals(getConcurrencyTestValue(engine)));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @SuppressWarnings("unchecked")
    public static void testLibrary(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithConfig(type, "knowledge_base_library");

        try {
            await().pollDelay(5, TimeUnit.SECONDS).atMost(60, TimeUnit.SECONDS)
                    .until(() -> ((Map<String, String>) engine.getOperations().getVariable("hostStatus")).size() >= 2);

            Map<String, String> hostStatus = (Map<String, String>) engine.getOperations().getVariable("hostStatus");
            assertEquals("ERROR", hostStatus.get("www.wikipedia.org.unknown"));
            assertTrue(Integer.decode(hostStatus.get("www.wikipedia.org").trim()).intValue() > 0);
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    public static void testScriptOverriding(KnowledgeBaseType type) {
        SpongeEngine engine =
                DefaultSpongeEngine.builder().config(ScriptTestUtils.getConfigFilename(type, "knowledge_base_script_overriding")).build();
        engine.getConfigurationManager().setAutoEnable(false);
        engine.startup();

        try {
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "receivedEventA2").intValue() == 2);

            assertEquals(0, engine.getOperations().getVariable(Number.class, "receivedEventA1").intValue());

            assertEquals(0, engine.getOperations().getVariable(Number.class, "functionA1").intValue());
            assertEquals(2, engine.getOperations().getVariable(Number.class, "functionA2").intValue());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
