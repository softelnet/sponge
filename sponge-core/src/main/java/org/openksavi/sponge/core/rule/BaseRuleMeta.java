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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.core.BaseEventSetProcessorMeta;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.RuleEventSpec;
import org.openksavi.sponge.rule.RuleMeta;

/**
 * A base rule metadata.
 */
public class BaseRuleMeta extends BaseEventSetProcessorMeta implements RuleMeta {

    protected boolean ordered = true;

    protected List<RuleEventSpec> eventSpecs;

    protected Map<String, List<EventCondition>> conditions = Collections.synchronizedMap(new LinkedHashMap<>());

    @Override
    public boolean isOrdered() {
        return ordered;
    }

    @Override
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    @Override
    public List<RuleEventSpec> getEventSpecs() {
        return eventSpecs;
    }

    @Override
    public RuleEventSpec getEventSpec(int index) {
        Validate.isTrue(index >= 0 && index < eventSpecs.size(), "Invalid event specification index: %d", index);
        return eventSpecs.get(index);
    }

    @Override
    public void setEventSpecs(List<RuleEventSpec> eventSpecs) {
        this.eventSpecs = new ArrayList<>(eventSpecs);
        setEventNames(eventSpecs.stream().map(RuleEventSpec::getName).collect(Collectors.toList()));
    }

    public void addEventSpecs(List<RuleEventSpec> eventSpecs) {
        if (this.eventSpecs != null) {
            this.eventSpecs.addAll(eventSpecs);
            addEventNames(eventSpecs.stream().map(RuleEventSpec::getName).collect(Collectors.toList()));
        } else {
            setEventSpecs(eventSpecs);
        }
    }

    @Override
    public void addEventConditions(String eventAlias, List<EventCondition> newConditions) {
        synchronized (conditions) {
            safeGetEventConditions(eventAlias).addAll(newConditions);
        }
    }

    @Override
    public synchronized void addEventCondition(String eventAlias, EventCondition condition) {
        synchronized (conditions) {
            safeGetEventConditions(eventAlias).add(condition);
        }
    }

    @Override
    public void addAllEventConditions(List<EventCondition> newConditions) {
        Validate.isTrue(eventSpecs != null && !eventSpecs.isEmpty(),
                "Tring to add conditions to all events when no events have been specified");
        synchronized (conditions) {
            eventSpecs.forEach(spec -> addEventConditions(spec.getAlias(), newConditions));
        }
    }

    protected List<EventCondition> safeGetEventConditions(String eventAlias) {
        synchronized (conditions) {
            List<EventCondition> eventConditions = conditions.get(eventAlias);
            if (eventConditions == null) {
                eventConditions = Collections.synchronizedList(new ArrayList<>());
                conditions.put(eventAlias, eventConditions);
            }

            return eventConditions;
        }
    }

    @Override
    public List<EventCondition> getEventConditions(String eventAlias) {
        return conditions.get(eventAlias);
    }

    @Override
    public Map<String, List<EventCondition>> getEventConditions() {
        return conditions;
    }
}
