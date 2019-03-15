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

import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.type.StringType;

/**
 * Sponge Knowledge base. Hello World action.
 */
public class ActionsHelloWorld extends JKnowledgeBase {

    @Override
    public void onStartup() {
        getSponge().getLogger().info("{}", getSponge().call("HelloWorldAction", Arrays.asList("Sponge user")));
    }

    public static class HelloWorldAction extends JAction {

        @Override
        public void onConfigure() {
            withLabel("Hello world").withDescription("Returns a greeting text.");
            withArg(new StringType("name").withLabel("Your name").withDescription("Type your name."));
            withResult(new StringType().withLabel("Greeting").withDescription("The greeting text."));
        }

        public String onCall(String name) {
            return String.format("Hello World! Hello %s!", name);
        }
    }
}
