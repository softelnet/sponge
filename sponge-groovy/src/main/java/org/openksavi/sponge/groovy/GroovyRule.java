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

package org.openksavi.sponge.groovy;

import java.util.List;
import java.util.function.Function;

import groovy.lang.Closure;

import org.codehaus.groovy.runtime.MethodClosure;

import org.openksavi.sponge.core.rule.BaseRule;
import org.openksavi.sponge.core.rule.CompositeEventCondition;
import org.openksavi.sponge.core.rule.ScriptKnowledgeBaseEventCondition;
import org.openksavi.sponge.groovy.core.GroovyClosureEventCondition;
import org.openksavi.sponge.rule.EventCondition;

/**
 * Groovy-specific implementation of a rule.
 */
public abstract class GroovyRule extends BaseRule {

    private static final Function<? super Closure<Boolean>, ? extends EventCondition> MAPPER = closure -> {
        if (closure instanceof MethodClosure) {
            return new ScriptKnowledgeBaseEventCondition(((MethodClosure) closure).getMethod());
        } else {
            return new GroovyClosureEventCondition(closure);
        }
    };

    public GroovyRule withConditions(String eventAlias, List<Closure<Boolean>> closures) {
        return (GroovyRule) super.withEventCondition(eventAlias, CompositeEventCondition.create(MAPPER, closures));
    }

    public final GroovyRule withCondition(String eventAlias, Closure<Boolean> closure) {
        return (GroovyRule) super.withEventCondition(eventAlias, MAPPER.apply(closure));
    }

    public GroovyRule withAllConditions(List<Closure<Boolean>> closures) {
        return (GroovyRule) super.withAllEventCondition(CompositeEventCondition.create(MAPPER, closures));
    }

    public final GroovyRule withAllCondition(Closure<Boolean> closure) {
        return (GroovyRule) super.withAllEventCondition(MAPPER.apply(closure));
    }
}
