/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.core.trigger;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.event.Event;

/**
 * A default trigger for a builder.
 */
public class DefaultBuilderTrigger extends BaseTrigger {

    private BaseTriggerBuilder builder;

    public DefaultBuilderTrigger(BaseTriggerBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void onConfigure() {
        getMeta().update(builder.getMeta());

        Validate.notNull(builder.getOnRunCallback(), "A trigger onRun callback must be set");
    }

    @Override
    public void onInit() {
        if (builder.getOnInitCallback() != null) {
            builder.getOnInitCallback().onInit(this);
        } else {
            super.onInit();
        }
    }

    @Override
    public boolean onAccept(Event event) {
        return builder.getOnAcceptCallback() != null ? builder.getOnAcceptCallback().onAccept(this, event) : super.onAccept(event);
    }

    @Override
    public void onRun(Event event) {
        builder.getOnRunCallback().onRun(this, event);
    }
}
