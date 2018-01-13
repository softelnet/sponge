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

import com.google.common.collect.Lists;

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
    public final void setEventAliases(String... aliases) {
        getAdapter().setEventAliases(aliases);
    }

    @Override
    public final String[] getEventAliases() {
        return getAdapter().getEventAliases();
    }

    @Override
    public final String getEventAlias(int index) {
        return getAdapter().getEventAlias(index);
    }

    @Override
    public final void setEventModes(EventMode... modes) {
        getAdapter().setEventModes(modes);
    }

    @Override
    public final EventMode[] getEventModes() {
        return getAdapter().getEventModes();
    }

    @Override
    public final EventMode getEventMode(int index) {
        return getAdapter().getEventMode(index);
    }

    @Override
    public final void addJavaConditions(String eventAlias, EventCondition... conditions) {
        getAdapter().addJavaConditions(eventAlias, conditions);
    }

    @Override
    public final void addAllJavaConditions(EventCondition... conditions) {
        getAdapter().addAllJavaConditions(conditions);
    }

    @Override
    public final void addJavaCondition(String eventAlias, EventCondition condition) {
        getAdapter().addJavaCondition(eventAlias, condition);
    }

    @Override
    public final List<EventCondition> getConditions(String eventAlias) {
        return getAdapter().getConditions(eventAlias);
    }

    @Override
    public final Map<String, List<EventCondition>> getConditions() {
        return getAdapter().getConditions();
    }

    @Override
    public final Event getEvent(String eventAlias) {
        return getRuleAdapterImpl().getEvent(eventAlias);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setEvents(Object[] events) {
        // Some scripting languages may invoke this method differently, because the array of Objects is too generic.
        if (events.length == 1 && events[0] != null && events[0] instanceof Iterable) {
            getRuleAdapterImpl().setEventSpecs(Lists.newArrayList((Iterable<Object>) events[0]));
        } else {
            getRuleAdapterImpl().setEventSpecs(Arrays.asList(events));
        }
    }

    @Override
    public void setEvents(String... eventSpecs) {
        setEvents((Object[]) eventSpecs);
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
}
