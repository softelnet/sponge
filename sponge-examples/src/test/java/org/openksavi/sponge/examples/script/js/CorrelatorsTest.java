/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.examples.script.js;

import org.junit.Test;

import org.openksavi.sponge.examples.script.template.CorrelatorsTestTemplate;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.nashorn.JavaScriptConstants;

public class CorrelatorsTest {

    private static final KnowledgeBaseType TYPE = JavaScriptConstants.TYPE;

    @Test
    public void testCorrelators() throws InterruptedException {
        CorrelatorsTestTemplate.testCorrelators(TYPE);
    }

    @Test
    public void testCorrelatorsRepeated() throws InterruptedException {
        CorrelatorsTestTemplate.testCorrelatorsRepeated(TYPE);
    }

    @Test
    public void testCorrelatorsDuration() throws InterruptedException {
        CorrelatorsTestTemplate.testCorrelatorsDuration(TYPE);
    }
}
