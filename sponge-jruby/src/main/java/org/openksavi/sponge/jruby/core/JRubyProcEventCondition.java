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

package org.openksavi.sponge.jruby.core;

import org.jruby.RubyProc;
import org.jruby.embed.ScriptingContainer;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.Rule;

/**
 * JRuby proc (e.g. lambda) rule event condition.
 */
public class JRubyProcEventCondition implements EventCondition {

    private RubyProc proc;

    /**
     * Creates a new JRuby proc rule event condition.
     *
     * @param proc JRuby proc.
     *
     */
    public JRubyProcEventCondition(RubyProc proc) {
        this.proc = proc;
    }

    /**
     * Checks rule event condition by evaluating the defined knowledge base rule method.
     *
     * @param rule rule.
     * @param event event.
     * @return {@code true} if this condition is met.
     */
    @Override
    public boolean condition(Rule rule, Event event) {
        ScriptingContainer scriptContainer =
                ((JRubyKnowledgeBaseInterpreter) rule.getKnowledgeBase().getInterpreter()).getScriptContainer();
        Object result = scriptContainer.callMethod(proc, "call", rule, event);

        if (!(result instanceof Boolean)) {
            throw new IllegalArgumentException("Condition method must return boolean value");
        }

        return (Boolean) result;
    }
}
