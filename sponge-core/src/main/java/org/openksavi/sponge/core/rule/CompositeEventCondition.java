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

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.Rule;

/**
 * Composite event condition.
 */
public class CompositeEventCondition implements EventCondition {

    /** Condition list. */
    private List<EventCondition> conditions;

    public CompositeEventCondition(EventCondition... conditions) {
        this.conditions = Lists.newArrayList(conditions);
    }

    public CompositeEventCondition(List<EventCondition> conditions) {
        this.conditions = Lists.newArrayList(conditions);
    }

    @Override
    public boolean condition(Rule rule, Event event) {
        for (EventCondition condition : conditions) {
            if (!condition.condition(rule, event)) {
                return false;
            }
        }

        return true;
    }

    @SafeVarargs
    public static <T> CompositeEventCondition create(Function<? super T, ? extends EventCondition> mapper, T... conditionTemplates) {
        return create(mapper, Arrays.asList(conditionTemplates));
    }

    public static <T> CompositeEventCondition create(Function<? super T, ? extends EventCondition> mapper, List<T> conditionTemplates) {
        return new CompositeEventCondition(conditionTemplates.stream().map(mapper).collect(Collectors.toList()));
    }
}
