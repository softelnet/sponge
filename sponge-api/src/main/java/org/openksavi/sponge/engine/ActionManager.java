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
     * Calls the action. Throws {@code ProcessorNotFoundException} when such action is not registered.
     *
     * @param actionName name of registered action.
     * @param args arguments for an action call. This parameter may be {@code null} and in that case it will be passed as an empty array to
     *        the action.
     * @return result of executing action.
     */
    Object callAction(String actionName, Object[] args);

    /**
     * Returns registered processor adapter map.
     *
     * @return registered processor adapter map.
     */
    Map<String, ActionAdapter> getRegisteredActionAdapterMap();

    /**
     * Returns {@code true} if an action named {@code name} exists.
     *
     * @param name action name.
     * @return {@code true} if an action named {@code name} exists.
     */
    boolean existsAction(String name);

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
