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

package org.openksavi.sponge.nashorn;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.Undefined;

import org.openksavi.sponge.core.action.BaseActionBuilder;

/**
 * JavaScript-specific implementation of an action builder.
 */
@SuppressWarnings("restriction")
public class NashornActionBuilder extends BaseActionBuilder {

    public NashornActionBuilder(String name) {
        super(name);
    }

    public NashornActionBuilder withOnCall(ScriptObjectMirror function) {
        return (NashornActionBuilder) super.withOnCallArgs((action, args) -> {
            Object result = function.call(null, Stream.concat(Stream.of(action), args.stream()).collect(Collectors.toList()).toArray());

            if (result instanceof Undefined) {
                return null;
            }

            return result;
        });
    }
}
