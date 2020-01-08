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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.ProcessorOnInitCallback;
import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.action.ActionBuilder;
import org.openksavi.sponge.action.ActionOnCallCallback;
import org.openksavi.sponge.action.ActionOnCallNoResultCallback;
import org.openksavi.sponge.action.ActionOnCallResultCallback;
import org.openksavi.sponge.action.ActionOnIsActiveCallback;
import org.openksavi.sponge.action.ActionOnProvideArgsCallback;
import org.openksavi.sponge.core.BaseProcessorBuilder;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.VoidType;

/**
 * A base action builder.
 */
@SuppressWarnings("rawtypes")
public class BaseActionBuilder extends BaseProcessorBuilder<Action> implements ActionBuilder {

    private ActionOnIsActiveCallback onIsActiveCallback;

    private ActionOnCallCallback onCallCallback;

    private ActionOnProvideArgsCallback onProvideArgsCallback;

    public BaseActionBuilder(String name) {
        super(new BaseActionMeta(), name);
    }

    @Override
    public BaseActionMeta getMeta() {
        return (BaseActionMeta) super.getMeta();
    }

    @Override
    public BaseActionBuilder withName(String name) {
        return (BaseActionBuilder) super.withName(name);
    }

    @Override
    public BaseActionBuilder withLabel(String label) {
        return (BaseActionBuilder) super.withLabel(label);
    }

    @Override
    public BaseActionBuilder withDescription(String description) {
        return (BaseActionBuilder) super.withDescription(description);
    }

    @Override
    public BaseActionBuilder withVersion(Integer version) {
        return (BaseActionBuilder) super.withVersion(version);
    }

    @Override
    public BaseActionBuilder withFeatures(Map<String, Object> features) {
        return (BaseActionBuilder) super.withFeatures(features);
    }

    @Override
    public BaseActionBuilder withFeature(String name, Object value) {
        return (BaseActionBuilder) super.withFeature(name, value);
    }

    @Override
    public BaseActionBuilder withCategory(String category) {
        return (BaseActionBuilder) super.withCategory(category);
    }

    @Override
    public ActionBuilder withArgs(List<DataType> argTypes) {
        getMeta().addArgs(argTypes);
        return this;
    }

    @Override
    public ActionBuilder withArg(DataType argType) {
        return withArgs(Arrays.asList(argType));
    }

    @Override
    public ActionBuilder withNoArgs() {
        return withArgs(Collections.emptyList());
    }

    @Override
    public ActionBuilder withResult(DataType resultType) {
        getMeta().setResult(resultType);
        return this;
    }

    @Override
    public ActionBuilder withNoResult() {
        return withResult(new VoidType());
    }

    @Override
    public ActionBuilder withCallable(boolean callable) {
        getMeta().setCallable(callable);
        if (!callable && getMeta().getResult() == null) {
            withNoResult();
        }
        return this;
    }

    @Override
    public ActionBuilder withNotCallable() {
        return withCallable(false);
    }

    @Override
    public BaseActionBuilder withOnInit(ProcessorOnInitCallback<Action> onInitCallback) {
        return (BaseActionBuilder) super.withOnInit(onInitCallback);
    }

    @Override
    public ActionBuilder withOnIsActive(ActionOnIsActiveCallback onIsActiveCallback) {
        this.onIsActiveCallback = onIsActiveCallback;

        getMeta().setActivatable(onIsActiveCallback != null);

        return this;
    }

    @Override
    public ActionBuilder withOnCallArgs(ActionOnCallResultCallback onCallCallback) {
        this.onCallCallback = onCallCallback;

        return this;
    }

    @Override
    public ActionBuilder withOnCallArgsNoResult(ActionOnCallNoResultCallback onCallCallback) {
        this.onCallCallback = onCallCallback;

        return this;
    }

    @Override
    public ActionBuilder withOnProvideArgs(ActionOnProvideArgsCallback onProvideArgsCallback) {
        this.onProvideArgsCallback = onProvideArgsCallback;

        return this;
    }

    public ActionOnIsActiveCallback getOnIsActiveCallback() {
        return onIsActiveCallback;
    }

    public void setOnIsActiveCallback(ActionOnIsActiveCallback onIsActiveCallback) {
        this.onIsActiveCallback = onIsActiveCallback;
    }

    public ActionOnCallCallback getOnCallCallback() {
        return onCallCallback;
    }

    public void setOnCallCallback(ActionOnCallCallback onCallCallback) {
        this.onCallCallback = onCallCallback;
    }

    public ActionOnProvideArgsCallback getOnProvideArgsCallback() {
        return onProvideArgsCallback;
    }

    public void setOnProvideArgsCallback(ActionOnProvideArgsCallback onProvideArgsCallback) {
        this.onProvideArgsCallback = onProvideArgsCallback;
    }

    @Override
    public Action build() {
        return new DefaultBuilderAction(this);
    }
}
