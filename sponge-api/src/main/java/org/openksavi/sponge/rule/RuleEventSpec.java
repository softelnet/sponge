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

package org.openksavi.sponge.rule;

import org.openksavi.sponge.SpongeConstants;

/**
 * Rule event specification.
 */
public class RuleEventSpec {

    private String name;

    private String alias;

    private EventMode mode;

    public RuleEventSpec(String name, String alias, EventMode mode) {
        this.name = name;
        this.alias = alias != null ? alias : name;
        this.mode = mode != null ? mode : SpongeConstants.DEFAULT_RULE_EVENT_MODE;
    }

    public RuleEventSpec(String name, String alias) {
        this(name, alias, null);
    }

    public RuleEventSpec(String name, EventMode mode) {
        this(name, null, mode);
    }

    public RuleEventSpec(String name) {
        this(name, null, null);
    }

    /**
     * Returns the event name.
     *
     * @return the event name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the event alias.
     *
     * @return the event alias.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Returns the event mode.
     *
     * @return the event mode.
     */
    public EventMode getMode() {
        return mode;
    }
}
