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

package org.openksavi.sponge.core.action;

import java.util.Arrays;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.ActionOnCallCallback;
import org.openksavi.sponge.action.ActionOnCallNoResultCallback;
import org.openksavi.sponge.action.ActionOnCallResultCallback;
import org.openksavi.sponge.action.IsActionActiveContext;
import org.openksavi.sponge.action.ProvideArgsContext;

/**
 * An action created by a builder.
 */
public class DefaultBuilderAction extends BaseAction {

    private BaseActionBuilder builder;

    public DefaultBuilderAction(BaseActionBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void onConfigure() {
        getMeta().update(builder.getMeta());

        Validate.isTrue(!getMeta().isCallable() || builder.getOnCallCallback() != null,
                "An onCall callback must be set for a callable action");
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
    public boolean onIsActive(IsActionActiveContext context) {
        if (builder.getOnIsActiveCallback() != null) {
            return builder.getOnIsActiveCallback().onIsActive(this, context);
        } else {
            return super.onIsActive(context);
        }
    }

    public Object onCall(Object... args) {
        ActionOnCallCallback onCall = builder.getOnCallCallback();

        if (onCall instanceof ActionOnCallResultCallback) {
            return ((ActionOnCallResultCallback) onCall).onCall(this, Arrays.asList(args));
        } else if (onCall instanceof ActionOnCallNoResultCallback) {
            ((ActionOnCallNoResultCallback) onCall).onCall(this, Arrays.asList(args));
            return null;
        }

        throw new SpongeException("An onCall callback is not configured properly");
    }

    @Override
    public void onProvideArgs(ProvideArgsContext context) {
        if (builder.getOnProvideArgsCallback() != null) {
            builder.getOnProvideArgsCallback().onProvideArgs(this, context);
        } else {
            super.onProvideArgs(context);
        }
    }
}
