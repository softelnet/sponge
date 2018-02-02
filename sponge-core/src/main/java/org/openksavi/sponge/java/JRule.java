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

package org.openksavi.sponge.java;

import java.util.Arrays;

import org.openksavi.sponge.core.rule.BaseRule;
import org.openksavi.sponge.rule.EventCondition;

/**
 * Java-specific implementation of the rule. As a condition supports a method name or a direct instance of EventCondition.
 */
public abstract class JRule extends BaseRule {

    /**
     * Adds event conditions.
     *
     * @param eventAlias event alias.
     * @param conditions event conditions.
     */
    public void addConditions(String eventAlias, EventCondition... conditions) {
        addEventConditions(eventAlias, conditions);
    }

    /**
     * Adds event conditions for all events.
     *
     * @param conditions event conditions.
     */
    public void addAllConditions(EventCondition... conditions) {
        addAllEventConditions(conditions);
    }

    /**
     * Adds an event condition.
     *
     * @param eventAlias an event alias.
     * @param condition an event condition.
     */
    public void addCondition(String eventAlias, EventCondition condition) {
        addEventCondition(eventAlias, condition);
    }

    /**
     * Adds event conditions.
     *
     * @param eventAlias event alias.
     * @param methodNames event condition method names.
     */
    public void addConditions(String eventAlias, String... methodNames) {
        addEventConditions(eventAlias, createEventConditionForMethods(Arrays.asList(methodNames)));
    }

    /**
     * Adds event conditions for all events.
     *
     * @param methodNames event condition method names.
     */
    public void addAllConditions(String... methodNames) {
        addAllEventConditions(createEventConditionForMethods(Arrays.asList(methodNames)));
    }

    /**
     * Adds an event condition.
     *
     * @param eventAlias an event alias.
     * @param methodName an event condition method name.
     */
    public void addCondition(String eventAlias, String methodName) {
        addEventCondition(eventAlias, createEventConditionForMethod(methodName));
    }
}
