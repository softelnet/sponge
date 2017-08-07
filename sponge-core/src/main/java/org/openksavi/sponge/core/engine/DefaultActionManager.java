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

package org.openksavi.sponge.core.engine;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.engine.ActionManager;
import org.openksavi.sponge.engine.Engine;

/**
 * Action manager.
 */
public class DefaultActionManager extends BaseEngineModule implements ActionManager {

    /** Registered actions map (action name, action adapter). */
    private Map<String, ActionAdapter> registeredActions = Collections.synchronizedMap(new LinkedHashMap<>());

    public DefaultActionManager(Engine engine) {
        super("ActionManager", engine);
    }

    @Override
    public void addAction(ActionAdapter actionAdapter) {
        Validate.notNull(actionAdapter, "addAction called with null actionAdapter.");

        registeredActions.put(actionAdapter.getName(), actionAdapter);
    }

    @Override
    public void removeAction(String actionName) {
        Validate.notNull(actionName, "removeAction called with null action name.");

        registeredActions.remove(actionName);
    }

    @Override
    public Object callAction(String actionName, Object... args) {
        ActionAdapter action = registeredActions.get(actionName);
        if (action == null) {
            throw new SpongeException("Action " + actionName + " is not registered");
        }

        return action.getProcessor().onCall(args);
    }

    @Override
    public Map<String, ActionAdapter> getRegisteredActionAdapterMap() {
        return registeredActions;
    }

    @Override
    public boolean existsAction(String name) {
        return registeredActions.containsKey(name);
    }
}
