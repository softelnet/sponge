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

package org.openksavi.sponge.core.filter;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.ProcessorOnInitCallback;
import org.openksavi.sponge.core.BaseEventProcessorBuilder;
import org.openksavi.sponge.filter.Filter;
import org.openksavi.sponge.filter.FilterBuilder;
import org.openksavi.sponge.filter.FilterOnAcceptCallback;

/**
 * A base filter builder.
 */
public class BaseFilterBuilder extends BaseEventProcessorBuilder<Filter> implements FilterBuilder {

    private FilterOnAcceptCallback onAcceptCallback;

    public BaseFilterBuilder(String name) {
        super(new BaseFilterMeta(), name);
    }

    @Override
    public BaseFilterMeta getMeta() {
        return (BaseFilterMeta) super.getMeta();
    }

    @Override
    public BaseFilterBuilder withName(String name) {
        return (BaseFilterBuilder) super.withName(name);
    }

    @Override
    public BaseFilterBuilder withLabel(String label) {
        return (BaseFilterBuilder) super.withLabel(label);
    }

    @Override
    public BaseFilterBuilder withDescription(String description) {
        return (BaseFilterBuilder) super.withDescription(description);
    }

    @Override
    public BaseFilterBuilder withVersion(Integer version) {
        return (BaseFilterBuilder) super.withVersion(version);
    }

    @Override
    public BaseFilterBuilder withFeatures(Map<String, Object> features) {
        return (BaseFilterBuilder) super.withFeatures(features);
    }

    @Override
    public BaseFilterBuilder withFeature(String name, Object value) {
        return (BaseFilterBuilder) super.withFeature(name, value);
    }

    @Override
    public BaseFilterBuilder withCategory(String category) {
        return (BaseFilterBuilder) super.withCategory(category);
    }

    @Override
    public BaseFilterBuilder withOnInit(ProcessorOnInitCallback<Filter> onInitCallback) {
        return (BaseFilterBuilder) super.withOnInit(onInitCallback);
    }

    public FilterOnAcceptCallback getOnAcceptCallback() {
        return onAcceptCallback;
    }

    public void setOnAcceptCallback(FilterOnAcceptCallback onAcceptCallback) {
        this.onAcceptCallback = onAcceptCallback;
    }

    @Override
    public BaseFilterBuilder withEvents(List<String> eventNames) {
        return (BaseFilterBuilder) super.withEvents(eventNames);
    }

    @Override
    public BaseFilterBuilder withEvent(String eventName) {
        return (BaseFilterBuilder) super.withEvent(eventName);
    }

    @Override
    public BaseFilterBuilder withOnAccept(FilterOnAcceptCallback onAcceptCallback) {
        this.onAcceptCallback = onAcceptCallback;

        return this;
    }

    @Override
    public Filter build() {
        return new DefaultBuilderFilter(this);
    }
}
