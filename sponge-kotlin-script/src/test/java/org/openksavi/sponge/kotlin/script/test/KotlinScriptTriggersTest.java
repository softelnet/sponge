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

package org.openksavi.sponge.kotlin.script.test;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.test.script.template.TriggersTestTemplate;

public class KotlinScriptTriggersTest extends KotlinScriptTest /* implements TriggersTest */ {

    // @Override
    // @Test
    // public void testTriggers() {
    // TriggersTestTemplate.testTriggers(getType());
    // }
    //
    // @Override
    // @Test
    // public void testTriggersEventPattern() {
    // TriggersTestTemplate.testTriggersEventPattern(getType());
    // }
    //
    // @Override
    // @Test
    // public void testTriggersEventPatternIncorrect() {
    // TriggersTestTemplate.testTriggersEventPatternIncorrect(getType());
    // }
    // @Override
    // @Test
    // public void testTriggersBuilder() {
    // TriggersTestTemplate.testTriggersBuilder(getType());
    // }
    //
    // @Override
    @Test
    public void testHelloWorld() {
        TriggersTestTemplate.testHelloWorld(getType());
    }
}
