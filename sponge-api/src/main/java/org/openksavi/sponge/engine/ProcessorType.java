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

package org.openksavi.sponge.engine;

import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.correlator.CorrelatorAdapter;
import org.openksavi.sponge.correlator.CorrelatorAdapterGroup;
import org.openksavi.sponge.filter.FilterAdapter;
import org.openksavi.sponge.rule.RuleAdapter;
import org.openksavi.sponge.rule.RuleAdapterGroup;
import org.openksavi.sponge.trigger.TriggerAdapter;

/**
 * Processor type.
 */
@SuppressWarnings("rawtypes")
public enum ProcessorType {

    ACTION("action", ActionAdapter.class),

    FILTER("filter", FilterAdapter.class),

    TRIGGER("trigger", TriggerAdapter.class),

    RULE_GROUP("ruleGroup", RuleAdapterGroup.class),

    RULE("rule", RuleAdapter.class),

    CORRELATOR_GROUP("correlatorGroup", CorrelatorAdapterGroup.class),

    CORRELATOR("correlator", CorrelatorAdapter.class);

    private String name;

    private Class<? extends ProcessorAdapter> adapterClass;

    ProcessorType(String name, Class<? extends ProcessorAdapter> adapterClass) {
        this.name = name;
        this.adapterClass = adapterClass;
    }

    /**
     * Returns name.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }

    public Class<? extends ProcessorAdapter> getAdapterClass() {
        return adapterClass;
    }

    public String getDisplayName() {
        return name.toUpperCase();
    }
}
