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

package org.openksavi.sponge.core.trigger;

import org.openksavi.sponge.core.BaseEventProcessor;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.trigger.Trigger;
import org.openksavi.sponge.trigger.TriggerAdapter;

public abstract class BaseTrigger extends BaseEventProcessor<TriggerAdapter> implements Trigger {

    /**
     * Informs whether the specified event is to be accepted.
     *
     * @param event
     *            event.
     * @return whether the specified event is to be accepted.
     */
    @Override
    public boolean accepts(Event event) {
        return true;
    }

    @Override
    public final TriggerAdapter createAdapter() {
        return new BaseTriggerAdapter(new BaseTriggerDefinition());
    }
}
