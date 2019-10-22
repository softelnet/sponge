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

package org.openksavi.sponge.kotlin;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.ProcessorOnInitCallback;
import org.openksavi.sponge.core.rule.BaseRuleBuilder;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.rule.RuleEventSpec;
import org.openksavi.sponge.rule.RuleOnRunCallback;

/**
 * Kotlin-specific implementation of a rule builder.
 */
public class KRuleBuilder extends BaseRuleBuilder {

    public KRuleBuilder(String name) {
        super(name);
    }

    @Override
    public KRuleBuilder withName(String name) {
        return (KRuleBuilder) super.withName(name);
    }

    @Override
    public KRuleBuilder withLabel(String label) {
        return (KRuleBuilder) super.withLabel(label);
    }

    @Override
    public KRuleBuilder withDescription(String description) {
        return (KRuleBuilder) super.withDescription(description);
    }

    @Override
    public KRuleBuilder withVersion(Integer version) {
        return (KRuleBuilder) super.withVersion(version);
    }

    @Override
    public KRuleBuilder withFeatures(Map<String, Object> features) {
        return (KRuleBuilder) super.withFeatures(features);
    }

    @Override
    public KRuleBuilder withFeature(String name, Object value) {
        return (KRuleBuilder) super.withFeature(name, value);
    }

    @Override
    public KRuleBuilder withCategory(String category) {
        return (KRuleBuilder) super.withCategory(category);
    }

    @Override
    public KRuleBuilder withEvents(List<String> eventStringSpecs) {
        return (KRuleBuilder) super.withEvents(eventStringSpecs);
    }

    public KRuleBuilder withEvents(String... eventStringSpecs) {
        return withEvents(Arrays.asList(eventStringSpecs));
    }

    @Override
    public KRuleBuilder withEvent(String eventStringSpec) {
        return (KRuleBuilder) super.withEvent(eventStringSpec);
    }

    @Override
    public KRuleBuilder withDuration(Duration duration) {
        return (KRuleBuilder) super.withDuration(duration);
    }

    @Override
    public KRuleBuilder withSynchronous(Boolean synchronous) {
        return (KRuleBuilder) super.withSynchronous(synchronous);
    }

    @Override
    public KRuleBuilder withEventSpecs(List<RuleEventSpec> eventSpecs) {
        return (KRuleBuilder) super.withEventSpecs(eventSpecs);
    }

    public KRuleBuilder withEventSpecs(RuleEventSpec... eventSpecs) {
        return withEventSpecs(Arrays.asList(eventSpecs));
    }

    @Override
    public KRuleBuilder withEventSpec(RuleEventSpec eventSpec) {
        return (KRuleBuilder) super.withEventSpec(eventSpec);
    }

    @Override
    public KRuleBuilder withOrdered(boolean ordered) {
        return (KRuleBuilder) super.withOrdered(ordered);
    }

    @Override
    public KRuleBuilder withCondition(String eventAlias, EventCondition condition) {
        return (KRuleBuilder) super.withCondition(eventAlias, condition);
    }

    @Override
    public KRuleBuilder withAllCondition(EventCondition condition) {
        return (KRuleBuilder) super.withAllCondition(condition);
    }

    @Override
    public KRuleBuilder withOnInit(ProcessorOnInitCallback<Rule> onInitCallback) {
        return (KRuleBuilder) super.withOnInit(onInitCallback);
    }

    @Override
    public KRuleBuilder withOnRun(RuleOnRunCallback onRunCallback) {
        return (KRuleBuilder) super.withOnRun(onRunCallback);
    }
}
