/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.integration.tests.java.examples;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JTrigger;

/**
 * Sponge Knowledge Base. Concurrency.
 */
@SuppressWarnings("unchecked")
public class KnowledgeBaseConcurrency extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("value", new AtomicReference<>(""));
    }

    public static class A extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("a");
        }

        @Override
        public void onRun(Event event) {
            try {
                TimeUnit.SECONDS.sleep(1);
                getSponge().getVariable(AtomicReference.class, "value").set("A1");
                TimeUnit.SECONDS.sleep(3);
                getSponge().getVariable(AtomicReference.class, "value").set("A2");
            } catch (InterruptedException e) {
                throw SpongeUtils.wrapException(e);
            }
        }
    }

    public static class B extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("b");
        }

        @Override
        public void onRun(Event event) {
            try {
                TimeUnit.SECONDS.sleep(2);
                getSponge().getVariable(AtomicReference.class, "value").set("B1");
                TimeUnit.SECONDS.sleep(4);
                getSponge().getVariable(AtomicReference.class, "value").set("B2");
            } catch (InterruptedException e) {
                throw SpongeUtils.wrapException(e);
            }
        }
    }

    public static class C extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("c");
        }

        @Override
        public void onRun(Event event) {
            try {
                TimeUnit.SECONDS.sleep(8);
                getSponge().getVariable(AtomicReference.class, "value").set("C1");
                TimeUnit.SECONDS.sleep(1);
                getSponge().getVariable(AtomicReference.class, "value").set("C2");
            } catch (InterruptedException e) {
                throw SpongeUtils.wrapException(e);
            }
        }
    }

    @Override
    public void onStartup() {
        getSponge().event("a").send();
        getSponge().event("b").send();
        getSponge().event("c").send();
    }
}
