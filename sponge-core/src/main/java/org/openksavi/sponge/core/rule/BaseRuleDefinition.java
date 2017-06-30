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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.core.BaseEventSetProcessorDefinition;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.EventMode;
import org.openksavi.sponge.rule.RuleDefinition;

public class BaseRuleDefinition extends BaseEventSetProcessorDefinition implements RuleDefinition {

    protected String[] aliases;

    protected EventMode[] modes;

    protected Map<String, List<EventCondition>> conditions = Collections.synchronizedMap(new LinkedHashMap<>());

    public BaseRuleDefinition() {
    }

    @Override
    public void setEventAliases(String... aliases) {
        this.aliases = aliases;
    }

    @Override
    public String[] getEventAliases() {
        return aliases;
    }

    @Override
    public String getEventAlias(int index) {
        return aliases[index];
    }

    @Override
    public void setEventModes(EventMode... modes) {
        this.modes = modes;
    }

    @Override
    public EventMode[] getEventModes() {
        return modes;
    }

    @Override
    public EventMode getEventMode(int index) {
        return modes[index];
    }

    @Override
    public void setJavaConditions(String eventAlias, EventCondition... newConditions) {
        synchronized (conditions) {
            List<EventCondition> eventConditions = safeGetEventConditions(eventAlias);

            eventConditions.clear();
            eventConditions.addAll(Arrays.asList(newConditions));
        }
    }

    @Override
    public synchronized void addJavaCondition(String eventAlias, EventCondition condition) {
        synchronized (conditions) {
            List<EventCondition> eventConditions = safeGetEventConditions(eventAlias);

            eventConditions.add(condition);
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
    public List<EventCondition> getConditions(String eventAlias) {
        return conditions.get(eventAlias);
    }
}
