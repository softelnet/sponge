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

import org.openksavi.sponge.core.filter.BaseFilter;

/**
 * Java-specific implementation of the filter.
 */
public abstract class JFilter extends BaseFilter {

    @Override
    public final JFilter withName(String name) {
        return (JFilter) super.withName(name);
    }

    @Override
    public final JFilter withLabel(String label) {
        return (JFilter) super.withLabel(label);
    }

    @Override
    public final JFilter withDescription(String description) {
        return (JFilter) super.withDescription(description);
    }

    @Override
    public final JFilter withVersion(Integer version) {
        return (JFilter) super.withVersion(version);
    }

    @Override
    public final JFilter withFeatures(Map<String, Object> features) {
        return (JFilter) super.withFeatures(features);
    }

    @Override
    public final JFilter withFeature(String name, Object value) {
        return (JFilter) super.withFeature(name, value);
    }

    @Override
    public final JFilter withCategory(String category) {
        return (JFilter) super.withCategory(category);
    }

    @Override
    public final JFilter withEvents(List<String> eventNames) {
        return (JFilter) super.withEvents(eventNames);
    }

    public final JFilter withEvents(String... events) {
        return withEvents(Arrays.asList(events));
    }

    @Override
    public final JFilter withEvent(String eventName) {
        return (JFilter) super.withEvent(eventName);
    }
}
