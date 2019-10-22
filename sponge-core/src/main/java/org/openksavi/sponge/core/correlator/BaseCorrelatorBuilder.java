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

package org.openksavi.sponge.core.correlator;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.ProcessorOnInitCallback;
import org.openksavi.sponge.core.BaseEventSetProcessorBuilder;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.correlator.CorrelatorBuilder;
import org.openksavi.sponge.correlator.CorrelatorOnAcceptAsFirstCallback;
import org.openksavi.sponge.correlator.CorrelatorOnDurationCallback;
import org.openksavi.sponge.correlator.CorrelatorOnEventCallback;

/**
 * A base correlator builder.
 */
public class BaseCorrelatorBuilder extends BaseEventSetProcessorBuilder<Correlator> implements CorrelatorBuilder {

    private CorrelatorOnAcceptAsFirstCallback onAcceptAsFirstCallback;

    private CorrelatorOnEventCallback onEventCallback;

    private CorrelatorOnDurationCallback onDurationCallback;

    public BaseCorrelatorBuilder(String name) {
        super(new BaseCorrelatorMeta(), name);
    }

    @Override
    public BaseCorrelatorMeta getMeta() {
        return (BaseCorrelatorMeta) super.getMeta();
    }

    @Override
    public BaseCorrelatorBuilder withName(String name) {
        return (BaseCorrelatorBuilder) super.withName(name);
    }

    @Override
    public BaseCorrelatorBuilder withLabel(String label) {
        return (BaseCorrelatorBuilder) super.withLabel(label);
    }

    @Override
    public BaseCorrelatorBuilder withDescription(String description) {
        return (BaseCorrelatorBuilder) super.withDescription(description);
    }

    @Override
    public BaseCorrelatorBuilder withVersion(Integer version) {
        return (BaseCorrelatorBuilder) super.withVersion(version);
    }

    @Override
    public BaseCorrelatorBuilder withFeatures(Map<String, Object> features) {
        return (BaseCorrelatorBuilder) super.withFeatures(features);
    }

    @Override
    public BaseCorrelatorBuilder withFeature(String name, Object value) {
        return (BaseCorrelatorBuilder) super.withFeature(name, value);
    }

    @Override
    public BaseCorrelatorBuilder withCategory(String category) {
        return (BaseCorrelatorBuilder) super.withCategory(category);
    }

    @Override
    public BaseCorrelatorBuilder withEvents(List<String> eventNames) {
        return (BaseCorrelatorBuilder) super.withEvents(eventNames);
    }

    @Override
    public BaseCorrelatorBuilder withEvent(String eventName) {
        return (BaseCorrelatorBuilder) super.withEvent(eventName);
    }

    @Override
    public BaseCorrelatorBuilder withDuration(Duration duration) {
        return (BaseCorrelatorBuilder) super.withDuration(duration);
    }

    @Override
    public BaseCorrelatorBuilder withSynchronous(Boolean synchronous) {
        return (BaseCorrelatorBuilder) super.withSynchronous(synchronous);
    }

    @Override
    public BaseCorrelatorBuilder withMaxInstances(int maxInstances) {
        getMeta().setMaxInstances(maxInstances);
        return this;
    }

    @Override
    public BaseCorrelatorBuilder withInstanceSynchronous(boolean instanceSynchronous) {
        getMeta().setInstanceSynchronous(instanceSynchronous);
        return this;
    }

    @Override
    public BaseCorrelatorBuilder withOnInit(ProcessorOnInitCallback<Correlator> onInitCallback) {
        return (BaseCorrelatorBuilder) super.withOnInit(onInitCallback);
    }

    @Override
    public BaseCorrelatorBuilder withOnAcceptAsFirst(CorrelatorOnAcceptAsFirstCallback onAcceptAsFirstCallback) {
        this.onAcceptAsFirstCallback = onAcceptAsFirstCallback;
        return this;
    }

    @Override
    public BaseCorrelatorBuilder withOnEvent(CorrelatorOnEventCallback onEventCallback) {
        this.onEventCallback = onEventCallback;
        return this;
    }

    @Override
    public BaseCorrelatorBuilder withOnDuration(CorrelatorOnDurationCallback onDurationCallback) {
        this.onDurationCallback = onDurationCallback;
        return this;
    }

    public CorrelatorOnAcceptAsFirstCallback getOnAcceptAsFirstCallback() {
        return onAcceptAsFirstCallback;
    }

    public void setOnAcceptAsFirstCallback(CorrelatorOnAcceptAsFirstCallback onAcceptAsFirstCallback) {
        this.onAcceptAsFirstCallback = onAcceptAsFirstCallback;
    }

    public CorrelatorOnEventCallback getOnEventCallback() {
        return onEventCallback;
    }

    public void setOnEventCallback(CorrelatorOnEventCallback onEventCallback) {
        this.onEventCallback = onEventCallback;
    }

    public CorrelatorOnDurationCallback getOnDurationCallback() {
        return onDurationCallback;
    }

    public void setOnDurationCallback(CorrelatorOnDurationCallback onDurationCallback) {
        this.onDurationCallback = onDurationCallback;
    }

    @Override
    public Correlator build() {
        return new DefaultBuilderCorrelator(this);
    }
}
