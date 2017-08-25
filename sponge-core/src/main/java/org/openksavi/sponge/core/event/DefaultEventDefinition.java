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

package org.openksavi.sponge.core.event;

import java.time.Duration;
import java.time.Instant;

import org.openksavi.sponge.EngineOperations;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;
import org.openksavi.sponge.event.EventDefinition;
import org.openksavi.sponge.event.EventDefinitionModifier;
import org.openksavi.sponge.event.EventSchedulerEntry;

public class DefaultEventDefinition implements EventDefinition {

    private EngineOperations engineOperations;

    private Event event;

    /**
     * Creates a new event definition.
     *
     * @param engineOperations an engine operations.
     * @param name an event name.
     * @param clonePolicy an event clone policy.
     */
    public DefaultEventDefinition(EngineOperations engineOperations, String name, EventClonePolicy clonePolicy) {
        this.engineOperations = engineOperations;
        event = this.engineOperations.makeEvent(name, clonePolicy);
    }

    /**
     * Creates a new event definition.
     *
     * @param engineOperations an engine operations.
     * @param event an event.
     */
    public DefaultEventDefinition(EngineOperations engineOperations, Event event) {
        this.engineOperations = engineOperations;
        this.event = event;
    }

    @Override
    public DefaultEventDefinition set(String name, Object value) {
        event.set(name, value);
        return this;
    }

    @Override
    public DefaultEventDefinition modify(EventDefinitionModifier modifier) {
        modifier.modify(event);
        return this;
    }

    @Override
    public Event send() {
        return engineOperations.send(event);
    }

    @Override
    public EventSchedulerEntry sendAfter(long delay) {
        return engineOperations.sendAfter(event, delay);
    }

    @Override
    public EventSchedulerEntry sendAfter(Duration delay) {
        return sendAfter(delay.toMillis());
    }

    @Override
    public EventSchedulerEntry sendAfter(long delay, long interval) {
        return engineOperations.sendAfter(event, delay, interval);
    }

    @Override
    public EventSchedulerEntry sendAfter(Duration delay, Duration interval) {
        return sendAfter(delay.toMillis(), interval.toMillis());
    }

    @Override
    public EventSchedulerEntry sendAt(long milliseconds) {
        return engineOperations.sendAt(event, milliseconds);
    }

    @Override
    public EventSchedulerEntry sendAt(Instant instant) {
        return sendAt(instant.toEpochMilli());
    }

    @Override
    public EventSchedulerEntry sendAt(long milliseconds, long interval) {
        return engineOperations.sendAt(event, milliseconds, interval);
    }

    @Override
    public EventSchedulerEntry sendAt(Instant instant, Duration interval) {
        return sendAt(instant.toEpochMilli(), interval.toMillis());
    }

    @Override
    public EventSchedulerEntry sendAt(String crontabSpec) {
        return engineOperations.sendAt(event, crontabSpec);
    }

    @Override
    public Event make() {
        return event;
    }
}
