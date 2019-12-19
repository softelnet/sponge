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

import org.openksavi.sponge.Processor;

/**
 * Action. Every action should implement at least one {@code onCall} method that performs an operation and returns a result. For example:
 * Object onCall(Number value, String text).
 */
public interface Action extends Processor<ActionAdapter>, ActionOperations {

    /**
     * A callback method that informs if the action in a given context is active.
     *
     * @param context the context.
     * @return {@code true} if the action is active.
     */
    boolean onIsActive(IsActionActiveContext context);

    /**
     * A callback method that provides argument values along with argument value sets (i.e. possible values of an argument) and/or submits
     * arguments.
     *
     * @param context the provided action arguments context. The {@code context.provided} map should be set up with the provided values if
     *        necessary.
     */
    void onProvideArgs(ProvideArgsContext context);
}
