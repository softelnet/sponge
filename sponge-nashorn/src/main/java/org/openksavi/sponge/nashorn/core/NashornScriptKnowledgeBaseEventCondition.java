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

package org.openksavi.sponge.nashorn.core;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.Rule;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * Script knowledge base rule event condition.
 */
@SuppressWarnings({ "restriction" })
public class NashornScriptKnowledgeBaseEventCondition implements EventCondition {

    private ScriptObjectMirror function;

    public NashornScriptKnowledgeBaseEventCondition(ScriptObjectMirror function) {
        this.function = function;
    }

    /**
     * Checks rule event condition by evaluating the defined
     * knowledge base rule method.
     *
     * @param rule
     *            rule.
     * @param event
     *            event.
     * @return {@code true} if this condition is met.
     */
    @Override
    public boolean condition(Rule rule, Event event) {
        Object result = function.call(null, rule, event);

        if (!(result instanceof Boolean)) {
            throw new IllegalArgumentException("Condition method must return boolean value");
        }

        return (Boolean) result;
    }
}
