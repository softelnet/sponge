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

package org.openksavi.sponge.engine;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * A session.
 *
 */
public interface Session extends Serializable {

    /**
     * Sets session variable value.
     *
     * @param name variable name.
     * @param value variable value.
     */
    void setVariable(String name, Object value);

    /**
     * Returns the value of the session variable. Throws exception if not found.
     *
     * @param name variable name.
     * @return variable value.
     * @param <T> variable.
     */
    <T> T getVariable(String name);

    /**
     * Returns the value of the session variable. Throws exception if not found.
     *
     * @param cls variable class.
     * @param name variable name.
     *
     * @return variable value.
     * @param <T> variable.
     */
    <T> T getVariable(Class<T> cls, String name);

    /**
     * Returns the value of the session variable or {@code defaultValue} if not found.
     *
     * @param name variable name.
     * @param defaultValue default value.
     *
     * @return variable value.
     * @param <T> variable.
     */
    <T> T getVariable(String name, T defaultValue);

    /**
     * Returns the value of the session variable or {@code defaultValue} if not found.
     *
     * @param cls variable class.
     * @param name variable name.
     * @param defaultValue default value.
     *
     * @return variable value.
     * @param <T> variable.
     */
    <T> T getVariable(Class<T> cls, String name, T defaultValue);

    /**
     * Removes a session variable.
     *
     * @param name variable name.
     */
    void removeVariable(String name);

    /**
     * Returns {@code true} if a session variable named {@code name} is defined.
     *
     * @param name variable name.
     * @return {@code true} if a session variable named {@code name} is defined.
     */
    boolean hasVariable(String name);

    /**
     * Sets the session variable if not set already.
     *
     * @param name variable name.
     * @param supplier variable value supplier.
     * @param <T> variable.
     */
    <T> void setVariableIfNone(String name, Supplier<T> supplier);

    /**
     * Updates the session variable.
     *
     * @param name variable name.
     */
    void updateVariable(String name);
}
