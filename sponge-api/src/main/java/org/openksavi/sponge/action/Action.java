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

import java.util.Map;
import java.util.Set;

import org.openksavi.sponge.Processor;

/**
 * Action. Every action should implement at least one {@code onCall} method that performs an operation and returns a result. For example:
 * Object onCall(Number value, String text).
 */
public interface Action extends Processor<ActionAdapter>, ActionOperations {

    /**
     * A callback method that returns the provided argument values along with argument value sets (i.e. possible values of an argument).
     *
     * @param names the not null set of argument names that are to be provided.
     * @param current the not null map of argument names and their current values passed from a client code. The map is required to contain
     *        values of those arguments that the arguments specified in the {@code names} depend on.
     * @param provided the initially empty map of argument names and values (value sets) that is to be set up in this callback method.
     */
    void onProvideArgs(Set<String> names, Map<String, Object> current, Map<String, ArgValue<?>> provided);
}
