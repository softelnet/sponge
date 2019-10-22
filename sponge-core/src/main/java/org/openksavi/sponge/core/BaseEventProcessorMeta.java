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

package org.openksavi.sponge.core;

import java.util.ArrayList;
import java.util.List;

import org.openksavi.sponge.EventProcessorMeta;

/**
 * A base event processor metadata.
 */
public class BaseEventProcessorMeta extends BaseProcessorMeta implements EventProcessorMeta {

    /** Registered event names. */
    private List<String> eventNames;

    /**
     * Returns event names for which this processor definition is registered.
     *
     * @return event names.
     */
    @Override
    public List<String> getEventNames() {
        return eventNames;
    }

    /**
     * Sets event names for which this processor definition is registered.
     *
     * @param eventNames event names.
     */
    @Override
    public void setEventNames(List<String> eventNames) {
        this.eventNames = eventNames != null ? new ArrayList<>(eventNames) : null;
    }

    @Override
    public void addEventNames(List<String> eventNames) {
        if (this.eventNames != null) {
            this.eventNames.addAll(eventNames);
        } else {
            setEventNames(eventNames);
        }
    }

    @Override
    public String getEventName(int index) {
        return eventNames.get(index);
    }

    @Override
    public void update(BaseProcessorMeta source) {
        super.update(source);

        if (source instanceof BaseEventProcessorMeta) {
            BaseEventProcessorMeta sourceMeta = (BaseEventProcessorMeta) source;
            setEventNames(sourceMeta.getEventNames());
        }
    }
}
