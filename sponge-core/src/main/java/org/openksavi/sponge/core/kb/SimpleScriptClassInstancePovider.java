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

package org.openksavi.sponge.core.kb;

import java.util.function.Function;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.Engine;

/**
 * The simple provider of script-based class instances.
 *
 * @param <T> the type of the instance to provide.
 */
public class SimpleScriptClassInstancePovider<T> implements ScriptClassInstanceProvider<T> {

    private Function<String, T> createInstanceFunction;

    public SimpleScriptClassInstancePovider(Engine engine, Function<String, T> createInstanceFunction) {
        this.createInstanceFunction = createInstanceFunction;
    }

    @Override
    public T newInstance(String className, Class<T> javaClass) {
        try {
            return createInstanceFunction.apply(className);
        } catch (Exception e) {
            throw SpongeUtils.wrapException(getClass().getSimpleName(), e);
        }
    }

    @Override
    public void clear() {
        //
    }
}
