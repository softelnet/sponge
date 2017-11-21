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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.engine.Engine;

public class CamelRssNewsTest {

    private static final Logger logger = LoggerFactory.getLogger(CamelRssNewsTest.class);

    @Test
    public void testCamelRssNews() throws InterruptedException {
        CamelRssNewsExampleMain example = new CamelRssNewsExampleMain();
        try {
            example.startup();
            Engine engine = example.getEngine();

            await().atMost(60, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "alarmSounded").get()
                            && engine.getOperations().getVariable(AtomicBoolean.class, "alarmForwarded").get()
                            && engine.getOperations().getVariable(AtomicBoolean.class, "sourceRoutesStopped").get());

            if (engine.isError()) {
                Assert.fail(engine.getError().toString());
            }
        } finally {
            example.shutdown();
        }
    }

    /**
     * This test results in a nondeterministic error after running a few iterations and when //.filter((exchange) -> false) line is
     * uncommented in SpringConfiguration.java. Possibly a Jython bug. The error is: java.lang.NullPointerException at
     * org.python.core.PyType$MROMergeState.isMerged(PyType.java:2094) or java.lang.NullPointerException at
     * org.python.core.PyType$MROMergeState.pastnextContains(PyType.java:2114).
     */
    // @Test
    public void testCamelRssNewsLoop() throws InterruptedException {
        int i = 0;
        while (true) {
            logger.info("Iteration {}", i++);
            testCamelRssNews();
        }
    }
}
