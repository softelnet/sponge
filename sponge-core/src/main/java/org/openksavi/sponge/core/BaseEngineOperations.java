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

package org.openksavi.sponge.core;

import java.util.function.Supplier;

import org.openksavi.sponge.EngineOperations;
import org.openksavi.sponge.core.engine.BaseSpongeEngine;
import org.openksavi.sponge.core.event.AttributeMapEvent;
import org.openksavi.sponge.core.event.DefaultEventDefinition;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;
import org.openksavi.sponge.event.EventDefinition;
import org.openksavi.sponge.event.EventSchedulerEntry;
import org.openksavi.sponge.plugin.Plugin;

public class BaseEngineOperations implements EngineOperations {

    protected BaseSpongeEngine engine;

    public BaseEngineOperations(BaseSpongeEngine engine) {
        this.engine = engine;
    }

    @Override
    public BaseSpongeEngine getEngine() {
        return engine;
    }

    /**
     * Calls registered action with arguments.
     *
     * @param actionName name of the action.
     * @param args arguments to pass to action.
     */
    @Override
    public Object call(String actionName, Object... args) {
        return engine.getActionManager().callAction(actionName, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T call(Class<T> resultClass, String actionName, Object... args) {
        Object result = engine.getActionManager().callAction(actionName, args);

        SpongeUtils.isTrue(result == null || resultClass.isInstance(result), "Action result cannot be cast to expected class %s",
                resultClass);

        return (T) result;
    }

    @Override
    public Event send(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        engine.getEventScheduler().scheduleNow(event);

        return event;
    }

    @Override
    public Event send(String eventName) {
        return send(makeEvent(eventName));
    }

    @Override
    public EventSchedulerEntry sendAfter(String name, long delay) {
        return sendAfter(makeEvent(name), delay, 0);
    }

    @Override
    public EventSchedulerEntry sendAfter(String name, long delay, long interval) {
        return sendAfter(makeEvent(name), delay, interval);
    }

    @Override
    public EventSchedulerEntry sendAfter(final Event event, long delay) {
        return sendAfter(event, delay, 0);
    }

    @Override
    public EventSchedulerEntry sendAfter(final Event event, long delay, long interval) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        return engine.getEventScheduler().scheduleAfter(event, delay, interval);
    }

    @Override
    public EventSchedulerEntry sendAt(String name, long milliseconds) {
        return sendAt(makeEvent(name), milliseconds, 0);
    }

    @Override
    public EventSchedulerEntry sendAt(String name, long milliseconds, long interval) {
        return sendAt(makeEvent(name), milliseconds, interval);
    }

    @Override
    public EventSchedulerEntry sendAt(final Event event, long milliseconds) {
        return sendAt(event, milliseconds, 0);
    }

    @Override
    public EventSchedulerEntry sendAt(final Event event, long milliseconds, long interval) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        return engine.getEventScheduler().scheduleAt(event, milliseconds, interval);
    }

    @Override
    public EventSchedulerEntry sendAt(final Event event, String crontabSpec) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (crontabSpec == null) {
            throw new IllegalArgumentException("Crontab specification cannot be null");
        }
        return engine.getEventScheduler().scheduleAt(event, crontabSpec);
    }

    @Override
    public EventSchedulerEntry sendAt(String name, String crontabSpec) {
        return sendAt(makeEvent(name), crontabSpec);
    }

    /**
     * Removes event from Event Scheduler.
     *
     * @param entry scheduled event entry.
     * @return informs whether the specified event has been scheduled.
     */
    @Override
    public boolean removeEvent(EventSchedulerEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Event entry cannot be null");
        }
        return engine.getEventScheduler().remove(entry);
    }

    /**
     * Returns the plugin that has the specified name or {@code null} if there is no such plugin.
     *
     * @param name plugin name.
     * @return plugin.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Plugin> T getPlugin(String name) {
        return (T) engine.getPluginManager().getPlugin(name);
    }

    @Override
    public <T extends Plugin> T getPlugin(String name, Class<T> cls) {
        return engine.getPluginManager().getPlugin(name, cls);
    }

    @Override
    public <T extends Plugin> T getPlugin(Class<T> cls) {
        return engine.getPluginManager().getPlugin(cls);
    }

    /**
     * Returns the engine version.
     *
     * @return the engine version.
     */
    @Override
    public String getVersion() {
        return engine.getVersion();
    }

    /**
     * Returns the engine info.
     *
     * @return the engine info.
     */
    @Override
    public String getInfo() {
        return engine.getInfo();
    }

    /**
     * Returns the engine statistics summary.
     *
     * @return the engine statistics summary.
     */
    @Override
    public String getStatisticsSummary() {
        return engine.getStatisticsManager().getSummary();
    }

    /**
     * Creates a new named event with default clone policy.
     *
     * @param name event name.
     * @return a new event.
     */
    @Override
    public Event makeEvent(String name) {
        return new AttributeMapEvent(name, engine.getConfigurationManager().getEventClonePolicy());
    }

    /**
     * Creates a new named event with specified clone policy.
     *
     * @param name event name.
     * @param policy event clone policy.
     * @return a new event.
     */
    @Override
    public Event makeEvent(String name, EventClonePolicy policy) {
        return new AttributeMapEvent(name, policy);
    }

    @Override
    public EventDefinition event(String name) {
        return new DefaultEventDefinition(this, name, engine.getConfigurationManager().getEventClonePolicy());
    }

    @Override
    public EventDefinition event(String name, EventClonePolicy policy) {
        return new DefaultEventDefinition(this, name, policy);
    }

    @Override
    public EventDefinition event(Event event) {
        return new DefaultEventDefinition(this, event);
    }

    @Override
    public void shutdown() {
        engine.shutdown();
    }

    @Override
    public void requestShutdown() {
        engine.requestShutdown();
    }

    @Override
    public void reload() {
        engine.reload();
    }

    @Override
    public void requestReload() {
        engine.requestReload();
    }

    @Override
    public boolean existsAction(String name) {
        return engine.getActionManager().existsAction(name);
    }

    @Override
    public boolean existsFilter(String name) {
        return engine.getFilterProcessingUnit().existsProcessor(name);
    }

    @Override
    public boolean existsTrigger(String name) {
        return engine.getMainProcessingUnit().existsProcessor(name);
    }

    @Override
    public boolean existsRule(String name) {
        return engine.getMainProcessingUnit().existsProcessor(name);
    }

    @Override
    public boolean existsCorrelator(String name) {
        return engine.getMainProcessingUnit().existsProcessor(name);
    }

    @Override
    public void setVariable(String name, Object value) {
        engine.getSession().setVariable(name, value);
    }

    @Override
    public <T> T getVariable(String name) {
        return engine.getSession().getVariable(name);
    }

    @Override
    public <T> T getVariable(Class<T> cls, String name) {
        return engine.getSession().getVariable(cls, name);
    }

    @Override
    public <T> T getVariable(String name, T defaultValue) {
        return engine.getSession().getVariable(name, defaultValue);
    }

    @Override
    public <T> T getVariable(Class<T> cls, String name, T defaultValue) {
        return engine.getSession().getVariable(cls, name, defaultValue);
    }

    @Override
    public boolean existsVariable(String name) {
        return engine.getSession().existsVariable(name);
    }

    @Override
    public void removeVariable(String name) {
        engine.getSession().removeVariable(name);
    }

    @Override
    public <T> void setVariableIfNone(String name, Supplier<T> supplier) {
        engine.getSession().setVariableIfNone(name, supplier);
    }

    @Override
    public void updateVariable(String name) {
        engine.getSession().updateVariable(name);
    }

    @Override
    public String getProperty(String name) {
        return engine.getConfigurationManager().getProperty(name);
    }

    @Override
    public String getHome() {
        return engine.getConfigurationManager().getHome();
    }
}
