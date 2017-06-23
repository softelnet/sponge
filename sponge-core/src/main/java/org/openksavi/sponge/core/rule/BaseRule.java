/*
 * Copyright 2016-2017 Softelnet.
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

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.core.BaseEventSetProcessor;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.EventMode;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.rule.RuleAdapter;
import org.openksavi.sponge.rule.RuleEventSpec;

public abstract class BaseRule extends BaseEventSetProcessor<RuleAdapter> implements Rule {

    protected final BaseRuleAdapter getAdapterImpl() {
        return (BaseRuleAdapter) super.getAdapter();
    }

    @Override
    public final void setEventAliases(String[] aliases) {
        getAdapter().setEventAliases(aliases);
    }

    @Override
    public final String[] getEventAliases() {
        return getAdapter().getEventAliases();
    }

    @Override
    public final void setEventModes(EventMode[] modes) {
        getAdapter().setEventModes(modes);

    }

    @Override
    public final EventMode[] getEventModes() {
        return getAdapter().getEventModes();
    }

    @Override
    public final void setJavaConditions(String eventAlias, EventCondition... conditions) {
        getAdapter().setJavaConditions(eventAlias, conditions);
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
    public final Event getEvent(String eventAlias) {
        return getAdapterImpl().getEvent(eventAlias);
    }

    @Override
    public final void setEvents(Object[] events) {
        getAdapterImpl().setEvents(events);
    }

    @Override
    public final RuleEventSpec makeEventSpec(String eventName, String eventAlias, EventMode eventMode) {
        return getAdapterImpl().makeEventSpec(eventName, eventAlias, eventMode);
    }

    @Override
    public final RuleEventSpec makeEventSpec(String eventName, EventMode eventMode) {
        return getAdapterImpl().makeEventSpec(eventName, eventMode);
    }

    @Override
    public final RuleEventSpec makeEventSpec(String eventName, String eventAlias) {
        return getAdapterImpl().makeEventSpec(eventName, eventAlias);
    }

    @Override
    public final RuleEventSpec makeEventSpec(String eventName) {
        return getAdapterImpl().makeEventSpec(eventName);
    }

    public final Map<String, Event> getEventAliasMap() {
        return getAdapterImpl().getEventAliasMap();
    }

    public final List<Event> getEventSequence() {
        return getAdapterImpl().getEventSequence();
    }

    @Override
    public final RuleAdapter createAdapter() {
        return new BaseRuleAdapter(new BaseRuleDefinition());
    }
}
