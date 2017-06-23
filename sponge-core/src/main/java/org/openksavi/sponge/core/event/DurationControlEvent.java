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

package org.openksavi.sponge.core.event;

import org.openksavi.sponge.EventSetProcessorAdapter;
import org.openksavi.sponge.EventSetProcessorAdapterGroup;
import org.openksavi.sponge.core.util.Utils;

/**
 * A control event for handling duration timeout in event set processors.
 */
@SuppressWarnings("rawtypes")
public class DurationControlEvent extends ProcessorControlEvent {

    private static final long serialVersionUID = 639657397395742783L;

    private EventSetProcessorAdapter eventSetProcessorAdapter;

    public DurationControlEvent(EventSetProcessorAdapter eventSetProcessorAdapter) {
        super(Utils.createControlEventName(DurationControlEvent.class), eventSetProcessorAdapter.getGroup());

        this.eventSetProcessorAdapter = eventSetProcessorAdapter;
    }

    public EventSetProcessorAdapterGroup getEventSetProcessorAdapterGroup() {
        return eventSetProcessorAdapter.getGroup();
    }

    public EventSetProcessorAdapter getEventSetProcessorAdapter() {
        return eventSetProcessorAdapter;
    }
}
