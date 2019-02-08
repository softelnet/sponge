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

package org.openksavi.sponge.core.correlator;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.openksavi.sponge.core.BaseEventSetProcessor;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.correlator.CorrelatorAdapter;
import org.openksavi.sponge.event.Event;

public abstract class BaseCorrelator extends BaseEventSetProcessor<CorrelatorAdapter> implements Correlator {

    @Override
    public BaseCorrelatorMeta getMeta() {
        return (BaseCorrelatorMeta) super.getMeta();
    }

    @Override
    public final void finish() {
        getAdapter().finish();
    }

    @Override
    public boolean onAcceptAsFirst(Event event) {
        return true;
    }

    @Override
    public void onDuration() {
        //
    }

    @Override
    public final CorrelatorAdapter createAdapter() {
        return new BaseCorrelatorAdapter(new BaseCorrelatorDefinition());
    }

    /**
     * Returns the lock that can be used to synchronize threads when the instance synchronous flag is set to {@code false}.
     *
     * @return the lock.
     */
    public final Lock getLock() {
        return ((BaseCorrelatorAdapter) getAdapter()).getLock();
    }

    @Override
    public BaseCorrelator withName(String name) {
        return (BaseCorrelator) super.withName(name);
    }

    @Override
    public BaseCorrelator withLabel(String label) {
        return (BaseCorrelator) super.withLabel(label);
    }

    @Override
    public BaseCorrelator withDescription(String description) {
        return (BaseCorrelator) super.withDescription(description);
    }

    @Override
    public BaseCorrelator withVersion(Integer version) {
        return (BaseCorrelator) super.withVersion(version);
    }

    @Override
    public BaseCorrelator withFeatures(Map<String, Object> features) {
        return (BaseCorrelator) super.withFeatures(features);
    }

    @Override
    public BaseCorrelator withFeature(String name, Object value) {
        return (BaseCorrelator) super.withFeature(name, value);
    }

    @Override
    public BaseCorrelator withCategory(String category) {
        return (BaseCorrelator) super.withCategory(category);
    }

    @Override
    public BaseCorrelator withEvents(List<String> eventNames) {
        return (BaseCorrelator) super.withEvents(eventNames);
    }

    @Override
    public BaseCorrelator withEvent(String eventName) {
        return (BaseCorrelator) super.withEvent(eventName);
    }

    @Override
    public BaseCorrelator withDuration(Duration duration) {
        return (BaseCorrelator) super.withDuration(duration);
    }

    @Override
    public BaseCorrelator withSynchronous(Boolean synchronous) {
        return (BaseCorrelator) super.withSynchronous(synchronous);
    }

    public BaseCorrelator withMaxInstances(int maxInstances) {
        getMeta().setMaxInstances(maxInstances);
        return this;
    }

    public BaseCorrelator withInstanceSynchronous(boolean instanceSynchronous) {
        getMeta().setInstanceSynchronous(instanceSynchronous);
        return this;
    }
}
