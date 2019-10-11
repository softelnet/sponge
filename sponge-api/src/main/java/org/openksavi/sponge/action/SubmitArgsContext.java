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

/**
 * A submitted action arguments context.
 */
public class SubmitArgsContext {

    /** The not null set of submitted argument names. */
    private Set<String> names;

    /**
     * The not null map of argument names and their current values passed from a client code. The map is required to contain values of
     * {@code names} and those arguments that the arguments specified in the {@code names} depend on and submitted arguments.
     */
    private Map<String, Object> current;

    public SubmitArgsContext(Set<String> names, Map<String, Object> current) {
        this.names = names;
        this.current = current;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }

    public Map<String, Object> getCurrent() {
        return current;
    }

    public void setCurrent(Map<String, Object> current) {
        this.current = current;
    }
}
