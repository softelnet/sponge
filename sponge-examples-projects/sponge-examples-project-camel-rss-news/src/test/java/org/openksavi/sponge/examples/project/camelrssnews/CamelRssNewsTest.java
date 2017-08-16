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

package org.openksavi.sponge.examples.project.camelrssnews;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

import org.openksavi.sponge.engine.Engine;

public class CamelRssNewsTest {

    @Test
    public void testCamelRssNews() throws InterruptedException {
        CamelRssNewsExampleMain example = new CamelRssNewsExampleMain();
        try {
            example.startup();
            Engine engine = example.getEngine();

            await().atMost(20, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "alarmSounded").get());

            TimeUnit.SECONDS.sleep(2);

            if (engine.isError()) {
                Assert.fail(engine.getError().toString());
            }
        } finally {
            example.shutdown();
        }
    }
}
