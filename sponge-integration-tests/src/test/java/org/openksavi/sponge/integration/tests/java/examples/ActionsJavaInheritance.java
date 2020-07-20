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
import java.util.List;

import org.openksavi.sponge.examples.PowerEchoAction;
import org.openksavi.sponge.java.JKnowledgeBase;

/**
 * Sponge Knowledge Base. Actions - Java inheritance.
 */
public class ActionsJavaInheritance extends JKnowledgeBase {

    public static class ExtendedFromAction extends PowerEchoAction {

        @Override
        public List<?> onCall(Number value, String text) {
            return Arrays.asList(value.intValue() + 10, text.toLowerCase());
        }
    }
}
