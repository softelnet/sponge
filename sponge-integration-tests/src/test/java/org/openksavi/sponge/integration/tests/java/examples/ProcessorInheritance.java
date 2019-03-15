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

import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.java.JKnowledgeBase;

/**
 * Sponge Knowledge base. Processor inheritance.
 */
public class ProcessorInheritance extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("result", null);
    }

    public static abstract class AbstractEchoAction extends JAction {

        public int calculateResult() {
            return 1;
        }
    }

    public static class EchoAction extends AbstractEchoAction {

        public int onCall() {
            return calculateResult() * 2;
        }
    }

    @Override
    public void onStartup() {
        int result = getSponge().call(Number.class, "EchoAction").intValue();
        getSponge().setVariable("result", result);
        getLogger().debug("Action returned: {}", result);
    }
}
