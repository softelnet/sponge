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

package org.openksavi.sponge.core.correlator;

import java.util.concurrent.locks.Lock;

import org.openksavi.sponge.core.BaseEventSetProcessor;
import org.openksavi.sponge.correlator.Correlator;
import org.openksavi.sponge.correlator.CorrelatorAdapter;
import org.openksavi.sponge.event.Event;

public abstract class BaseCorrelator extends BaseEventSetProcessor<CorrelatorAdapter> implements Correlator {

    @Override
    public final void setMaxInstances(int maxInstances) {
        getAdapter().setMaxInstances(maxInstances);
    }

    @Override
    public final int getMaxInstances() {
        return getAdapter().getMaxInstances();
    }

    @Override
    public final boolean isInstanceSynchronous() {
        return getAdapter().isInstanceSynchronous();
    }

    @Override
    public final void setInstanceSynchronous(boolean instanceSynchronous) {
        getAdapter().setInstanceSynchronous(instanceSynchronous);
    }

    @Override
    public final void finish() {
        getAdapter().finish();
    }

    @Override
    public boolean onAcceptAsFirst(Event event) {
        return true;
    }

    @Override
    public void onDuration() {
        //
    }

    @Override
    public final CorrelatorAdapter createAdapter() {
        return new BaseCorrelatorAdapter(new BaseCorrelatorDefinition());
    }

    /**
     * Returns the lock that can be used to synchronize threads when the instance synchronous flag is set to {@code false}.
     *
     * @return the lock.
     */
    public final Lock getLock() {
        return ((BaseCorrelatorAdapter) getAdapter()).getLock();
    }
}
