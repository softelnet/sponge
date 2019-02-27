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

import java.util.List;

import org.openksavi.sponge.ProcessorMeta;
import org.openksavi.sponge.type.DataType;

/**
 * An action metadata.
 */
@SuppressWarnings("rawtypes")
public interface ActionMeta extends ProcessorMeta {

    /**
     * Returns the argument types or {@code null} in not specified.
     *
     * @return the argument types.
     */
    List<DataType> getArgs();

    /**
     * Sets the (optional) argument types.
     *
     * @param args the argument types.
     */
    void setArgs(List<DataType> args);

    /**
     * Adds the (optional) argument type.
     *
     * @param args the argument type.
     */
    void addArgs(List<DataType> args);

    /**
     * Returns the argument type. Throws exception if not found. Supports sub-arguments specified using the dot notation.
     *
     * @param name the argument name.
     * @return the argument type.
     */
    DataType getArg(String name);

    /**
     * Returns the result type or {@code null} in not specified.
     *
     * @return the result type.
     */
    DataType getResult();

    /**
     * Sets the (optional) result type.
     *
     * @param result the result type.
     */
    void setResult(DataType result);

    /**
     * Tells if this action is callable. A callable action must have an {@code onCall} method defined. Defaults to {@code true}.
     *
     * @return {@code true} if this action is callable.
     */
    boolean isCallable();

    /**
     * Sets the callable flag.
     *
     * @param callable the callable flag.
     */
    void setCallable(boolean callable);
}
