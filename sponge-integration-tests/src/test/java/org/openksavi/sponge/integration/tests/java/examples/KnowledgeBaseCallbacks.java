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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.examples.util.TestStatus;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JTrigger;

/**
 * Sponge Knowledge Base. Using knowledge base callbacks.
 */
public class KnowledgeBaseCallbacks extends JKnowledgeBase {

    public static class ReloadTrigger extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("reload");
        }

        @Override
        public void onRun(Event event) {
            getLogger().debug("Received event: {}", event.getName());
            getSponge().reload();
        }
    }

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("onInitCalled", new AtomicBoolean(false));
        getSponge().setVariable("onBeforeLoadCalled", new AtomicInteger(0));
        getSponge().setVariable("onLoadCalled", new AtomicInteger(0));
        getSponge().setVariable("onAfterLoadCalled", new AtomicInteger(0));
        getSponge().setVariable("onStartupCalled", new AtomicBoolean(false));
        getSponge().setVariable("onBeforeReloadCalled", new AtomicBoolean(false));
        getSponge().setVariable("onAfterReloadCalled", new AtomicBoolean(false));

        getLogger().debug("onInit");
        getSponge().getVariable(AtomicBoolean.class, "onInitCalled").set(true);
    }

    @Override
    public void onBeforeLoad() {
        getLogger().debug("onBeforeLoad");
        getSponge().getVariable(AtomicInteger.class, "onBeforeLoadCalled").incrementAndGet();
    }

    @Override
    public void onLoad() {
        getLogger().debug("onLoad");
        getSponge().getVariable(AtomicInteger.class, "onLoadCalled").incrementAndGet();
    }

    @Override
    public void onAfterLoad() {
        getLogger().debug("onAfterLoad");
        getSponge().getVariable(AtomicInteger.class, "onAfterLoadCalled").incrementAndGet();
    }

    @Override
    public void onStartup() {
        getLogger().debug("onStartup");
        getSponge().getVariable(AtomicBoolean.class, "onStartupCalled").set(true);
        getSponge().event("reload").sendAfter(1000);
    }

    @Override
    public void onShutdown() {
        getLogger().debug("onShutdown");
        // Using Java static field because all variables will be lost after shutdown.
        TestStatus.onShutdownCalled = true;
    }

    @Override
    public void onBeforeReload() {
        getLogger().debug("onBeforeReload");
        getSponge().getVariable(AtomicBoolean.class, "onBeforeReloadCalled").set(true);
    }

    @Override
    public void onAfterReload() {
        getLogger().debug("onAfterReload");
        getSponge().getVariable(AtomicBoolean.class, "onAfterReloadCalled").set(true);
    }
}
