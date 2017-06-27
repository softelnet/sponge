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

package org.openksavi.sponge.nashorn;

import java.util.function.Function;

import org.openksavi.sponge.core.rule.CompositeEventCondition;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.nashorn.core.NashornScriptKnowledgeBaseEventCondition;
import org.openksavi.sponge.rule.EventCondition;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * JavaScript-specific implementation of the rule.
 */
@SuppressWarnings("restriction")
public abstract class NashornRule extends org.openksavi.sponge.core.rule.BaseRule implements NashornScriptObject {

    private static final Function<? super ScriptObjectMirror, ? extends EventCondition> MAPPER =
            function -> new NashornScriptKnowledgeBaseEventCondition(function);

    /** JavaScript processor used to overcome class inheritance limitations in JavaScript and Nashorn. Doesn't have to be thread safe. */
    private Object target;

    @Override
    public Object getSelf() {
        return this;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public final void configure() {
        configure(getSelf());
    }

    @Override
    public final void init() {
        init(getSelf());
    }

    @Override
    public final void run(Event event) {
        run(getSelf(), event);
    }

    public abstract void configure(Object self);

    public void init(Object self) {
        //
    }

    public abstract void run(Object self, Event event);

    public void setConditions(String eventAlias, ScriptObjectMirror... functions) {
        setJavaConditions(eventAlias, new CompositeEventCondition(MAPPER, functions));
    }

    public void addCondition(String eventAlias, ScriptObjectMirror function) {
        addJavaCondition(eventAlias, MAPPER.apply(function));
    }
}