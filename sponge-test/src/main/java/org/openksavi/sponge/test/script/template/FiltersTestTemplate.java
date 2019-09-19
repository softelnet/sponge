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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.concurrent.TimeUnit;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.test.util.ScriptTestUtils;
import org.openksavi.sponge.test.util.TestUtils;

public class FiltersTestTemplate {

    public static void testJavaFilter(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "filters_java");

        try {
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> TestUtils.getEventCounter(engine, "e2") >= 5 && TestUtils.getEventCounter(engine, "e3") >= 5);
            TimeUnit.SECONDS.sleep(1);

            assertEquals(0, TestUtils.getEventCounter(engine, "e1"));
            assertFalse(engine.isError());
        } catch (InterruptedException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            engine.shutdown();
        }
    }

    public static void testFilter(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "filters");

        try {
            await().atMost(30, TimeUnit.SECONDS).until(() -> TestUtils.getEventCounter(engine, "blue") >= 1);

            TimeUnit.SECONDS.sleep(1);

            assertEquals(0, TestUtils.getEventCounter(engine, "red"));
            assertFalse(engine.isError());
        } catch (InterruptedException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            engine.shutdown();
        }
    }

    public static void testDeduplication(KnowledgeBaseType type) {
        SpongeEngine engine = ScriptTestUtils.startWithKnowledgeBase(type, "filters_deduplication");

        try {
            await().atMost(30, TimeUnit.SECONDS)
                    .until(() -> TestUtils.getEventCounter(engine, "e1-red") >= 1 && TestUtils.getEventCounter(engine, "e1-blue") >= 1
                            && TestUtils.getEventCounter(engine, "e2-red") >= 2 && TestUtils.getEventCounter(engine, "e2-blue") >= 2);

            assertEquals(1, TestUtils.getEventCounter(engine, "e1-red"));
            assertEquals(1, TestUtils.getEventCounter(engine, "e1-blue"));
            assertEquals(2, TestUtils.getEventCounter(engine, "e2-red"));
            assertEquals(2, TestUtils.getEventCounter(engine, "e2-blue"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
