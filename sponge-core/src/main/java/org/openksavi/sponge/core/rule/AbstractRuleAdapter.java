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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.BaseEventSetProcessorAdapter;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.EventMode;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.rule.RuleAdapter;
import org.openksavi.sponge.rule.RuleEventSpec;

/**
 * Abstract rule adapter.
 */
public abstract class AbstractRuleAdapter<T extends Rule> extends BaseEventSetProcessorAdapter<Rule> implements RuleAdapter {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRuleAdapter.class);

    protected AbstractRuleAdapter(BaseRuleDefinition definition) {
        super(definition);
    }

    @Override
    public void setJavaConditions(String eventAlias, EventCondition... conditions) {
        getDefinition().setJavaConditions(eventAlias, conditions);
    }

    @Override
    public void addJavaCondition(String eventAlias, EventCondition condition) {
        getDefinition().addJavaCondition(eventAlias, condition);
    }

    @Override
    public List<EventCondition> getConditions(String eventAlias) {
        return getDefinition().getConditions(eventAlias);
    }

    @Override
    protected void onDuration() {
        runRule();
    }

    /**
     * Attempts to run (fire) this rule.
     *
     * @return {@code true} if this rule has been run.
     */
    protected abstract boolean runRule();

    @Override
    public void setEventAliases(String... aliases) {
        getDefinition().setEventAliases(aliases);
    }

    @Override
    public String[] getEventAliases() {
        return getDefinition().getEventAliases();
    }

    @Override
    public String getEventAlias(int index) {
        return getDefinition().getEventAlias(index);
    }

    @Override
    public void setEventModes(EventMode... modes) {
        getDefinition().setEventModes(modes);
    }

    @Override
    public EventMode[] getEventModes() {
        return getDefinition().getEventModes();
    }

    @Override
    public EventMode getEventMode(int index) {
        return getDefinition().getEventMode(index);
    }

    public void setEvents(Object[] events) {
        String[] eventNames = new String[events.length];
        String[] eventAliases = new String[events.length];
        EventMode[] modes = new EventMode[events.length];

        for (int i = 0; i < events.length; i++) {
            RuleEventSpec eventSpec = getKnowledgeBase().getInterpreter().getRuleEventSpec(events[i]);

            eventNames[i] = eventSpec.getEventName();
            eventAliases[i] = eventSpec.getEventAlias();
            modes[i] = eventSpec.getEventMode();
        }

        setEventNames(eventNames);
        setEventAliases(eventAliases);
        setEventModes(modes);
    }

    @Override
    public BaseRuleDefinition getDefinition() {
        return (BaseRuleDefinition) super.getDefinition();
    }

    @Override
    public int getEventCount() {
        return getDefinition().getEventNames().length;
    }

    public RuleEventSpec makeEventSpec(String eventName, String eventAlias, EventMode eventMode) {
        return new GenericRuleEventSpec(eventName, eventAlias, eventMode);
    }

    public RuleEventSpec makeEventSpec(String eventName, EventMode eventMode) {
        return new GenericRuleEventSpec(eventName, eventName, eventMode);
    }

    public RuleEventSpec makeEventSpec(String eventName, String eventAlias) {
        return new GenericRuleEventSpec(eventName, eventAlias, DEFAULT_MODE);
    }

    public RuleEventSpec makeEventSpec(String eventName) {
        return new GenericRuleEventSpec(eventName, eventName, DEFAULT_MODE);
    }
}
