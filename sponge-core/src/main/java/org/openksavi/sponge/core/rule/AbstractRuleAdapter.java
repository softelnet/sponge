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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    protected AbstractRuleAdapter(BaseRuleDefinition definition) {
        super(definition);
    }

    @Override
    public boolean isOrdered() {
        return getDefinition().isOrdered();
    }

    @Override
    public void setOrdered(boolean ordered) {
        getDefinition().setOrdered(ordered);
    }

    @Override
    public void addEventConditions(String eventAlias, EventCondition... conditions) {
        getDefinition().addEventConditions(eventAlias, conditions);
    }

    @Override
    public void addAllEventConditions(EventCondition... conditions) {
        getDefinition().addAllEventConditions(conditions);
    }

    @Override
    public void addEventCondition(String eventAlias, EventCondition condition) {
        getDefinition().addEventCondition(eventAlias, condition);
    }

    @Override
    public List<EventCondition> getEventConditions(String eventAlias) {
        return getDefinition().getEventConditions(eventAlias);
    }

    @Override
    public final Map<String, List<EventCondition>> getEventConditions() {
        return getDefinition().getEventConditions();
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

    public void setEventStringSpecs(List<String> eventStringSpecs) {
        setEventSpecs(eventStringSpecs.stream().map(spec -> getKnowledgeBase().getInterpreter().getRuleEventSpec(spec))
                .collect(Collectors.toList()));
    }

    public void setEventSpecs(List<RuleEventSpec> eventSpecs) {
        getDefinition().setEventNames(eventSpecs.stream().map(RuleEventSpec::getName).collect(Collectors.toList()));
        getDefinition().setEventSpecs(eventSpecs);
    }

    @Override
    public BaseRuleDefinition getDefinition() {
        return (BaseRuleDefinition) super.getDefinition();
    }

    @Override
    public int getEventCount() {
        return getDefinition().getEventNames().size();
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
