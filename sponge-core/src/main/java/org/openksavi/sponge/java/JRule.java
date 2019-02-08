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

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.core.rule.BaseRule;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.RuleEventSpec;

/**
 * Java-specific implementation of the rule. As a condition supports a method name or a direct instance of EventCondition.
 */
public abstract class JRule extends BaseRule {

    @Override
    public final JRule withName(String name) {
        return (JRule) super.withName(name);
    }

    @Override
    public final JRule withLabel(String label) {
        return (JRule) super.withLabel(label);
    }

    @Override
    public final JRule withDescription(String description) {
        return (JRule) super.withDescription(description);
    }

    @Override
    public final JRule withVersion(Integer version) {
        return (JRule) super.withVersion(version);
    }

    @Override
    public final JRule withFeatures(Map<String, Object> features) {
        return (JRule) super.withFeatures(features);
    }

    @Override
    public final JRule withFeature(String name, Object value) {
        return (JRule) super.withFeature(name, value);
    }

    @Override
    public final JRule withCategory(String category) {
        return (JRule) super.withCategory(category);
    }

    @Override
    public final JRule withEvents(List<String> eventStringSpecs) {
        return (JRule) super.withEvents(eventStringSpecs);
    }

    public final JRule withEvents(String... eventStringSpecs) {
        return withEvents(Arrays.asList(eventStringSpecs));
    }

    @Override
    public final JRule withEvent(String eventStringSpec) {
        return (JRule) super.withEvent(eventStringSpec);
    }

    @Override
    public final JRule withDuration(Duration duration) {
        return (JRule) super.withDuration(duration);
    }

    @Override
    public final JRule withSynchronous(Boolean synchronous) {
        return (JRule) super.withSynchronous(synchronous);
    }

    @Override
    public final JRule withEventConditions(String eventAlias, List<EventCondition> conditions) {
        return (JRule) super.withEventConditions(eventAlias, conditions);
    }

    @Override
    public final JRule withEventCondition(String eventAlias, EventCondition condition) {
        return (JRule) super.withEventCondition(eventAlias, condition);
    }

    @Override
    public final JRule withAllEventCondition(EventCondition condition) {
        return (JRule) super.withAllEventCondition(condition);
    }

    @Override
    public final JRule withAllEventConditions(List<EventCondition> conditions) {
        return (JRule) super.withAllEventConditions(conditions);
    }

    @Override
    public final JRule withEventSpecs(List<RuleEventSpec> eventSpecs) {
        return (JRule) super.withEventSpecs(eventSpecs);
    }

    public final JRule withEventSpecs(RuleEventSpec... eventSpecs) {
        return withEventSpecs(Arrays.asList(eventSpecs));
    }

    @Override
    public final JRule withEventSpec(RuleEventSpec eventSpec) {
        return (JRule) super.withEventSpec(eventSpec);
    }

    @Override
    public final JRule withOrdered(boolean ordered) {
        return (JRule) super.withOrdered(ordered);
    }

    /**
     * Adds an event condition.
     *
     * @param eventAlias an event alias.
     * @param condition an event condition.
     */
    public final JRule withCondition(String eventAlias, EventCondition condition) {
        return withEventCondition(eventAlias, condition);
    }

    /**
     * Adds an event condition.
     *
     * @param eventAlias an event alias.
     * @param methodName an event condition method name.
     */
    public final JRule withCondition(String eventAlias, String methodName) {
        return withConditions(eventAlias, methodName);
    }

    /**
     * Adds event conditions.
     *
     * @param eventAlias event alias.
     * @param conditions event conditions.
     */
    public final JRule withConditions(String eventAlias, List<EventCondition> conditions) {
        return withEventConditions(eventAlias, conditions);
    }

    /**
     * Adds event conditions.
     *
     * @param eventAlias event alias.
     * @param conditions event conditions.
     */
    public final JRule withConditions(String eventAlias, EventCondition... conditions) {
        return withEventConditions(eventAlias, Arrays.asList(conditions));
    }

    public final JRule withConditions(String eventAlias, String... methodNames) {
        return withEventCondition(eventAlias, createEventConditionForMethods(Arrays.asList(methodNames)));
    }

    /**
     * Adds event conditions for all events.
     *
     * @param conditions event conditions.
     */
    public final JRule withAllConditions(List<EventCondition> conditions) {
        return withAllEventConditions(conditions);
    }

    public final JRule withAllConditions(EventCondition... conditions) {
        return withAllEventConditions(Arrays.asList(conditions));
    }

    public final JRule withAllConditions(String... methodNames) {
        return withAllEventCondition(createEventConditionForMethods(Arrays.asList(methodNames)));
    }

    /**
     * Adds event condition for all events.
     *
     * @param condition the event condition.
     */
    public final JRule withAllCondition(EventCondition condition) {
        return withAllConditions(Arrays.asList(condition));
    }

    /**
     * Adds event condition for all events.
     *
     * @param methodName the event condition method name.
     */
    public final JRule withAllCondition(String methodName) {
        return withAllConditions(methodName);
    }
}
