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

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;
import org.openksavi.sponge.event.EventDefinition;
import org.openksavi.sponge.event.EventSchedulerEntry;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.util.process.ProcessConfiguration;
import org.openksavi.sponge.util.process.ProcessConfigurationBuilder;
import org.openksavi.sponge.util.process.ProcessDefinition;

/**
 * An engine operations.
 */
public interface EngineOperations {

    /**
     * Returns the engine.
     *
     * @return the engine.
     */
    SpongeEngine getEngine();

    /**
     * Calls the registered action with arguments. Throws {@code ProcessorNotFoundException} when such action is not registered.
     *
     * @param actionName actionName name of the action.
     * @param args arguments to pass to action.
     * @return result of action called for specified arguments.
     */
    Object call(String actionName, Object... args);

    /**
     * Calls the registered action with arguments. Throws {@code ProcessorNotFoundException} when such action is not registered.
     *
     * @param resultClass result class.
     * @param actionName actionName name of the action.
     * @param args arguments to pass to action.
     * @param <T> result type.
     * @return result of action called for specified arguments.
     */
    <T> T call(Class<T> resultClass, String actionName, Object... args);

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
     * @param <T> Plugin type.
     */
    <T extends Plugin> T getPlugin(String name);

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
     * Returns the engine info.
     *
     * @return the engine info.
     */
    String getInfo();

    /**
     * Returns the engine statistics summary.
     *
     * @return the engine statistics summary.
     */
    String getStatisticsSummary();

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
     * @param <T> variable.
     */
    <T> T getVariable(String name);

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

    /**
     * Returns the configuration property value. Throws exception if not found.
     *
     * @param name the configuration property name.
     * @return the configuration property value.
     */
    String getProperty(String name);

    /**
     * Returns the configuration property value or {@code defaultValue} if not found.
     *
     * @param name the configuration property name.
     * @param defaultValue the default value
     * @return the configuration property value.
     */
    String getProperty(String name, String defaultValue);

    /**
     * Returns the home directory for the engine.
     *
     * @return the home directory for the engine.
     */
    String getHome();

    /**
     * Create a new process definition.
     *
     * @param processConfiguration the process configuration.
     * @return a new process definition.
     */
    ProcessDefinition process(ProcessConfiguration processConfiguration);

    /**
     * Create a new process definition.
     *
     * @param processConfigurationBuilder the process configuration builder.
     * @return a new process definition.
     */
    ProcessDefinition process(ProcessConfigurationBuilder processConfigurationBuilder);
}
