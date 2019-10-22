/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.kotlin;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import kotlin.jvm.functions.Function2;
import kotlin.reflect.KFunction;

import org.openksavi.sponge.core.rule.BaseRule;
import org.openksavi.sponge.core.rule.BiFunctionEventCondition;
import org.openksavi.sponge.core.rule.CompositeEventCondition;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.kotlin.core.KotlinFunction2EventCondition;
import org.openksavi.sponge.kotlin.core.KotlinKnowledgeBaseEngineOperations;
import org.openksavi.sponge.kotlin.core.KotlinUtils;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.Rule;
import org.openksavi.sponge.rule.RuleEventSpec;

/**
 * Kotlin-specific implementation of the rule.
 */
@SuppressWarnings("unchecked")
public abstract class KRule extends BaseRule {

    private static final Function<? super Object, ? extends EventCondition> MAPPER = kotlinObject -> {
        if (kotlinObject instanceof Function2) {
            return new KotlinFunction2EventCondition((Function2<Rule, Event, Boolean>) kotlinObject);
        } else if (kotlinObject instanceof BiFunction) {
            return new BiFunctionEventCondition((BiFunction<Rule, Event, Boolean>) kotlinObject);
        } else {
            throw new IllegalArgumentException("Incorrect condition type: " + kotlinObject.getClass());
        }
    };

    @Override
    public final KRule withName(String name) {
        return (KRule) super.withName(name);
    }

    @Override
    public final KRule withLabel(String label) {
        return (KRule) super.withLabel(label);
    }

    @Override
    public final KRule withDescription(String description) {
        return (KRule) super.withDescription(description);
    }

    @Override
    public final KRule withVersion(Integer version) {
        return (KRule) super.withVersion(version);
    }

    @Override
    public final KRule withFeatures(Map<String, Object> features) {
        return (KRule) super.withFeatures(features);
    }

    @Override
    public final KRule withFeature(String name, Object value) {
        return (KRule) super.withFeature(name, value);
    }

    @Override
    public final KRule withCategory(String category) {
        return (KRule) super.withCategory(category);
    }

    @Override
    public final KRule withEvents(List<String> eventStringSpecs) {
        return (KRule) super.withEvents(eventStringSpecs);
    }

    public final KRule withEvents(String... eventStringSpecs) {
        return withEvents(Arrays.asList(eventStringSpecs));
    }

    @Override
    public final KRule withEvent(String eventStringSpec) {
        return (KRule) super.withEvent(eventStringSpec);
    }

    @Override
    public final KRule withDuration(Duration duration) {
        return (KRule) super.withDuration(duration);
    }

    @Override
    public final KRule withSynchronous(Boolean synchronous) {
        return (KRule) super.withSynchronous(synchronous);
    }

    @Override
    public final KRule withEventConditions(String eventAlias, List<EventCondition> conditions) {
        return (KRule) super.withEventConditions(eventAlias, conditions);
    }

    @Override
    public final KRule withEventCondition(String eventAlias, EventCondition condition) {
        return (KRule) super.withEventCondition(eventAlias, condition);
    }

    @Override
    public final KRule withAllEventCondition(EventCondition condition) {
        return (KRule) super.withAllEventCondition(condition);
    }

    @Override
    public final KRule withAllEventConditions(List<EventCondition> conditions) {
        return (KRule) super.withAllEventConditions(conditions);
    }

    @Override
    public final KRule withEventSpecs(List<RuleEventSpec> eventSpecs) {
        return (KRule) super.withEventSpecs(eventSpecs);
    }

    public final KRule withEventSpecs(RuleEventSpec... eventSpecs) {
        return withEventSpecs(Arrays.asList(eventSpecs));
    }

    @Override
    public final KRule withEventSpec(RuleEventSpec eventSpec) {
        return (KRule) super.withEventSpec(eventSpec);
    }

    @Override
    public final KRule withOrdered(boolean ordered) {
        return (KRule) super.withOrdered(ordered);
    }

    public final KRule withCondition(String eventAlias, KFunction<Boolean> kotlinObject) {
        return (KRule) super.withEventCondition(eventAlias,
                SpongeUtils.createRuleEventConditionForMethod(this, KotlinUtils.createEventConditionMethodName(kotlinObject)));
    }

    public final KRule withCondition(String eventAlias, String methodName) {
        return (KRule) super.withEventCondition(eventAlias, SpongeUtils.createRuleEventConditionForMethod(this, methodName));
    }

    public final KRule withCondition(String eventAlias, Function2<Rule, Event, Boolean> kotlinObject) {
        return (KRule) super.withEventCondition(eventAlias, MAPPER.apply(kotlinObject));
    }

    public final KRule withCondition(String eventAlias, BiFunction<Rule, Event, Boolean> kotlinObject) {
        return (KRule) super.withEventCondition(eventAlias, MAPPER.apply(kotlinObject));
    }

    public final KRule withConditions(String eventAlias, KFunction<Boolean>... kotlinObjects) {
        return (KRule) super.withEventCondition(eventAlias,
                SpongeUtils.createRuleEventConditionForMethods(this, KotlinUtils.createEventConditionMethodNames(kotlinObjects)));
    }

    public final KRule withConditions(String eventAlias, String... methodNames) {
        return (KRule) super.withEventCondition(eventAlias,
                SpongeUtils.createRuleEventConditionForMethods(this, Arrays.asList(methodNames)));
    }

    public final KRule withConditions(String eventAlias, Function2<Rule, Event, Boolean>... kotlinObjects) {
        return (KRule) super.withEventCondition(eventAlias, CompositeEventCondition.create(MAPPER, kotlinObjects));
    }

    public final KRule withConditions(String eventAlias, BiFunction<Rule, Event, Boolean>... kotlinObjects) {
        return (KRule) super.withEventCondition(eventAlias, CompositeEventCondition.create(MAPPER, kotlinObjects));
    }

    public final KRule withAllConditions(KFunction<Boolean>... kotlinObjects) {
        return (KRule) super.withAllEventCondition(
                SpongeUtils.createRuleEventConditionForMethods(this, KotlinUtils.createEventConditionMethodNames(kotlinObjects)));
    }

    public final KRule withAllConditions(String... methodNames) {
        return (KRule) super.withAllEventCondition(SpongeUtils.createRuleEventConditionForMethods(this, Arrays.asList(methodNames)));
    }

    public final KRule withAllConditions(Function2<Rule, Event, Boolean>... kotlinObjects) {
        return (KRule) super.withAllEventCondition(CompositeEventCondition.create(MAPPER, kotlinObjects));
    }

    public final KRule withAllConditions(BiFunction<Rule, Event, Boolean>... kotlinObjects) {
        return (KRule) super.withAllEventCondition(CompositeEventCondition.create(MAPPER, kotlinObjects));
    }

    public final KRule withAllCondition(KFunction<Boolean> kotlinObject) {
        return withAllConditions(kotlinObject);
    }

    public final KRule withAllCondition(String methodName) {
        return withAllConditions(methodName);
    }

    public final KRule withAllCondition(Function2<Rule, Event, Boolean> kotlinObject) {
        return withAllConditions(kotlinObject);
    }

    public final KRule withAllCondition(BiFunction<Rule, Event, Boolean> kotlinObject) {
        return withAllConditions(kotlinObject);
    }

    /**
     * Method required for accessing the engine operations in Kotlin-based processors.
     *
     * @return the engine operations.
     */
    @Override
    public final KotlinKnowledgeBaseEngineOperations getSponge() {
        return (KotlinKnowledgeBaseEngineOperations) super.getSponge();
    }
}
