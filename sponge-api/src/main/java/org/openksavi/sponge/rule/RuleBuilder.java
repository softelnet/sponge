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

package org.openksavi.sponge.rule;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.EventSetProcessorBuilder;
import org.openksavi.sponge.ProcessorOnInitCallback;

/**
 * A rule builder.
 */
public interface RuleBuilder extends EventSetProcessorBuilder<Rule> {

    @Override
    RuleBuilder withName(String name);

    @Override
    RuleBuilder withLabel(String label);

    @Override
    RuleBuilder withDescription(String description);

    @Override
    RuleBuilder withVersion(Integer version);

    @Override
    RuleBuilder withFeatures(Map<String, Object> features);

    @Override
    RuleBuilder withFeature(String name, Object value);

    @Override
    RuleBuilder withCategory(String category);

    @Override
    RuleBuilder withEvents(List<String> eventStringSpecs);

    @Override
    RuleBuilder withEvent(String eventStringSpecs);

    @Override
    RuleBuilder withDuration(Duration duration);

    @Override
    RuleBuilder withSynchronous(Boolean synchronous);

    RuleBuilder withEventSpecs(List<RuleEventSpec> eventSpecs);

    RuleBuilder withEventSpec(RuleEventSpec eventSpec);

    RuleBuilder withOrdered(boolean ordered);

    RuleBuilder withCondition(String eventAlias, EventCondition condition);

    RuleBuilder withAllCondition(EventCondition condition);

    @Override
    RuleBuilder withOnInit(ProcessorOnInitCallback<Rule> onInitCallback);

    RuleBuilder withOnRun(RuleOnRunCallback onRunCallback);
}
