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

import java.util.Arrays;

import org.openksavi.sponge.examples.PowerEchoAction;
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.java.JKnowledgeBase;

/**
 * Sponge Knowledge Base. Defining, calling and disabling Actions
 */
public class Actions extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("scriptActionResult", null);
        getSponge().setVariable("javaActionResult", null);
    }

    public static class EchoAction extends JAction {

        @Override
        public void onConfigure() {
            withLabel("Echo Action");
        }

        public Object[] onCall(Number value, String text) {
            return new Object[] { value, text };
        }
    }

    public static class ArrayArgumentAction extends JAction {

        public int onCall(Object[] arrayArg) {
            return arrayArg.length;
        }
    }

    @Override
    public void onLoad() {
        getSponge().enableJava(PowerEchoAction.class);
    }

    @Override
    public void onStartup() {
        getSponge().getLogger().debug("Calling script defined action");
        Object scriptActionResult = getSponge().call("EchoAction", Arrays.asList(1, "test"));
        getSponge().getLogger().debug("Action returned: {}", scriptActionResult);
        getSponge().setVariable("scriptActionResult", scriptActionResult);

        getSponge().getLogger().debug("Calling Java defined action");
        Object javaActionResult = getSponge().call("PowerEchoAction", Arrays.asList(1, "test"));
        getSponge().getLogger().debug("Action returned: {}", javaActionResult);
        getSponge().setVariable("javaActionResult", javaActionResult);

        getSponge().getLogger().debug("Disabling actions");
        getSponge().disable(EchoAction.class);
        getSponge().disableJava(PowerEchoAction.class);
    }
}
