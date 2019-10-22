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

package org.openksavi.sponge.java;

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
 * Java-specific implementation of a rule builder.
 */
public class JRuleBuilder extends BaseRuleBuilder {

    public JRuleBuilder(String name) {
        super(name);
    }

    @Override
    public JRuleBuilder withName(String name) {
        return (JRuleBuilder) super.withName(name);
    }

    @Override
    public JRuleBuilder withLabel(String label) {
        return (JRuleBuilder) super.withLabel(label);
    }

    @Override
    public JRuleBuilder withDescription(String description) {
        return (JRuleBuilder) super.withDescription(description);
    }

    @Override
    public JRuleBuilder withVersion(Integer version) {
        return (JRuleBuilder) super.withVersion(version);
    }

    @Override
    public JRuleBuilder withFeatures(Map<String, Object> features) {
        return (JRuleBuilder) super.withFeatures(features);
    }

    @Override
    public JRuleBuilder withFeature(String name, Object value) {
        return (JRuleBuilder) super.withFeature(name, value);
    }

    @Override
    public JRuleBuilder withCategory(String category) {
        return (JRuleBuilder) super.withCategory(category);
    }

    @Override
    public JRuleBuilder withEvents(List<String> eventStringSpecs) {
        return (JRuleBuilder) super.withEvents(eventStringSpecs);
    }

    public JRuleBuilder withEvents(String... eventStringSpecs) {
        return withEvents(Arrays.asList(eventStringSpecs));
    }

    @Override
    public JRuleBuilder withEvent(String eventStringSpec) {
        return (JRuleBuilder) super.withEvent(eventStringSpec);
    }

    @Override
    public JRuleBuilder withDuration(Duration duration) {
        return (JRuleBuilder) super.withDuration(duration);
    }

    @Override
    public JRuleBuilder withSynchronous(Boolean synchronous) {
        return (JRuleBuilder) super.withSynchronous(synchronous);
    }

    @Override
    public JRuleBuilder withEventSpecs(List<RuleEventSpec> eventSpecs) {
        return (JRuleBuilder) super.withEventSpecs(eventSpecs);
    }

    public JRuleBuilder withEventSpecs(RuleEventSpec... eventSpecs) {
        return withEventSpecs(Arrays.asList(eventSpecs));
    }

    @Override
    public JRuleBuilder withEventSpec(RuleEventSpec eventSpec) {
        return (JRuleBuilder) super.withEventSpec(eventSpec);
    }

    @Override
    public JRuleBuilder withOrdered(boolean ordered) {
        return (JRuleBuilder) super.withOrdered(ordered);
    }

    @Override
    public JRuleBuilder withCondition(String eventAlias, EventCondition condition) {
        return (JRuleBuilder) super.withCondition(eventAlias, condition);
    }

    @Override
    public JRuleBuilder withAllCondition(EventCondition condition) {
        return (JRuleBuilder) super.withAllCondition(condition);
    }

    @Override
    public JRuleBuilder withOnInit(ProcessorOnInitCallback<Rule> onInitCallback) {
        return (JRuleBuilder) super.withOnInit(onInitCallback);
    }

    @Override
    public JRuleBuilder withOnRun(RuleOnRunCallback onRunCallback) {
        return (JRuleBuilder) super.withOnRun(onRunCallback);
    }
}
