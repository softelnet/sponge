/*
 * Copyright 2016-2018 The Sponge authors.
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

import org.openksavi.sponge.core.correlator.BaseCorrelator;
import org.openksavi.sponge.kotlin.core.KotlinKnowledgeBaseEngineOperations;

/**
 * Kotlin-specific implementation of the correlator.
 */
public abstract class KCorrelator extends BaseCorrelator {

    /**
     * Method required for accessing the engine operations in Kotlin-based processors.
     *
     * @return the engine operations.
     */
    @Override
    public final KotlinKnowledgeBaseEngineOperations getSponge() {
        return (KotlinKnowledgeBaseEngineOperations) super.getSponge();
    }

    @Override
    public final KCorrelator withName(String name) {
        return (KCorrelator) super.withName(name);
    }

    @Override
    public final KCorrelator withLabel(String label) {
        return (KCorrelator) super.withLabel(label);
    }

    @Override
    public final KCorrelator withDescription(String description) {
        return (KCorrelator) super.withDescription(description);
    }

    @Override
    public final KCorrelator withVersion(Integer version) {
        return (KCorrelator) super.withVersion(version);
    }

    @Override
    public final KCorrelator withFeatures(Map<String, Object> features) {
        return (KCorrelator) super.withFeatures(features);
    }

    @Override
    public final KCorrelator withFeature(String name, Object value) {
        return (KCorrelator) super.withFeature(name, value);
    }

    @Override
    public final KCorrelator withCategory(String category) {
        return (KCorrelator) super.withCategory(category);
    }

    @Override
    public final KCorrelator withEvents(List<String> eventNames) {
        return (KCorrelator) super.withEvents(eventNames);
    }

    public final KCorrelator withEvents(String... events) {
        return withEvents(Arrays.asList(events));
    }

    @Override
    public final KCorrelator withEvent(String eventName) {
        return (KCorrelator) super.withEvent(eventName);
    }

    @Override
    public final KCorrelator withDuration(Duration duration) {
        return (KCorrelator) super.withDuration(duration);
    }

    @Override
    public final KCorrelator withSynchronous(Boolean synchronous) {
        return (KCorrelator) super.withSynchronous(synchronous);
    }

    @Override
    public final KCorrelator withMaxInstances(int maxInstances) {
        return (KCorrelator) super.withMaxInstances(maxInstances);
    }

    @Override
    public final KCorrelator withInstanceSynchronous(boolean instanceSynchronous) {
        return (KCorrelator) super.withInstanceSynchronous(instanceSynchronous);
    }
}
