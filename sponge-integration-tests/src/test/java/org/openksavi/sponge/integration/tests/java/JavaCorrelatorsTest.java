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

package org.openksavi.sponge.integration.tests.java;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.test.script.CorrelatorsTest;
import org.openksavi.sponge.test.script.template.CorrelatorsTestTemplate;

public class JavaCorrelatorsTest extends JavaTest implements CorrelatorsTest {

    @Override
    @Test
    public void testCorrelators() {
        CorrelatorsTestTemplate.testCorrelators(getType());
    }

    @Override
    @Test
    public void testCorrelatorsRepeated() {
        CorrelatorsTestTemplate.testCorrelatorsRepeated(getType());
    }

    @Override
    @Test
    public void testCorrelatorsDuration() {
        CorrelatorsTestTemplate.testCorrelatorsDuration(getType());
    }
}
