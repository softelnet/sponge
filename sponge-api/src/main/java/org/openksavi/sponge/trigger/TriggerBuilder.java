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

package org.openksavi.sponge.trigger;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.EventProcessorBuilder;
import org.openksavi.sponge.ProcessorOnInitCallback;

/**
 * A trigger builder.
 */
public interface TriggerBuilder extends EventProcessorBuilder<Trigger> {

    @Override
    TriggerBuilder withName(String name);

    @Override
    TriggerBuilder withLabel(String label);

    @Override
    TriggerBuilder withDescription(String description);

    @Override
    TriggerBuilder withVersion(Integer version);

    @Override
    TriggerBuilder withFeatures(Map<String, Object> features);

    @Override
    TriggerBuilder withFeature(String name, Object value);

    @Override
    TriggerBuilder withCategory(String category);

    @Override
    TriggerBuilder withEvents(List<String> eventNames);

    @Override
    TriggerBuilder withEvent(String eventName);

    @Override
    TriggerBuilder withOnInit(ProcessorOnInitCallback<Trigger> onInitCallback);

    TriggerBuilder withOnAccept(TriggerOnAcceptCallback onAcceptCallback);

    TriggerBuilder withOnRun(TriggerOnRunCallback onRunCallback);
}
