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

package org.openksavi.sponge.core.rule;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.ProcessorOnInitCallback;
import org.openksavi.sponge.core.BaseEventSetProcessorBuilder;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.rule.RuleBuilder;
import org.openksavi.sponge.rule.RuleEventSpec;
import org.openksavi.sponge.rule.RuleOnRunCallback;

/**
 * A base rule builder.
 */
public class BaseRuleBuilder extends BaseEventSetProcessorBuilder<Rule> implements RuleBuilder {

    private List<Object> genericEventSpecs = new ArrayList<>();

    private RuleOnRunCallback onRunCallback;

    public BaseRuleBuilder(String name) {
        super(new BaseRuleMeta(), name);
    }

    @Override
    public BaseRuleMeta getMeta() {
        return (BaseRuleMeta) super.getMeta();
    }

    @Override
    public BaseRuleBuilder withName(String name) {
        return (BaseRuleBuilder) super.withName(name);
    }

    @Override
    public BaseRuleBuilder withLabel(String label) {
        return (BaseRuleBuilder) super.withLabel(label);
    }

    @Override
    public BaseRuleBuilder withDescription(String description) {
        return (BaseRuleBuilder) super.withDescription(description);
    }

    @Override
    public BaseRuleBuilder withVersion(Integer version) {
        return (BaseRuleBuilder) super.withVersion(version);
    }

    @Override
    public BaseRuleBuilder withFeatures(Map<String, Object> features) {
        return (BaseRuleBuilder) super.withFeatures(features);
    }

    @Override
    public BaseRuleBuilder withFeature(String name, Object value) {
        return (BaseRuleBuilder) super.withFeature(name, value);
    }

    @Override
    public BaseRuleBuilder withCategory(String category) {
        return (BaseRuleBuilder) super.withCategory(category);
    }

    @Override
    public BaseRuleBuilder withEvents(List<String> eventStringSpecs) {
        genericEventSpecs.addAll(eventStringSpecs);
        return this;
    }

    @Override
    public BaseRuleBuilder withEvent(String eventStringSpec) {
        return withEvents(Arrays.asList(eventStringSpec));
    }

    @Override
    public BaseRuleBuilder withDuration(Duration duration) {
        return (BaseRuleBuilder) super.withDuration(duration);
    }

    @Override
    public BaseRuleBuilder withSynchronous(Boolean synchronous) {
        return (BaseRuleBuilder) super.withSynchronous(synchronous);
    }

    @Override
    public BaseRuleBuilder withEventSpecs(List<RuleEventSpec> eventSpecs) {
        genericEventSpecs.addAll(eventSpecs);
        return this;
    }

    @Override
    public BaseRuleBuilder withEventSpec(RuleEventSpec eventSpec) {
        return withEventSpecs(Arrays.asList(eventSpec));
    }

    @Override
    public BaseRuleBuilder withOrdered(boolean ordered) {
        getMeta().setOrdered(ordered);
        return this;
    }

    @Override
    public BaseRuleBuilder withCondition(String eventAlias, EventCondition condition) {
        getMeta().addEventConditions(eventAlias, Arrays.asList(condition));
        return this;
    }

    @Override
    public BaseRuleBuilder withAllCondition(EventCondition condition) {
        getMeta().addAllEventConditions(Arrays.asList(condition));
        return this;
    }

    @Override
    public BaseRuleBuilder withOnInit(ProcessorOnInitCallback<Rule> onInitCallback) {
        return (BaseRuleBuilder) super.withOnInit(onInitCallback);
    }

    @Override
    public BaseRuleBuilder withOnRun(RuleOnRunCallback onRunCallback) {
        this.onRunCallback = onRunCallback;

        return this;
    }

    public List<Object> getGenericEventSpecs() {
        return genericEventSpecs;
    }

    public void setGenericEventSpecs(List<Object> genericEventSpecs) {
        this.genericEventSpecs = genericEventSpecs;
    }

    public RuleOnRunCallback getOnRunCallback() {
        return onRunCallback;
    }

    public void setOnRunCallback(RuleOnRunCallback onRunCallback) {
        this.onRunCallback = onRunCallback;
    }

    @Override
    public Rule build() {
        return new DefaultBuilderRule(this);
    }
}
