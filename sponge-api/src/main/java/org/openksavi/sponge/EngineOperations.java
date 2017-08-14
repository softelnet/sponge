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

package org.openksavi.sponge;

import java.util.function.Supplier;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;
import org.openksavi.sponge.event.EventDefinition;
import org.openksavi.sponge.event.EventSchedulerEntry;
import org.openksavi.sponge.plugin.Plugin;

/**
 * An engine operations.
 */
public interface EngineOperations {

    /**
     * Returns the engine.
     *
     * @return the engine.
     */
    Engine getEngine();

    /**
     * Calls registered action with arguments.
     *
     * @param actionName actionName name of the action.
     * @param args arguments to pass to action.
     * @return result of action called for specified arguments.
     */
    Object callAction(String actionName, Object... args);

    /**
     * Shuts down the engine using the current thread.
     */
    void shutdown();

    /**
     * Shuts down the engine using another thread.
     */
    void requestShutdown();

    /**
     * Reloads script-based knowledge bases.
     */
    void reload();

    /**
     * Reloads script-based knowledge bases using another thread.
     */
    void requestReload();

    /**
     * Sends an event to the Input Event Queue. May throw {@code QueueFullException} if the Input Event Queue is full.
     *
     * @param event an event.
     * @return an event.
     */
    Event send(Event event);

    /**
     * Sends a new event to the Input Event Queue. May throw {@code QueueFullException} if the Input Event Queue is full.
     *
     * @param eventName an event name.
     * @return an event.
     */
    Event send(String eventName);

    /**
     * Sends a new event to the Input Event Queue after a specified time.
     *
     * @param name event name.
     * @param delay delay in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAfter(String name, long delay);

    /**
     * Sends a new event to the Input Event Queue periodically after a specified time.
     *
     * @param name event name.
     * @param delay delay in milliseconds.
     * @param interval interval in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAfter(String name, long delay, long interval);

    /**
     * Sends an event to the Input Event Queue after a specified time.
     *
     * @param event an event.
     * @param delay delay in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAfter(final Event event, long delay);

    /**
     * Sends an event to the Input Event Queue periodically after a specified time.
     *
     * @param event an event.
     * @param delay delay in milliseconds.
     * @param interval interval in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAfter(final Event event, long delay, long interval);

    /**
     * Sends a new event to the Input Event Queue at a specified time.
     *
     * @param name event name.
     * @param milliseconds time in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAt(String name, long milliseconds);

    /**
     * Sends a new event to the Input Event Queue at a specified time and then periodically.
     *
     * @param name event name.
     * @param milliseconds time in milliseconds.
     * @param interval interval in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAt(String name, long milliseconds, long interval);

    /**
     * Sends an event to the Input Event Queue at a specified time.
     *
     * @param event event.
     * @param milliseconds time in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAt(final Event event, long milliseconds);

    /**
     * Sends an event to the Input Event Queue at a specified time and then periodically.
     *
     * @param event event.
     * @param milliseconds time in milliseconds.
     * @param interval interval in milliseconds.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAt(final Event event, long milliseconds, long interval);

    /**
     * Sends an event to the Input Event Queue at a time specified by the crontab spec.
     *
     * @param event event.
     * @param crontabSpec crontab spec.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAt(final Event event, String crontabSpec);

    /**
     * Sends a new event to the Input Event Queue at a time specified by the crontab spec.
     *
     * @param name event name.
     * @param crontabSpec crontab spec.
     * @return scheduled event entry.
     */
    EventSchedulerEntry sendAt(String name, String crontabSpec);

    /**
     * Removes scheduled event.
     *
     * @param entry scheduled event entry.
     * @return informs whether the specified event has been scheduled.
     */
    boolean removeEvent(EventSchedulerEntry entry);

    /**
     * Returns the plugin that has the specified name or {@code null} if there is no such plugin.
     *
     * @param name plugin name.
     * @return plugin.
     */
    Plugin getPlugin(String name);

    /**
     * Returns the plugin that has the specified name and type or {@code null} if there is no such plugin.
     *
     * @param name plugin name.
     * @param cls plugin class.
     * @return plugin.
     * @param <T> Plugin type.
     */
    <T extends Plugin> T getPlugin(String name, Class<T> cls);

    /**
     * Returns the plugin that has the specified type or {@code null} if there is no such plugin.
     *
     * @param cls plugin class.
     * @return plugin.
     * @param <T> Plugin type.
     */
    <T extends Plugin> T getPlugin(Class<T> cls);

    /**
     * Returns the engine version.
     *
     * @return the engine version.
     */
    String getVersion();

    /**
     * Returns the engine description.
     *
     * @return the engine description.
     */
    String getDescription();

    /**
     * Returns the engine statistics summary.
     *
     * @return the engine statistics summary.
     */
    String getStatisticsSummary();

    /**
     * Creates a new named event with default clone policy.
     *
     * @param name event name.
     * @return a new event.
     */
    Event makeEvent(String name);

    /**
     * Creates a new named event with specified clone policy.
     *
     * @param name event name.
     * @param policy event clone policy.
     * @return a new event.
     */
    Event makeEvent(String name, EventClonePolicy policy);

    /**
     * Creates a new event definition.
     *
     * @param name event name.
     * @return a new event definition.
     */
    EventDefinition event(String name);

    /**
     * Creates a new event definition.
     *
     * @param name event name.
     * @param policy event clone policy.
     * @return a new event definition.
     */
    EventDefinition event(String name, EventClonePolicy policy);

    /**
     * Creates a new event definition.
     *
     * @param event an event.
     * @return a new event definition.
     */
    EventDefinition event(Event event);

    /**
     * Returns {@code true} if an action named {@code name} exists.
     *
     * @param name action name.
     * @return {@code true} if an action named {@code name} exists.
     */
    boolean existsAction(String name);

    /**
     * Returns {@code true} if a filter named {@code name} exists.
     *
     * @param name filter name.
     * @return {@code true} if a filter named {@code name} exists.
     */
    boolean existsFilter(String name);

    /**
     * Returns {@code true} if a trigger named {@code name} exists.
     *
     * @param name trigger name.
     * @return {@code true} if a trigger named {@code name} exists.
     */
    boolean existsTrigger(String name);

    /**
     * Returns {@code true} if a rule named {@code name} exists.
     *
     * @param name rule name.
     * @return {@code true} if a rule named {@code name} exists.
     */
    boolean existsRule(String name);

    /**
     * Returns {@code true} if a correlator named {@code name} exists.
     *
     * @param name correlator name.
     * @return {@code true} if a correlator named {@code name} exists.
     */
    boolean existsCorrelator(String name);

    /**
     * Sets the engine scope variable.
     *
     * @param name variable name.
     * @param value variable value.
     */
    void setVariable(String name, Object value);

    /**
     * Returns the value of the engine scope variable. Throws exception if not found.
     *
     * @param name variable name.
     * @return variable value.
     */
    Object getVariable(String name);

    /**
     * Returns the value of the engine scope variable. Throws exception if not found.
     *
     * @param cls variable class.
     * @param name variable name.
     *
     * @return variable value.
     * @param <T> variable.
     */
    <T> T getVariable(Class<T> cls, String name);

    /**
     * Returns the value of the engine scope variable or {@code defaultValue} if not found.
     *
     * @param name variable name.
     * @param defaultValue default value.
     *
     * @return variable value.
     * @param <T> variable.
     */
    <T> T getVariable(String name, T defaultValue);

    /**
     * Returns the value of the engine scope variable or {@code defaultValue} if not found.
     *
     * @param cls variable class.
     * @param name variable name.
     * @param defaultValue default value.
     *
     * @return variable value.
     * @param <T> variable.
     */
    <T> T getVariable(Class<T> cls, String name, T defaultValue);

    /**
     * Removes the engine scope variable.
     *
     * @param name variable name.
     */
    void removeVariable(String name);

    /**
     * Returns {@code true} if the engine scope variable named {@code name} exists.
     *
     * @param name variable name.
     * @return {@code true} if the engine scope variable named {@code name} exists.
     */
    boolean existsVariable(String name);

    /**
     * Sets the engine scope variable if not set already.
     *
     * @param name variable name.
     * @param supplier variable value supplier.
     * @param <T> variable.
     */
    <T> void setVariableIfNone(String name, Supplier<T> supplier);

    /**
     * Updates the engine scope variable.
     *
     * @param name variable name.
     */
    void updateVariable(String name);
}
