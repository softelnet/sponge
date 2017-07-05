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

package org.openksavi.sponge.util;

/**
 * Represents an entity that can be managed.
 */
public interface Manageable {

    /**
     * Starts up this managed entity.
     */
    void startup();

    /**
     * Shuts down this managed entity.
     */
    void shutdown();

    /**
     * Informs whether this managed entity is new.
     *
     * @return {@code true} if this managed entity is new.
     */
    boolean isNew();

    /**
     * Informs whether this managed entity is starting.
     *
     * @return {@code true} if this managed entity is starting.
     */
    boolean isStarting();

    /**
     * Informs whether this managed entity is running.
     *
     * @return {@code true} if this managed entity is running.
     */
    boolean isRunning();

    /**
     * Informs whether this managed entity is stopping.
     *
     * @return {@code true} if this managed entity is stopping.
     */
    boolean isStopping();

    /**
     * Informs whether this managed entity is terminated.
     *
     * @return {@code true} if this managed entity is terminated.
     */
    boolean isTerminated();

    /**
     * Informs whether this managed entity failed.
     *
     * @return {@code true} if this managed entity failed.
     */
    boolean isFailed();
}
