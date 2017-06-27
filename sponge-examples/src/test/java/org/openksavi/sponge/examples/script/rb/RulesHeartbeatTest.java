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

package org.openksavi.sponge.examples.script.rb;

import org.junit.Test;

import org.openksavi.sponge.examples.script.template.RulesHeartbeatTestTemplate;
import org.openksavi.sponge.jruby.RubyConstants;
import org.openksavi.sponge.kb.KnowledgeBaseType;

public class RulesHeartbeatTest {

    private static final KnowledgeBaseType TYPE = RubyConstants.TYPE;

    @Test
    public void testHeartbeat() {
        RulesHeartbeatTestTemplate.testHeartbeat(TYPE);
    }

    @Test
    public void testHeartbeat2() {
        RulesHeartbeatTestTemplate.testHeartbeat2(TYPE);
    }
}