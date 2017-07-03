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

import org.openksavi.sponge.event.Event;

/**
 * JavaScript-specific implementation of the trigger.
 */
public abstract class NashornTrigger extends org.openksavi.sponge.core.trigger.BaseTrigger implements NashornScriptObject {

    /** JavaScript object to overcome class inheritance limitations in JavaScript and Nashorn. Doesn't have to be thread safe. */
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

    public abstract void configure(Object self);

    @Override
    public final void init() {
        init(getSelf());
    }

    public void init(Object self) {
        //
    }

    @Override
    public final void run(Event event) {
        run(getSelf(), event);
    }

    public abstract void run(Object self, Event event);
}
