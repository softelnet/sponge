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

package org.openksavi.sponge.correlator;

import org.openksavi.sponge.EventSetProcessorMeta;

/**
 * A correlator metadata.
 */
public interface CorrelatorMeta extends EventSetProcessorMeta {

    /**
     * Sets the maximum number of concurrent instances allowed for this correlator.
     *
     * @param maxInstances the maximum number of concurrent instances allowed for this correlator.
     */
    void setMaxInstances(int maxInstances);

    /**
     * Returns the maximum number of concurrent instances allowed for this correlator.
     *
     * @return the maximum number of concurrent instances allowed for this correlator.
     */
    int getMaxInstances();

    /**
     * Sets the instance synchronous flag.
     *
     * @param instanceSynchronous the instance synchronous flag.
     */
    void setInstanceSynchronous(boolean instanceSynchronous);

    /**
     * Returns the instance synchronous flag. If {@code true} (the default value), one instance of the correlator will process only one
     * event at a time. If {@code false}, one instance of the correlator will process many events concurrently. In that case the correlator
     * has to be thread safe.
     *
     * @return the instance synchronous flag.
     */
    boolean isInstanceSynchronous();
}
