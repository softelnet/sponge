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

package org.openksavi.sponge.java;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.core.correlator.BaseCorrelator;

/**
 * Java-specific implementation of the correlator.
 */
public abstract class JCorrelator extends BaseCorrelator {

    @Override
    public final JCorrelator withName(String name) {
        return (JCorrelator) super.withName(name);
    }

    @Override
    public final JCorrelator withLabel(String label) {
        return (JCorrelator) super.withLabel(label);
    }

    @Override
    public final JCorrelator withDescription(String description) {
        return (JCorrelator) super.withDescription(description);
    }

    @Override
    public final JCorrelator withVersion(Integer version) {
        return (JCorrelator) super.withVersion(version);
    }

    @Override
    public final JCorrelator withFeatures(Map<String, Object> features) {
        return (JCorrelator) super.withFeatures(features);
    }

    @Override
    public final JCorrelator withFeature(String name, Object value) {
        return (JCorrelator) super.withFeature(name, value);
    }

    @Override
    public final JCorrelator withCategory(String category) {
        return (JCorrelator) super.withCategory(category);
    }

    @Override
    public final JCorrelator withEvents(List<String> eventNames) {
        return (JCorrelator) super.withEvents(eventNames);
    }

    public final JCorrelator withEvents(String... events) {
        return withEvents(Arrays.asList(events));
    }

    @Override
    public final JCorrelator withEvent(String eventName) {
        return (JCorrelator) super.withEvent(eventName);
    }

    @Override
    public final JCorrelator withDuration(Duration duration) {
        return (JCorrelator) super.withDuration(duration);
    }

    @Override
    public final JCorrelator withSynchronous(Boolean synchronous) {
        return (JCorrelator) super.withSynchronous(synchronous);
    }

    @Override
    public final JCorrelator withMaxInstances(int maxInstances) {
        return (JCorrelator) super.withMaxInstances(maxInstances);
    }

    @Override
    public final JCorrelator withInstanceSynchronous(boolean instanceSynchronous) {
        return (JCorrelator) super.withInstanceSynchronous(instanceSynchronous);
    }
}
