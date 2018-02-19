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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.MethodUtils;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.rule.Rule;

/**
 * Reflection rule event condition.
 */
public class ReflectionEventCondition extends MethodNameEventCondition {

    /** Method reference. */
    private Method method;

    /**
     * Creates a new reflection rule event condition.
     *
     * @param method Java-based rule class method.
     */
    protected ReflectionEventCondition(Method method) {
        super(method.getName());

        this.method = method;
    }

    /**
     * Checks rule event condition by invoking the defined Java rule class method.
     *
     * @param rule rule.
     * @param event event.
     * @return {@code true} if this condition is met.
     */
    @Override
    public synchronized boolean condition(Rule rule, Event event) {
        try {
            Object result = method.invoke(rule, new Object[] { event });
            if (!(result instanceof Boolean)) {
                throw new IllegalArgumentException("Condition method must return a boolean value");
            }
            return (Boolean) result;
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw new SpongeException(e.getCause());
            } else {
                throw new SpongeException(e);
            }
        } catch (IllegalAccessException e) {
            throw new SpongeException(e);
        }
    }

    public static ReflectionEventCondition create(Rule rule, String methodName) {
        return new ReflectionEventCondition(resolveMethod(rule, methodName));
    }

    public static Method resolveMethod(Rule rule, String methodName) {
        Method method = MethodUtils.getMatchingMethod(rule.getClass(), methodName, Event.class);

        if (method == null) {
            throw new IllegalArgumentException("Event condition method " + methodName + " not found in rule " + rule.getName());
        }

        return method;
    }
}
