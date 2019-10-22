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

package org.openksavi.sponge.correlator;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.EventSetProcessorBuilder;
import org.openksavi.sponge.ProcessorOnInitCallback;

/**
 * A correlator builder.
 */
public interface CorrelatorBuilder extends EventSetProcessorBuilder<Correlator> {

    @Override
    CorrelatorBuilder withName(String name);

    @Override
    CorrelatorBuilder withLabel(String label);

    @Override
    CorrelatorBuilder withDescription(String description);

    @Override
    CorrelatorBuilder withVersion(Integer version);

    @Override
    CorrelatorBuilder withFeatures(Map<String, Object> features);

    @Override
    CorrelatorBuilder withFeature(String name, Object value);

    @Override
    CorrelatorBuilder withCategory(String category);

    @Override
    CorrelatorBuilder withEvents(List<String> eventNames);

    @Override
    CorrelatorBuilder withEvent(String eventName);

    @Override
    CorrelatorBuilder withDuration(Duration duration);

    @Override
    CorrelatorBuilder withSynchronous(Boolean synchronous);

    CorrelatorBuilder withMaxInstances(int maxInstances);

    CorrelatorBuilder withInstanceSynchronous(boolean instanceSynchronous);

    @Override
    CorrelatorBuilder withOnInit(ProcessorOnInitCallback<Correlator> onInitCallback);

    CorrelatorBuilder withOnAcceptAsFirst(CorrelatorOnAcceptAsFirstCallback onAcceptAsFirstCallback);

    CorrelatorBuilder withOnEvent(CorrelatorOnEventCallback onEventCallback);

    CorrelatorBuilder withOnDuration(CorrelatorOnDurationCallback onDurationCallback);
}
