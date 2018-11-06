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

package org.openksavi.sponge.action;

import java.util.List;

import org.openksavi.sponge.ProcessorOperations;

/**
 * Action operations.
 */
public interface ActionOperations extends ProcessorOperations {

    /**
     * Returns the arguments metadata or {@code null} in not specified.
     *
     * @return the arguments metadata.
     */
    List<ArgMeta<?>> getArgsMeta();

    /**
     * Sets the (optional) arguments metadata.
     *
     * @param argsMeta the arguments metadata.
     */
    void setArgsMeta(List<ArgMeta<?>> argsMeta);

    /**
     * Returns the result metadata or {@code null} in not specified.
     *
     * @return the result metadata.
     */
    ResultMeta<?> getResultMeta();

    /**
     * Sets the (optional) result metadata.
     *
     * @param resultMeta the result metadata.
     */
    void setResultMeta(ResultMeta<?> resultMeta);
}
