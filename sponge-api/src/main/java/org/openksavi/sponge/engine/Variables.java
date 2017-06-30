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

package org.openksavi.sponge.engine;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Variables.
 */
public interface Variables extends Serializable {

    /**
     * Sets the variable.
     *
     * @param name variable name.
     * @param value variable value.
     */
    void set(String name, Object value);

    /**
     * Returns the value of the variable. Throws exception if not found.
     *
     * @param name variable name.
     * @return variable value.
     */
    Object get(String name);

    /**
     * Returns the value of the variable. Throws exception if not found.
     *
     * @param cls variable class.
     * @param name variable name.
     *
     * @return variable value.
     * @param <T> variable.
     */
    <T> T get(Class<T> cls, String name);

    /**
     * Returns the value of the variable or {@code defaultValue} if not found.
     *
     * @param name variable name.
     * @param defaultValue default value.
     *
     * @return variable value.
     * @param <T> variable.
     */
    <T> T get(String name, T defaultValue);

    /**
     * Returns the value of the variable or {@code defaultValue} if not found.
     *
     * @param cls variable class.
     * @param name variable name.
     * @param defaultValue default value.
     *
     * @return variable value.
     * @param <T> variable.
     */
    <T> T get(Class<T> cls, String name, T defaultValue);

    /**
     * Removes the variable.
     *
     * @param name variable name.
     */
    void remove(String name);

    /**
     * Returns {@code true} if the variable exists.
     *
     * @param name variable name.
     * @return variable value.
     */
    boolean exists(String name);

    /**
     * Sets the variable if there is none (i.e. doesn't exist).
     *
     * @param name variable name.
     * @param supplier variable value supplier.
     * @param <T> variable.
     */
    <T> void setIfNone(String name, Supplier<T> supplier);
}
