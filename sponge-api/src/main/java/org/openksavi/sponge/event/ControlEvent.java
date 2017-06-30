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

package org.openksavi.sponge.event;

import org.openksavi.sponge.Experimental;

/**
 * Control event. I may have a priority other than default.
 */
@Experimental
public interface ControlEvent extends Event {

    /**
     * Sets the priority. Greater value means greater priority.
     *
     * @param priority the priority.
     * @return the holder of the priority for fluent API.
     */
    ControlEvent setPriority(int priority);
}
