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

import java.util.function.BiFunction;
import java.util.function.Function;

import kotlin.jvm.functions.Function2;
import kotlin.reflect.KFunction;

import org.openksavi.sponge.core.rule.BaseRule;
import org.openksavi.sponge.core.rule.BiFunctionEventCondition;
import org.openksavi.sponge.core.rule.CompositeEventCondition;
import org.openksavi.sponge.core.rule.ReflectionEventCondition;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.kotlin.core.KotlinFunction2EventCondition;
import org.openksavi.sponge.kotlin.core.KotlinKnowledgeBaseEngineOperations;
import org.openksavi.sponge.rule.EventCondition;
import org.openksavi.sponge.rule.Rule;

/**
 * Kotlin-specific implementation of the rule.
 */
public abstract class KRule extends BaseRule {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final Function<? super Object, ? extends EventCondition> MAPPER = kotlinObject -> {
        if (kotlinObject instanceof KFunction) {
            return new ReflectionEventCondition(((KFunction) kotlinObject).getName());
        } else if (kotlinObject instanceof Function2) {
            return new KotlinFunction2EventCondition((Function2<Rule, Event, Boolean>) kotlinObject);
        } else if (kotlinObject instanceof BiFunction) {
            return new BiFunctionEventCondition((BiFunction<Rule, Event, Boolean>) kotlinObject);
        } else if (kotlinObject instanceof String) {
            return new ReflectionEventCondition((String) kotlinObject);
        } else {
            throw new IllegalArgumentException("Incorrect condition type: " + kotlinObject.getClass());
        }
    };

    public void addConditions(String eventAlias, Object... kotlinObjects) {
        addJavaConditions(eventAlias, new CompositeEventCondition(MAPPER, kotlinObjects));
    }

    public void addAllConditions(Object... kotlinObjects) {
        addAllJavaConditions(new CompositeEventCondition(MAPPER, kotlinObjects));
    }

    public void addCondition(String eventAlias, Object kotlinObject) {
        addJavaCondition(eventAlias, MAPPER.apply(kotlinObject));
    }

    /**
     * Method required for accessing EPS in Kotlin-based processors.
     *
     * @return EPS.
     */
    @Override
    public final KotlinKnowledgeBaseEngineOperations getEps() {
        return (KotlinKnowledgeBaseEngineOperations) super.getEps();
    }
}
