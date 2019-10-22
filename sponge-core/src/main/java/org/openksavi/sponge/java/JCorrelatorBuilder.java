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

package org.openksavi.sponge.java;

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
 * Java-specific implementation of a correlator builder.
 */
public class JCorrelatorBuilder extends BaseCorrelatorBuilder {

    public JCorrelatorBuilder(String name) {
        super(name);
    }

    @Override
    public JCorrelatorBuilder withName(String name) {
        return (JCorrelatorBuilder) super.withName(name);
    }

    @Override
    public JCorrelatorBuilder withLabel(String label) {
        return (JCorrelatorBuilder) super.withLabel(label);
    }

    @Override
    public JCorrelatorBuilder withDescription(String description) {
        return (JCorrelatorBuilder) super.withDescription(description);
    }

    @Override
    public JCorrelatorBuilder withVersion(Integer version) {
        return (JCorrelatorBuilder) super.withVersion(version);
    }

    @Override
    public JCorrelatorBuilder withFeatures(Map<String, Object> features) {
        return (JCorrelatorBuilder) super.withFeatures(features);
    }

    @Override
    public JCorrelatorBuilder withFeature(String name, Object value) {
        return (JCorrelatorBuilder) super.withFeature(name, value);
    }

    @Override
    public JCorrelatorBuilder withCategory(String category) {
        return (JCorrelatorBuilder) super.withCategory(category);
    }

    @Override
    public JCorrelatorBuilder withEvents(List<String> eventNames) {
        return (JCorrelatorBuilder) super.withEvents(eventNames);
    }

    public JCorrelatorBuilder withEvents(String... eventNames) {
        return withEvents(Arrays.asList(eventNames));
    }

    @Override
    public JCorrelatorBuilder withEvent(String eventName) {
        return (JCorrelatorBuilder) super.withEvent(eventName);
    }

    @Override
    public JCorrelatorBuilder withDuration(Duration duration) {
        return (JCorrelatorBuilder) super.withDuration(duration);
    }

    @Override
    public JCorrelatorBuilder withSynchronous(Boolean synchronous) {
        return (JCorrelatorBuilder) super.withSynchronous(synchronous);
    }

    @Override
    public JCorrelatorBuilder withMaxInstances(int maxInstances) {
        return (JCorrelatorBuilder) super.withMaxInstances(maxInstances);
    }

    @Override
    public JCorrelatorBuilder withInstanceSynchronous(boolean instanceSynchronous) {
        return (JCorrelatorBuilder) super.withInstanceSynchronous(instanceSynchronous);
    }

    @Override
    public JCorrelatorBuilder withOnInit(ProcessorOnInitCallback<Correlator> onInitCallback) {
        return (JCorrelatorBuilder) super.withOnInit(onInitCallback);
    }

    @Override
    public JCorrelatorBuilder withOnAcceptAsFirst(CorrelatorOnAcceptAsFirstCallback onAcceptAsFirstCallback) {
        return (JCorrelatorBuilder) super.withOnAcceptAsFirst(onAcceptAsFirstCallback);
    }

    @Override
    public JCorrelatorBuilder withOnEvent(CorrelatorOnEventCallback onEventCallback) {
        return (JCorrelatorBuilder) super.withOnEvent(onEventCallback);
    }

    @Override
    public JCorrelatorBuilder withOnDuration(CorrelatorOnDurationCallback onDurationCallback) {
        return (JCorrelatorBuilder) super.withOnDuration(onDurationCallback);
    }
}
