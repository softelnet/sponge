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

package org.openksavi.sponge.engine;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.util.ValueHolder;

/**
 * Action Manager.
 */
public interface ActionManager extends EngineModule {

    /**
     * Adds a new action adapter.
     *
     * @param actionAdapter a new action adapter.
     */
    void addAction(ActionAdapter actionAdapter);

    /**
     * Removes the action adapter.
     *
     * @param actionName the name of the action adapter.
     */
    void removeAction(String actionName);

    /**
     * Calls the action. Throws {@code ProcessorNotFoundException} if the action is not registered.
     *
     * @param actionName the name of the registered action.
     * @param args the arguments for the action call. This parameter may be {@code null} and in that case no arguments will be passed to the
     *        action.
     * @return the result of executing the action.
     */
    Object callAction(String actionName, List<Object> args);

    /**
     * Calls the action. Throws {@code ProcessorNotFoundException} if the action is not registered.
     *
     * @param actionAdapter the action adapter.
     * @param args the arguments for the action call. This parameter may be {@code null} and in that case no arguments will be passed to the
     *        action.
     * @return the result of executing the action.
     */
    Object callAction(ActionAdapter actionAdapter, List<Object> args);

    /**
     * Calls the action with named arguments. Throws {@code ProcessorNotFoundException} if the action is not registered.
     *
     * @param actionName the name of the registered action.
     * @param args the arguments for the action call. This parameter may be {@code null} and in that case no arguments will be passed to the
     *        action.
     * @return the result of executing the action.
     */
    Object callAction(String actionName, Map<String, ?> args);

    /**
     * Calls the action with named arguments. Throws {@code ProcessorNotFoundException} if the action is not registered.
     *
     * @param actionAdapter the action adapter.
     * @param args the arguments for the action call. This parameter may be {@code null} and in that case no arguments will be passed to the
     *        action.
     * @return the result of executing the action.
     */
    Object callAction(ActionAdapter actionAdapter, Map<String, ?> args);

    /**
     * Calls the action if it exists.
     *
     * @param actionName the name of the registered action.
     * @param args the arguments for the action call. This parameter may be {@code null} and in that case no arguments will be passed to the
     *        action.
     * @return the action result wrapped in a value holder or {@code null} if the action is not registered.
     */
    ValueHolder<Object> callActionIfExists(String actionName, List<Object> args);

    /**
     * Calls the action with named arguments if it exists.
     *
     * @param actionName the name of the registered action.
     * @param args the arguments for the action call. This parameter may be {@code null} and in that case no arguments will be passed to the
     *        action.
     * @return the action result wrapped in a value holder or {@code null} if the action is not registered.
     */
    ValueHolder<Object> callActionIfExists(String actionName, Map<String, ?> args);

    /**
     * Returns registered processor adapter map.
     *
     * @return registered processor adapter map.
     */
    Map<String, ActionAdapter> getRegisteredActionAdapterMap();

    /**
     * Returns {@code true} if an action named {@code name} is registered.
     *
     * @param name action name.
     * @return {@code true} if an action named {@code name} is registered.
     */
    boolean hasAction(String name);

    /**
     * Returns an action adapter or {@code null} if action named {@code actionName} doesn't exist.
     *
     * @param actionName action name.
     * @return an action adapter.
     */
    ActionAdapter getActionAdapter(String actionName);

    /**
     * Returns action adapters matching the knowledge base name and the action name regular expressions. A {@code null} value of the regular
     * expression means that it will match any name.
     *
     * @param knowledgeBaseRegexp knowledge base name regular expression.
     * @param actionNameRegexp action name regular expression.
     * @return action adapters.
     */
    List<ActionAdapter> getActionAdapters(String knowledgeBaseRegexp, String actionNameRegexp);
}
