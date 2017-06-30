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

package org.openksavi.sponge.engine;

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
     * Calls the action.
     *
     * @param actionName Name of registered action.
     * @param args Arguments for Action.
     * @return result of executing action.
     */
    Object callAction(String actionName, Object... args);

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
}
