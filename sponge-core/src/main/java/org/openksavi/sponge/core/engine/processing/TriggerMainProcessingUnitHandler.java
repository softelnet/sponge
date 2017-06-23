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

package org.openksavi.sponge.core.engine.processing;

import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.processing.MainProcessingUnitHandler;
import org.openksavi.sponge.event.ControlEvent;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.trigger.TriggerAdapter;

public class TriggerMainProcessingUnitHandler extends BaseMainProcessingUnitHandler implements MainProcessingUnitHandler {

    public TriggerMainProcessingUnitHandler(BaseMainProcessingUnit processingUnit) {
        super(ProcessorType.TRIGGER, processingUnit);
    }

    @Override
    public void processEvent(ProcessorAdapter<?> adapter, Event event) {
        if (event instanceof ControlEvent) {
            return;
        }

        TriggerAdapter triggerAdapter = (TriggerAdapter) adapter;
        try {
            if (triggerAdapter.getProcessor().accepts(event)) {
                triggerAdapter.getProcessor().run(event);
            }
        } catch (Throwable e) {
            getProcessingUnit().getEngine().handleError(adapter, e);
        }
    }
}
