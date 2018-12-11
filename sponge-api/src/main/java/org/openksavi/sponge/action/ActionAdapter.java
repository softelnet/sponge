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

package org.openksavi.sponge.action;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.ProcessorAdapter;

/**
 * Action adapter.
 *
 */
public interface ActionAdapter extends ProcessorAdapter<Action>, ActionOperations {

    /**
     * Returns the argument metadata. Throws exception if not found.
     *
     * @param name the argument name.
     * @return the argument metadata.
     */
    ArgMeta<?> getArgMeta(String name);

    /**
     * Returns the provided values along with value sets of the action arguments.
     *
     * @param names the list of argument names.
     * @param current the map of argument names and their current values passed from a client code.
     * @return the map of argument names and values (value sets).
     */
    Map<String, ArgValue<?>> provideArgs(List<String> names, Map<String, Object> current);
}
