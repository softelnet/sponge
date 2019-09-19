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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.schedulers.Schedulers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.java.JKnowledgeBase;

public class ReactiveXTestKnowledgeBase extends JKnowledgeBase {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveXTestKnowledgeBase.class);

    private ReactiveXPlugin rx;

    public ReactiveXTestKnowledgeBase(String name) {
        super(name);
    }

    public ReactiveXTestKnowledgeBase() {
        //
    }

    @Override
    public void onInit() {
        getSponge().setVariable("eventCounter", new AtomicInteger(0));
    }

    @Override
    public void onStartup() {
        logger.info("onStartup begin");
        rx = getSponge().getPlugin(ReactiveXPlugin.class);

        rx.getObservable().subscribe(event -> logger.info("{}", event.getName()));
        rx.getObservable().observeOn(Schedulers.computation()).subscribe(event -> {
            TimeUnit.SECONDS.sleep(2);
            logger.info("After sleep: {}", event.getName());
            getSponge().getVariable(AtomicInteger.class, "eventCounter").incrementAndGet();
        });

        logger.info("onStartup end");
    }
}
