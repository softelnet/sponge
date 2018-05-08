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

package org.openksavi.sponge.nashorn;

import org.openksavi.sponge.core.action.BaseAction;

/**
 * JavaScript-specific implementation of the action.
 */
public abstract class NashornAction extends BaseAction implements NashornScriptObject {

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
    public final void onConfigure() {
        onConfigure(getSelf());
    }

    public void onConfigure(Object self) {
        //
    }

    @Override
    public final void onInit() {
        onInit(getSelf());
    }

    public void onInit(Object self) {
        //
    }

    @Override
    public final Object onCall(Object[] args) {
        return onCall(getSelf(), args);
    }

    public abstract Object onCall(Object self, Object[] args);
}
