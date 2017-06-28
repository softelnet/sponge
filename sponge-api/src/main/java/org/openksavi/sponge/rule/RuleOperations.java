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

package org.openksavi.sponge.rule;

import java.util.List;

import org.openksavi.sponge.EventSetProcessorOperations;

/**
 * Rule operations.
 */
public interface RuleOperations extends EventSetProcessorOperations {

    /**
     * Sets event aliases.
     *
     * @param aliases
     *            event aliases.
     */
    void setEventAliases(String... aliases);

    /**
     * Returns event aliases.
     *
     * @return event aliases.
     */
    String[] getEventAliases();

    /**
     * Returns an event alias for the specified event index.
     *
     * @param index
     *            event index.
     *
     * @return event alias.
     */
    String getEventAlias(int index);

    /**
     * Sets event modes.
     *
     * @param modes
     *            event modes.
     */
    void setEventModes(EventMode... modes);

    /**
     * Returns event modes.
     *
     * @return event modes.
     */
    EventMode[] getEventModes();

    /**
     * Returns an event mode for the specified event index.
     *
     * @param index
     *            event index.
     *
     * @return event mode.
     */
    EventMode getEventMode(int index);

    /**
     * Sets event conditions.
     *
     * @param eventAlias
     *            event alias.
     * @param conditions
     *            event conditions.
     */
    void setJavaConditions(String eventAlias, EventCondition... conditions);

    /**
     * Adds an event condition.
     *
     * @param eventAlias
     *            event alias.
     * @param condition
     *            event condition.
     */
    void addJavaCondition(String eventAlias, EventCondition condition);

    /**
     * Returns event conditions for the specified event alias.
     *
     * @param eventAlias
     *            event alias.
     * @return event conditions.
     */
    List<EventCondition> getConditions(String eventAlias);
}
