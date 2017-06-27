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

package org.openksavi.sponge.rule;

import org.openksavi.sponge.EventSetProcessor;
import org.openksavi.sponge.event.Event;

/**
 * Rule.
 */
public interface Rule extends EventSetProcessor<RuleAdapter>, RuleOperations {

    /**
     * Runs (fires) this rule.
     *
     * @param event
     *            event.
     */
    void run(Event event);

    /**
     * Returns an event that has a specified alias.
     *
     * @param eventAlias
     *            an event alias.
     * @return an event.
     */
    Event getEvent(String eventAlias);

    /**
     * Sets events.
     *
     * @param events
     *            events.
     */
    void setEvents(Object[] events);

    /**
     * Creates a new rule event specification.
     * 
     * @param eventName
     *            an event name.
     * @param eventAlias
     *            an event alias.
     * @param eventMode
     *            an event mode.
     * 
     * @return a new rule event specification.
     */
    RuleEventSpec makeEventSpec(String eventName, String eventAlias, EventMode eventMode);

    /**
     * Creates a new rule event specification.
     * 
     * @param eventName
     *            an event name.
     * @param eventMode
     *            an event mode.
     * 
     * @return a new rule event specification.
     */
    RuleEventSpec makeEventSpec(String eventName, EventMode eventMode);

    /**
     * Creates a new rule event specification.
     * 
     * @param eventName
     *            an event name.
     * @param eventAlias
     *            an event alias.
     * 
     * @return a new rule event specification.
     */
    RuleEventSpec makeEventSpec(String eventName, String eventAlias);

    /**
     * Creates a new rule event specification.
     * 
     * @param eventName
     *            an event name.
     * 
     * @return a new rule event specification.
     */
    RuleEventSpec makeEventSpec(String eventName);
}