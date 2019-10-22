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

package org.openksavi.sponge.nashorn.test;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.test.script.RulesTest;
import org.openksavi.sponge.test.script.template.RulesTestTemplate;

public class NashornRulesTest extends NashornTest implements RulesTest {

    @Override
    @Test
    public void testRules() {
        RulesTestTemplate.testRules(getType());
    }

    @Override
    @Test
    public void testRulesEvents() {
        RulesTestTemplate.testRulesEvents(getType());
    }

    @Override
    @Test
    public void testRulesNoneModeEvents() {
        RulesTestTemplate.testRulesNoneModeEvents(getType());
    }

    @Override
    @Test
    public void testRulesNoneModeEventsConditions() {
        RulesTestTemplate.testRulesNoneModeEventsConditions(getType());
    }

    @Override
    @Test
    public void testRulesSyncAsync() {
        RulesTestTemplate.testRulesSyncAsync(getType());
    }

    @Override
    @Test
    public void testHeartbeat() {
        RulesTestTemplate.testHeartbeat(getType());
    }

    @Override
    @Test
    public void testHeartbeat2() {
        RulesTestTemplate.testHeartbeat2(getType());
    }

    @Override
    @Test
    public void testRulesInstances() {
        RulesTestTemplate.testRulesInstances(getType());
    }

    @Override
    @Test
    public void testRulesBuilder() {
        RulesTestTemplate.testRulesBuilder(getType());
    }
}
