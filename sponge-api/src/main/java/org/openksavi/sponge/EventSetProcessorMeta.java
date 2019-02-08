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

package org.openksavi.sponge;

import java.time.Duration;

/**
 * An event set processor metadata.
 */
public interface EventSetProcessorMeta extends EventProcessorMeta {

    /**
     * Sets a duration.
     *
     * @param duration a duration.
     */
    void setDuration(Duration duration);

    /**
     * Returns a duration.
     *
     * @return a duration.
     */
    Duration getDuration();

    /**
     * Informs whether this event set processor has a duration.
     *
     * @return {@code true} if this event set processor has duration.
     */
    boolean hasDuration();

    /**
     * Returns {@code true} if this event set processor is synchronous.
     *
     * @return {@code true} if this event set processor is synchronous. {@code null} value means that the default value should be used.
     */
    Boolean isSynchronous();

    /**
     * Sets synchronous flag.
     *
     * @param synchronous synchronous flag.
     */
    void setSynchronous(Boolean synchronous);
}
