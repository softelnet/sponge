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

package org.openksavi.sponge.filter;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.EventProcessorBuilder;
import org.openksavi.sponge.ProcessorOnInitCallback;

/**
 * A filter builder.
 */
public interface FilterBuilder extends EventProcessorBuilder<Filter> {

    @Override
    FilterBuilder withName(String name);

    @Override
    FilterBuilder withLabel(String label);

    @Override
    FilterBuilder withDescription(String description);

    @Override
    FilterBuilder withVersion(Integer version);

    @Override
    FilterBuilder withFeatures(Map<String, Object> features);

    @Override
    FilterBuilder withFeature(String name, Object value);

    @Override
    FilterBuilder withCategory(String category);

    @Override
    FilterBuilder withOnInit(ProcessorOnInitCallback<Filter> onInitCallback);

    @Override
    FilterBuilder withEvents(List<String> eventNames);

    @Override
    FilterBuilder withEvent(String eventName);

    FilterBuilder withOnAccept(FilterOnAcceptCallback onAcceptCallback);
}
