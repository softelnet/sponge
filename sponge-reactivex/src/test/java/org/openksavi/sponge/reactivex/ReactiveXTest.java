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

package org.openksavi.sponge.reactivex;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;

import io.reactivex.Flowable;

public class ReactiveXTest {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveXTest.class);

    @Test
    public void testSimpleReactiveX() {
        Flowable.just("Hello world", 1, 2).subscribe(o -> logger.info("{}", o));
    }

    @Test
    public void testReactiveX() throws Exception {
        SpongeEngine engine = DefaultSpongeEngine.builder().config("examples/reactivex/reactivex.xml")
                .knowledgeBase(new ReactiveXTestKnowledgeBase()).build();
        engine.startup();

        await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(Number.class, "eventCounter").intValue() >= 3);
    }
}
