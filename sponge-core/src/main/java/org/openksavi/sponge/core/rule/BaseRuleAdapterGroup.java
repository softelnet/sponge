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

import org.openksavi.sponge.core.BaseEventSetProcessorAdapterGroup;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.processing.EventSetProcessorMainProcessingUnitHandler;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.RuleAdapter;
import org.openksavi.sponge.rule.RuleAdapterGroup;

/**
 * Rule adapter group.
 */
public class BaseRuleAdapterGroup extends BaseEventSetProcessorAdapterGroup<RuleAdapter> implements RuleAdapterGroup {

    /**
     * Creates a new rule group.
     *
     * @param ruleDefinition
     *            rule definition.
     * @param handler
     *            a processing unit handler.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public BaseRuleAdapterGroup(BaseRuleDefinition ruleDefinition,
            EventSetProcessorMainProcessingUnitHandler<RuleAdapterGroup, RuleAdapter> handler) {
        super(ruleDefinition, (EventSetProcessorMainProcessingUnitHandler) handler);
    }

    @Override
    public ProcessorType getType() {
        return ProcessorType.RULE_GROUP;
    }

    /**
     * Checks if there is a need for creating a new rule instance.
     *
     * @param event
     *            event.
     * @return {@code true} if there is a need for creating
     *         a new rule instance.
     */
    @Override
    public boolean needNewInstance(Event event) {
        String events[] = getDefinition().getEventNames();
        return (events.length > 0 && event.getName().equals(events[0]));
    }

    @Override
    protected BaseRuleAdapter createNewEventSetProcessorAdapter() {
        return new BaseRuleAdapter((BaseRuleDefinition) getDefinition());
    }

    @Override
    public void validate() {
        //
    }

    @Override
    public List<RuleAdapter> getRules() {
        return getEventSetProcessorAdapters();
    }
}
