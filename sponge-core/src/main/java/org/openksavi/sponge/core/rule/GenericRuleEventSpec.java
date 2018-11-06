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

import org.openksavi.sponge.rule.EventMode;
import org.openksavi.sponge.rule.RuleEventSpec;

public class GenericRuleEventSpec implements RuleEventSpec {

    private String name;

    private String alias;

    private EventMode mode;

    public GenericRuleEventSpec(String name, String alias, EventMode mode) {
        this.name = name;
        this.alias = alias;
        this.mode = mode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public EventMode getMode() {
        return mode;
    }
}
