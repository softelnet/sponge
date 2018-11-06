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

package org.openksavi.sponge.rule;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.EventSetProcessorOperations;

/**
 * Rule operations.
 */
public interface RuleOperations extends EventSetProcessorOperations {

    /**
     * Sets the events for this rule to be ordered or unordered.
     *
     * @param ordered ordered.
     */
    void setOrdered(boolean ordered);

    /**
     * Returns {@code true} if this rule is ordered.
     *
     * @return {@code true} if this rule is ordered.
     */
    boolean isOrdered();

    /**
     * Adds event conditions.
     *
     * @param eventAlias event alias.
     * @param conditions event conditions.
     */
    void addEventConditions(String eventAlias, EventCondition... conditions);

    /**
     * Adds event conditions for all events.
     *
     * @param conditions event conditions.
     */
    void addAllEventConditions(EventCondition... conditions);

    /**
     * Adds an event condition.
     *
     * @param eventAlias event alias.
     * @param condition event condition.
     */
    void addEventCondition(String eventAlias, EventCondition condition);

    /**
     * Returns event conditions for the specified event alias.
     *
     * @param eventAlias event alias.
     * @return event conditions.
     */
    List<EventCondition> getEventConditions(String eventAlias);

    /**
     * Returns event conditions map.
     *
     * @return event conditions map.
     */
    Map<String, List<EventCondition>> getEventConditions();
}
