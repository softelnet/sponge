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

package org.openksavi.sponge.core.util;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;

public class SpongeUtilsTest {

    @Test
    public void testCalculateInitialDynamicThreadPoolSize() {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        engine.getDefaultParameters().setInitialDynamicThreadPoolSizeRatio(0.3);

        assertEquals(3, SpongeUtils.calculateInitialDynamicThreadPoolSize(engine, 10));
        assertEquals(1, SpongeUtils.calculateInitialDynamicThreadPoolSize(engine, 3));
        assertEquals(2, SpongeUtils.calculateInitialDynamicThreadPoolSize(engine, 5));
        assertEquals(1, SpongeUtils.calculateInitialDynamicThreadPoolSize(engine, 2));
        assertEquals(1, SpongeUtils.calculateInitialDynamicThreadPoolSize(engine, 1));
        assertEquals(5, SpongeUtils.calculateInitialDynamicThreadPoolSize(engine, 15));
        assertEquals(6, SpongeUtils.calculateInitialDynamicThreadPoolSize(engine, 20));
        assertEquals(2, SpongeUtils.calculateInitialDynamicThreadPoolSize(engine, 8));
    }

    @Test
    public void testExecuteConcurrentlyOnce() {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();
        AtomicBoolean value = new AtomicBoolean(false);

        SpongeUtils.executeConcurrentlyOnce(engine, () -> {
            value.set(true);
        }, "test");

        await().atMost(10, TimeUnit.SECONDS).until(() -> value.get());
    }
}
