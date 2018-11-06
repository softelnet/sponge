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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openksavi.sponge.core.BaseEventSetProcessor;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.EventMode;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.rule.RuleAdapter;
import org.openksavi.sponge.rule.RuleEventSpec;

public abstract class BaseRule extends BaseEventSetProcessor<RuleAdapter> implements Rule {

    protected final BaseRuleAdapter getRuleAdapterImpl() {
        return (BaseRuleAdapter) super.getAdapter();
    }

    @Override
    public final void addEventConditions(String eventAlias, EventCondition... conditions) {
        getAdapter().addEventConditions(eventAlias, conditions);
    }

    @Override
    public final void addAllEventConditions(EventCondition... conditions) {
        getAdapter().addAllEventConditions(conditions);
    }

    @Override
    public final void addEventCondition(String eventAlias, EventCondition condition) {
        getAdapter().addEventCondition(eventAlias, condition);
    }

    @Override
    public final List<EventCondition> getEventConditions(String eventAlias) {
        return getAdapter().getEventConditions(eventAlias);
    }

    @Override
    public final Map<String, List<EventCondition>> getEventConditions() {
        return getAdapter().getEventConditions();
    }

    @Override
    public final Event getEvent(String eventAlias) {
        return getRuleAdapterImpl().getEvent(eventAlias);
    }

    @Override
    public void setEvents(String... eventStringSpecs) {
        getRuleAdapterImpl().setEventStringSpecs(Arrays.asList(eventStringSpecs));
    }

    @Override
    public void setEventSpecs(RuleEventSpec... eventSpecs) {
        getRuleAdapterImpl().setEventSpecs(Arrays.asList(eventSpecs));
    }

    @Override
    public void setEvent(String eventStringSpec) {
        setEvents(eventStringSpec);
    }

    @Override
    public void setEventSpec(RuleEventSpec eventSpec) {
        setEventSpecs(eventSpec);
    }

    @Override
    public boolean isOrdered() {
        return getAdapter().isOrdered();
    }

    @Override
    public void setOrdered(boolean ordered) {
        getAdapter().setOrdered(ordered);
    }

    @Override
    public final RuleEventSpec makeEventSpec(String eventName, String eventAlias, EventMode eventMode) {
        return getRuleAdapterImpl().makeEventSpec(eventName, eventAlias, eventMode);
    }

    @Override
    public final RuleEventSpec makeEventSpec(String eventName, EventMode eventMode) {
        return getRuleAdapterImpl().makeEventSpec(eventName, eventMode);
    }

    @Override
    public final RuleEventSpec makeEventSpec(String eventName, String eventAlias) {
        return getRuleAdapterImpl().makeEventSpec(eventName, eventAlias);
    }

    @Override
    public final RuleEventSpec makeEventSpec(String eventName) {
        return getRuleAdapterImpl().makeEventSpec(eventName);
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

    protected final EventCondition createEventConditionForMethods(List<String> names) {
        return new CompositeEventCondition(names.stream().map(name -> createEventConditionForMethod(name)).collect(Collectors.toList()));
    }

    protected final EventCondition createEventConditionForMethod(String name) {
        return ReflectionEventCondition.create(this, name);
    }
}
