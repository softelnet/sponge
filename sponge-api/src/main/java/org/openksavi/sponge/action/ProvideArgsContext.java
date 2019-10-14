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

package org.openksavi.sponge.action;

import java.util.Map;
import java.util.Set;

import org.openksavi.sponge.type.provided.ProvidedValue;

/**
 * A provided action arguments context.
 */
public class ProvideArgsContext {

    /** The not null set of argument names that are to be provided (i.e. read). */
    private Set<String> provide;

    /** The not null set of argument names that are to be submitted (i.e. written). */
    private Set<String> submit;

    /**
     * The not null map of argument names and their current values passed from a client code. The map is required to contain values of those
     * arguments that the arguments specified in the {@code names} depend on and submitted arguments.
     */
    private Map<String, Object> current;

    /**
     * The initially empty map of argument names and values (value sets) that is to be set up with provided values.
     */
    private Map<String, ProvidedValue<?>> provided;

    public ProvideArgsContext(Set<String> provide, Set<String> submit, Map<String, Object> current,
            Map<String, ProvidedValue<?>> provided) {
        this.provide = provide;
        this.submit = submit;
        this.current = current;
        this.provided = provided;
    }

    public Set<String> getProvide() {
        return provide;
    }

    public void setProvide(Set<String> provide) {
        this.provide = provide;
    }

    public Set<String> getSubmit() {
        return submit;
    }

    public void setSubmit(Set<String> submit) {
        this.submit = submit;
    }

    public Map<String, Object> getCurrent() {
        return current;
    }

    public void setCurrent(Map<String, Object> current) {
        this.current = current;
    }

    public Map<String, ProvidedValue<?>> getProvided() {
        return provided;
    }

    public void setProvided(Map<String, ProvidedValue<?>> provided) {
        this.provided = provided;
    }
}
