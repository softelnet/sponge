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

package org.openksavi.sponge.kotlin;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.ProcessorOnInitCallback;
import org.openksavi.sponge.core.correlator.BaseCorrelatorBuilder;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.correlator.CorrelatorOnAcceptAsFirstCallback;
import org.openksavi.sponge.correlator.CorrelatorOnDurationCallback;
import org.openksavi.sponge.correlator.CorrelatorOnEventCallback;

/**
 * Kotlin-specific implementation of a correlator builder.
 */
public class KCorrelatorBuilder extends BaseCorrelatorBuilder {

    public KCorrelatorBuilder(String name) {
        super(name);
    }

    @Override
    public KCorrelatorBuilder withName(String name) {
        return (KCorrelatorBuilder) super.withName(name);
    }

    @Override
    public KCorrelatorBuilder withLabel(String label) {
        return (KCorrelatorBuilder) super.withLabel(label);
    }

    @Override
    public KCorrelatorBuilder withDescription(String description) {
        return (KCorrelatorBuilder) super.withDescription(description);
    }

    @Override
    public KCorrelatorBuilder withVersion(Integer version) {
        return (KCorrelatorBuilder) super.withVersion(version);
    }

    @Override
    public KCorrelatorBuilder withFeatures(Map<String, Object> features) {
        return (KCorrelatorBuilder) super.withFeatures(features);
    }

    @Override
    public KCorrelatorBuilder withFeature(String name, Object value) {
        return (KCorrelatorBuilder) super.withFeature(name, value);
    }

    @Override
    public KCorrelatorBuilder withCategory(String category) {
        return (KCorrelatorBuilder) super.withCategory(category);
    }

    @Override
    public KCorrelatorBuilder withEvents(List<String> eventNames) {
        return (KCorrelatorBuilder) super.withEvents(eventNames);
    }

    public KCorrelatorBuilder withEvents(String... eventNames) {
        return withEvents(Arrays.asList(eventNames));
    }

    @Override
    public KCorrelatorBuilder withEvent(String eventName) {
        return (KCorrelatorBuilder) super.withEvent(eventName);
    }

    @Override
    public KCorrelatorBuilder withDuration(Duration duration) {
        return (KCorrelatorBuilder) super.withDuration(duration);
    }

    @Override
    public KCorrelatorBuilder withSynchronous(Boolean synchronous) {
        return (KCorrelatorBuilder) super.withSynchronous(synchronous);
    }

    @Override
    public KCorrelatorBuilder withMaxInstances(int maxInstances) {
        return (KCorrelatorBuilder) super.withMaxInstances(maxInstances);
    }

    @Override
    public KCorrelatorBuilder withInstanceSynchronous(boolean instanceSynchronous) {
        return (KCorrelatorBuilder) super.withInstanceSynchronous(instanceSynchronous);
    }

    @Override
    public KCorrelatorBuilder withOnInit(ProcessorOnInitCallback<Correlator> onInitCallback) {
        return (KCorrelatorBuilder) super.withOnInit(onInitCallback);
    }

    @Override
    public KCorrelatorBuilder withOnAcceptAsFirst(CorrelatorOnAcceptAsFirstCallback onAcceptAsFirstCallback) {
        return (KCorrelatorBuilder) super.withOnAcceptAsFirst(onAcceptAsFirstCallback);
    }

    @Override
    public KCorrelatorBuilder withOnEvent(CorrelatorOnEventCallback onEventCallback) {
        return (KCorrelatorBuilder) super.withOnEvent(onEventCallback);
    }

    @Override
    public KCorrelatorBuilder withOnDuration(CorrelatorOnDurationCallback onDurationCallback) {
        return (KCorrelatorBuilder) super.withOnDuration(onDurationCallback);
    }
}
