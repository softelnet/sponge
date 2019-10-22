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

package org.openksavi.sponge;

import java.util.List;
import java.util.Map;

/**
 * An event processor builder.
 */
public interface EventProcessorBuilder<T extends EventProcessor<?>> extends ProcessorBuilder<T> {

    @Override
    EventProcessorBuilder<T> withName(String name);

    @Override
    EventProcessorBuilder<T> withLabel(String label);

    @Override
    EventProcessorBuilder<T> withDescription(String description);

    @Override
    EventProcessorBuilder<T> withVersion(Integer version);

    @Override
    EventProcessorBuilder<T> withFeatures(Map<String, Object> features);

    @Override
    EventProcessorBuilder<T> withFeature(String name, Object value);

    @Override
    EventProcessorBuilder<T> withCategory(String category);

    @Override
    EventProcessorBuilder<T> withOnInit(ProcessorOnInitCallback<T> onInitCallback);

    EventProcessorBuilder<T> withEvents(List<String> eventNames);

    EventProcessorBuilder<T> withEvent(String eventName);
}
