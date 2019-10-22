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

package org.openksavi.sponge.core.rule;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.core.BaseEventSetProcessor;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.rule.RuleAdapter;
import org.openksavi.sponge.rule.RuleEventSpec;

public abstract class BaseRule extends BaseEventSetProcessor<RuleAdapter> implements Rule {

    protected final BaseRuleAdapter getRuleAdapterImpl() {
        return (BaseRuleAdapter) super.getAdapter();
    }

    @Override
    public BaseRuleMeta getMeta() {
        return (BaseRuleMeta) super.getMeta();
    }

    @Override
    public final Event getEvent(String eventAlias) {
        return getRuleAdapterImpl().getEvent(eventAlias);
    }

    public final Map<String, Event> getEventAliasMap() {
        return getRuleAdapterImpl().getEventAliasMap();
    }

    @Override
    public final List<Event> getEventSequence() {
        return getRuleAdapterImpl().getEventSequence();
    }

    @Override
    public final RuleAdapter createAdapter() {
        return new BaseRuleAdapter(new BaseRuleDefinition());
    }

    @Override
    public BaseRule withName(String name) {
        return (BaseRule) super.withName(name);
    }

    @Override
    public BaseRule withLabel(String label) {
        return (BaseRule) super.withLabel(label);
    }

    @Override
    public BaseRule withDescription(String description) {
        return (BaseRule) super.withDescription(description);
    }

    @Override
    public BaseRule withVersion(Integer version) {
        return (BaseRule) super.withVersion(version);
    }

    @Override
    public BaseRule withFeatures(Map<String, Object> features) {
        return (BaseRule) super.withFeatures(features);
    }

    @Override
    public BaseRule withFeature(String name, Object value) {
        return (BaseRule) super.withFeature(name, value);
    }

    @Override
    public BaseRule withCategory(String category) {
        return (BaseRule) super.withCategory(category);
    }

    @Override
    public BaseRule withEvents(List<String> eventStringSpecs) {
        getMeta().addEventSpecs(SpongeUtils.convertRuleEventSpecs(getKnowledgeBase(), eventStringSpecs));
        return this;
    }

    @Override
    public BaseRule withEvent(String eventStringSpec) {
        return withEvents(Arrays.asList(eventStringSpec));
    }

    @Override
    public BaseRule withDuration(Duration duration) {
        return (BaseRule) super.withDuration(duration);
    }

    @Override
    public BaseRule withSynchronous(Boolean synchronous) {
        return (BaseRule) super.withSynchronous(synchronous);
    }

    public BaseRule withEventConditions(String eventAlias, List<EventCondition> conditions) {
        getMeta().addEventConditions(eventAlias, conditions);
        return this;
    }

    public BaseRule withEventCondition(String eventAlias, EventCondition condition) {
        withEventConditions(eventAlias, Arrays.asList(condition));
        return this;
    }

    public BaseRule withAllEventConditions(List<EventCondition> conditions) {
        getMeta().addAllEventConditions(conditions);
        return this;
    }

    public BaseRule withAllEventCondition(EventCondition condition) {
        return withAllEventConditions(Arrays.asList(condition));
    }

    public BaseRule withEventSpecs(List<RuleEventSpec> eventSpecs) {
        getMeta().addEventSpecs(eventSpecs);
        return this;
    }

    public BaseRule withEventSpec(RuleEventSpec eventSpec) {
        return withEventSpecs(Arrays.asList(eventSpec));
    }

    public BaseRule withOrdered(boolean ordered) {
        getMeta().setOrdered(ordered);
        return this;
    }
}
