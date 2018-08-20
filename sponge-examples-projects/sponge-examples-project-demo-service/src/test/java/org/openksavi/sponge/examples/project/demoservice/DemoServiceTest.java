/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.examples.project.demoservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(DemoServiceTest.class);

    // @Test
    // public void testCamelRssNews() throws InterruptedException {
    // CamelRssNewsExampleMain example = new CamelRssNewsExampleMain();
    // try {
    // example.startup();
    // SpongeEngine engine = example.getEngine();
    //
    // await().atMost(60, TimeUnit.SECONDS)
    // .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "alarmSounded").get()
    // && engine.getOperations().getVariable(AtomicBoolean.class, "alarmForwarded").get()
    // && engine.getOperations().getVariable(AtomicBoolean.class, "sourceRoutesStopped").get());
    //
    // if (engine.isError()) {
    // Assert.fail(engine.getError().toString());
    // }
    // } finally {
    // example.shutdown();
    // }
    // }
}
