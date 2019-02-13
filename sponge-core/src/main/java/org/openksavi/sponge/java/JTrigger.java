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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.core.trigger.BaseTrigger;

/**
 * Java-specific implementation of the trigger.
 */
public abstract class JTrigger extends BaseTrigger {

    @Override
    public final JTrigger withName(String name) {
        return (JTrigger) super.withName(name);
    }

    @Override
    public final JTrigger withLabel(String label) {
        return (JTrigger) super.withLabel(label);
    }

    @Override
    public final JTrigger withDescription(String description) {
        return (JTrigger) super.withDescription(description);
    }

    @Override
    public final JTrigger withVersion(Integer version) {
        return (JTrigger) super.withVersion(version);
    }

    @Override
    public final JTrigger withFeatures(Map<String, Object> features) {
        return (JTrigger) super.withFeatures(features);
    }

    @Override
    public final JTrigger withFeature(String name, Object value) {
        return (JTrigger) super.withFeature(name, value);
    }

    @Override
    public final JTrigger withCategory(String category) {
        return (JTrigger) super.withCategory(category);
    }

    @Override
    public final JTrigger withEvents(List<String> eventNames) {
        return (JTrigger) super.withEvents(eventNames);
    }

    public final JTrigger withEvents(String... events) {
        return withEvents(Arrays.asList(events));
    }

    @Override
    public final JTrigger withEvent(String eventName) {
        return (JTrigger) super.withEvent(eventName);
    }
}
