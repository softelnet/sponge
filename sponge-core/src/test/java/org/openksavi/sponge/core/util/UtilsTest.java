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

import org.junit.Assert;
import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.engine.Engine;

public class UtilsTest {

    @Test
    public void testCalculateInitialDynamicThreadPoolSize() {
        Engine engine = DefaultEngine.builder().build();
        Assert.assertEquals(3, Utils.calculateInitialDynamicThreadPoolSize(engine, 10));
        Assert.assertEquals(1, Utils.calculateInitialDynamicThreadPoolSize(engine, 3));
        Assert.assertEquals(2, Utils.calculateInitialDynamicThreadPoolSize(engine, 5));
        Assert.assertEquals(1, Utils.calculateInitialDynamicThreadPoolSize(engine, 2));
        Assert.assertEquals(1, Utils.calculateInitialDynamicThreadPoolSize(engine, 1));
        Assert.assertEquals(5, Utils.calculateInitialDynamicThreadPoolSize(engine, 15));
        Assert.assertEquals(6, Utils.calculateInitialDynamicThreadPoolSize(engine, 20));
        Assert.assertEquals(2, Utils.calculateInitialDynamicThreadPoolSize(engine, 8));
    }
}
