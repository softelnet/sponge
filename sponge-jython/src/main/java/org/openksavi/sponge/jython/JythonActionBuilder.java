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

package org.openksavi.sponge.jython;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.python.core.Py;
import org.python.core.PyFunction;
import org.python.core.PyObject;

import org.openksavi.sponge.core.action.BaseActionBuilder;

/**
 * Jython-specific implementation of an action builder.
 */
public class JythonActionBuilder extends BaseActionBuilder {

    public JythonActionBuilder(String name) {
        super(name);
    }

    public JythonActionBuilder withOnCall(PyFunction pyFunction) {
        return (JythonActionBuilder) super.withOnCallArgs((action, args) -> {
            PyObject pyObject = pyFunction._jcall(Stream.concat(Stream.of(action), args.stream()).collect(Collectors.toList()).toArray());
            return Py.tojava(pyObject, Object.class);
        });
    }
}
