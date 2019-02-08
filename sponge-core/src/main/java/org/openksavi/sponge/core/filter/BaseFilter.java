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

package org.openksavi.sponge.core.filter;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.core.BaseEventProcessor;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.filter.FilterAdapter;

public abstract class BaseFilter extends BaseEventProcessor<FilterAdapter> implements Filter {

    @Override
    public final FilterAdapter createAdapter() {
        return new BaseFilterAdapter(new BaseFilterDefinition());
    }

    @Override
    public BaseFilterMeta getMeta() {
        return (BaseFilterMeta) super.getMeta();
    }

    @Override
    public BaseFilter withName(String name) {
        return (BaseFilter) super.withName(name);
    }

    @Override
    public BaseFilter withLabel(String label) {
        return (BaseFilter) super.withLabel(label);
    }

    @Override
    public BaseFilter withDescription(String description) {
        return (BaseFilter) super.withDescription(description);
    }

    @Override
    public BaseFilter withVersion(Integer version) {
        return (BaseFilter) super.withVersion(version);
    }

    @Override
    public BaseFilter withFeatures(Map<String, Object> features) {
        return (BaseFilter) super.withFeatures(features);
    }

    @Override
    public BaseFilter withFeature(String name, Object value) {
        return (BaseFilter) super.withFeature(name, value);
    }

    @Override
    public BaseFilter withCategory(String category) {
        return (BaseFilter) super.withCategory(category);
    }

    @Override
    public BaseFilter withEvents(List<String> eventNames) {
        return (BaseFilter) super.withEvents(eventNames);
    }

    @Override
    public BaseFilter withEvent(String eventName) {
        return (BaseFilter) super.withEvent(eventName);
    }
}
