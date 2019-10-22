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

package org.openksavi.sponge.core.filter;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.event.Event;

/**
 * A default filter for a filter builder.
 */
public class DefaultBuilderFilter extends BaseFilter {

    private BaseFilterBuilder builder;

    public DefaultBuilderFilter(BaseFilterBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void onConfigure() {
        getMeta().update(builder.getMeta());

        Validate.notNull(builder.getOnAcceptCallback(), "A filter onAccept callback must be set");
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
        return builder.getOnAcceptCallback().onAccept(this, event);
    }
}
