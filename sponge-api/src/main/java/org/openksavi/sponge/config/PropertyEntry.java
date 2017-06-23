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

package org.openksavi.sponge.config;

/**
 * Configuration property entry.
 */
public interface PropertyEntry {

    /**
     * Returns a property value.
     *
     * @return a property value.
     */
    Object getValue();

    /**
     * Returns {@code true} if this property is to be set as an engine scope variable.
     * 
     * @return {@code true} if this property is to be set as an engine scope variable.
     */
    boolean isVariable();

    /**
     * Returns {@code true} if this is a system property.
     * 
     * @return {@code true} if this is a system property.
     */
    boolean isSystem();
}
