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

package org.openksavi.sponge.util;

/**
 * Represents an entity that has a name, a label and a description.
 */
public interface Descriptive {

    /**
     * Returns the name name.
     *
     * @return the name.
     */
    String getName();

    /**
     * Sets the name.
     *
     * @param name the name.
     */
    void setName(String name);

    /**
     * Sets the label.
     *
     * @param label the label.
     */
    void setLabel(String label);

    /**
     * Returns the label.
     *
     * @return the label.
     */
    String getLabel();

    /**
     * Sets the description.
     *
     * @param description the description.
     */
    void setDescription(String description);

    /**
     * Returns the description.
     *
     * @return the description.
     */
    String getDescription();
}
