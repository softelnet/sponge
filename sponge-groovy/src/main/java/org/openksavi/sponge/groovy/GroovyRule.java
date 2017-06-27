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

package org.openksavi.sponge.groovy;

import java.util.function.Function;

import org.codehaus.groovy.runtime.MethodClosure;

import org.openksavi.sponge.core.rule.CompositeEventCondition;
import org.openksavi.sponge.core.rule.ScriptKnowledgeBaseEventCondition;
import org.openksavi.sponge.groovy.core.GroovyClosureEventCondition;
import org.openksavi.sponge.rule.EventCondition;

import groovy.lang.Closure;

/**
 * Groovy-specific implementation of the rule.
 */
public abstract class GroovyRule extends org.openksavi.sponge.core.rule.BaseRule {

    private static final Function<? super Closure<Boolean>, ? extends EventCondition> MAPPER = closure -> {
        if (closure instanceof MethodClosure) {
            return new ScriptKnowledgeBaseEventCondition(((MethodClosure) closure).getMethod());
        } else {
            return new GroovyClosureEventCondition(closure);
        }
    };

    @SuppressWarnings("unchecked")
    public void setConditions(String eventAlias, Closure<Boolean>... closures) {
        setJavaConditions(eventAlias, new CompositeEventCondition(MAPPER, closures));
    }

    public void addCondition(String eventAlias, Closure<Boolean> closure) {
        addJavaCondition(eventAlias, MAPPER.apply(closure));
    }
}