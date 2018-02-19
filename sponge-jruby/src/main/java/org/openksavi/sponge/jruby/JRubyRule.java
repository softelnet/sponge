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

package org.openksavi.sponge.jruby;

import java.util.function.Function;

import org.jruby.RubyMethod;
import org.jruby.RubyObject;
import org.jruby.RubyProc;

import org.openksavi.sponge.core.rule.BaseRule;
import org.openksavi.sponge.core.rule.CompositeEventCondition;
import org.openksavi.sponge.core.rule.ScriptKnowledgeBaseEventCondition;
import org.openksavi.sponge.jruby.core.JRubyProcEventCondition;
import org.openksavi.sponge.rule.EventCondition;

/**
 * JRuby-specific implementation of the rule.
 */
public abstract class JRubyRule extends BaseRule {

    private static final Function<? super RubyObject, ? extends EventCondition> MAPPER = rubyObject -> {
        if (rubyObject instanceof RubyMethod) {
            return new ScriptKnowledgeBaseEventCondition(((RubyMethod) rubyObject).getMethodName());
        } else if (rubyObject instanceof RubyProc) {
            return new JRubyProcEventCondition((RubyProc) rubyObject);
        } else {
            throw new IllegalArgumentException("Incorrect condition type: " + rubyObject.getClass());
        }
    };

    public final void setEvents(Object eventNames) {
        super.setEvents(RubyUtils.toJavaArray(eventNames));
    }

    public void addConditions(String eventAlias, RubyObject... rubyObjects) {
        addEventConditions(eventAlias, CompositeEventCondition.create(MAPPER, rubyObjects));
    }

    public void addAllConditions(RubyObject... rubyObjects) {
        addAllEventConditions(CompositeEventCondition.create(MAPPER, rubyObjects));
    }

    public void addCondition(String eventAlias, RubyObject rubyObject) {
        addEventCondition(eventAlias, MAPPER.apply(rubyObject));
    }
}
