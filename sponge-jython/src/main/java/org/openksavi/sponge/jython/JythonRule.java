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

package org.openksavi.sponge.jython;

import java.util.function.Function;

import org.python.core.PyFunction;
import org.python.core.PyMethod;
import org.python.core.PyObject;

import org.openksavi.sponge.core.rule.CompositeEventCondition;
import org.openksavi.sponge.core.rule.ScriptKnowledgeBaseEventCondition;
import org.openksavi.sponge.jython.core.JythonFunctionEventCondition;
import org.openksavi.sponge.rule.EventCondition;

/**
 * Jython-specific implementation of the rule.
 */
public abstract class JythonRule extends org.openksavi.sponge.core.rule.BaseRule {

    private static final Function<? super PyObject, ? extends EventCondition> MAPPER = pyObject -> {
        if (pyObject instanceof PyMethod) {
            return new ScriptKnowledgeBaseEventCondition(((PyMethod) pyObject).__func__.__findattr__("__name__").toString());
        } else if (pyObject instanceof PyFunction) {
            return new JythonFunctionEventCondition((PyFunction) pyObject);
        } else {
            throw new IllegalArgumentException("Incorrect condition type: " + pyObject.getClass());
        }
    };

    public void setConditions(String eventAlias, PyObject... pyObjects) {
        setJavaConditions(eventAlias, new CompositeEventCondition(MAPPER, pyObjects));
    }

    public void addCondition(String eventAlias, PyObject pyObject) {
        addJavaCondition(eventAlias, MAPPER.apply(pyObject));
    }
}
