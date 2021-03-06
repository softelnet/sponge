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

package org.openksavi.sponge.core.trigger;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.core.BaseEventProcessor;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.trigger.Trigger;
import org.openksavi.sponge.trigger.TriggerAdapter;

public abstract class BaseTrigger extends BaseEventProcessor<TriggerAdapter> implements Trigger {

    @Override
    public BaseTriggerMeta getMeta() {
        return (BaseTriggerMeta) super.getMeta();
    }

    @Override
    public boolean onAccept(Event event) {
        return true;
    }

    @Override
    public TriggerAdapter createAdapter() {
        return new BaseTriggerAdapter(new BaseTriggerDefinition());
    }

    @Override
    public BaseTrigger withName(String name) {
        return (BaseTrigger) super.withName(name);
    }

    @Override
    public BaseTrigger withLabel(String label) {
        return (BaseTrigger) super.withLabel(label);
    }

    @Override
    public BaseTrigger withDescription(String description) {
        return (BaseTrigger) super.withDescription(description);
    }

    @Override
    public BaseTrigger withVersion(Integer version) {
        return (BaseTrigger) super.withVersion(version);
    }

    @Override
    public BaseTrigger withFeatures(Map<String, Object> features) {
        return (BaseTrigger) super.withFeatures(features);
    }

    @Override
    public BaseTrigger withFeature(String name, Object value) {
        return (BaseTrigger) super.withFeature(name, value);
    }

    @Override
    public BaseTrigger withCategory(String category) {
        return (BaseTrigger) super.withCategory(category);
    }

    @Override
    public BaseTrigger withEvents(List<String> eventNames) {
        return (BaseTrigger) super.withEvents(eventNames);
    }

    @Override
    public BaseTrigger withEvent(String eventName) {
        return (BaseTrigger) super.withEvent(eventName);
    }
}
