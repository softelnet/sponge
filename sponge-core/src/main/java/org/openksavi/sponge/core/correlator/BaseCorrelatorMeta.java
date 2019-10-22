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

package org.openksavi.sponge.core.correlator;

import org.openksavi.sponge.core.BaseEventSetProcessorMeta;
import org.openksavi.sponge.core.BaseProcessorMeta;
import org.openksavi.sponge.correlator.CorrelatorMeta;

/**
 * A base correlator metadata.
 */
public class BaseCorrelatorMeta extends BaseEventSetProcessorMeta implements CorrelatorMeta {

    public static final int UNBOUND_MAX_INSTANCES = -1;

    private int maxInstances = UNBOUND_MAX_INSTANCES;

    private boolean instanceSynchronous = true;

    @Override
    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }

    @Override
    public int getMaxInstances() {
        return maxInstances;
    }

    public boolean isMaxInstancesUnbound() {
        return maxInstances == UNBOUND_MAX_INSTANCES;
    }

    @Override
    public boolean isInstanceSynchronous() {
        return instanceSynchronous;
    }

    @Override
    public void setInstanceSynchronous(boolean instanceSynchronous) {
        this.instanceSynchronous = instanceSynchronous;
    }

    @Override
    public void update(BaseProcessorMeta source) {
        super.update(source);

        if (source instanceof BaseCorrelatorMeta) {
            BaseCorrelatorMeta sourceMeta = (BaseCorrelatorMeta) source;
            setMaxInstances(sourceMeta.getMaxInstances());
            setInstanceSynchronous(sourceMeta.isInstanceSynchronous());
        }
    }
}
