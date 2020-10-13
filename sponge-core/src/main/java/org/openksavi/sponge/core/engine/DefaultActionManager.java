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

package org.openksavi.sponge.core.engine;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.ProcessorNotFoundException;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.InactiveActionException;
import org.openksavi.sponge.action.IsActionActiveContext;
import org.openksavi.sponge.core.BaseProcessorDefinition;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.ActionManager;
import org.openksavi.sponge.engine.ProcessorType;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.util.SpongeApiUtils;
import org.openksavi.sponge.util.ValueHolder;

/**
 * Action manager.
 */
public class DefaultActionManager extends BaseEngineModule implements ActionManager {

    /** Registered actions map (action name, action adapter). */
    private Map<String, ActionAdapter> registeredActions = Collections.synchronizedMap(new LinkedHashMap<>());

    public DefaultActionManager(SpongeEngine engine) {
        super("ActionManager", engine);
    }

    @Override
    public void addAction(ActionAdapter actionAdapter) {
        Validate.notNull(actionAdapter, "addAction called with null actionAdapter.");

        registeredActions.put(actionAdapter.getMeta().getName(), actionAdapter);
    }

    @Override
    public void removeAction(String actionName) {
        Validate.notNull(actionName, "removeAction called with null action name.");

        registeredActions.remove(actionName);
    }

    @Override
    public Object callAction(String actionName, List<Object> args) {
        ActionAdapter action = registeredActions.get(actionName);
        if (action == null) {
            throw new ProcessorNotFoundException(ProcessorType.ACTION, actionName);
        }

        return callAction(action, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object callAction(ActionAdapter actionAdapter, List<Object> args) {
        if (!actionAdapter.isActive(new IsActionActiveContext(args))) {
            throw new InactiveActionException(actionAdapter.getMeta().getName());
        }

        validateActionCallArgs(actionAdapter, args);

        try {
            return ((BaseProcessorDefinition) actionAdapter.getDefinition()).getProcessorProvider()
                    .invokeActionOnCall(actionAdapter.getProcessor(), args);
        } catch (Throwable e) {
            throw SpongeUtils.wrapException(actionAdapter.getProcessor(), e);
        }
    }

    @Override
    public Object callAction(String actionName, Map<String, ?> args) {
        ActionAdapter action = registeredActions.get(actionName);
        if (action == null) {
            throw new ProcessorNotFoundException(ProcessorType.ACTION, actionName);
        }

        return callAction(action, args);
    }

    @Override
    public Object callAction(ActionAdapter actionAdapter, Map<String, ?> args) {
        return callAction(actionAdapter, SpongeUtils.buildActionArgsList(actionAdapter, args));
    }

    protected void validateActionCallArgs(ActionAdapter actionAdapter, List<Object> args) {
        if (args == null) {
            return;
        }

        SpongeApiUtils.validateActionCallArgs(actionAdapter.getMeta().getArgs(), args);
    }

    @Override
    public ValueHolder<Object> callActionIfExists(String actionName, List<Object> args) {
        ActionAdapter action = registeredActions.get(actionName);
        if (action == null) {
            return null;
        }

        return new ValueHolder<>(callAction(action, args));
    }

    @Override
    public ValueHolder<Object> callActionIfExists(String actionName, Map<String, ?> args) {
        ActionAdapter action = registeredActions.get(actionName);
        if (action == null) {
            return null;
        }

        return new ValueHolder<>(callAction(action, args));
    }

    @Override
    public Map<String, ActionAdapter> getRegisteredActionAdapterMap() {
        return registeredActions;
    }

    @Override
    public boolean hasAction(String name) {
        return registeredActions.containsKey(name);
    }

    @Override
    public ActionAdapter getActionAdapter(String actionName) {
        return registeredActions.get(actionName);
    }

    @Override
    public List<ActionAdapter> getActionAdapters(String knowledgeBaseRegexp, String actionNameRegexp) {
        return registeredActions.values().stream()
                .filter(adapter -> (knowledgeBaseRegexp == null || adapter.getKnowledgeBase().getName().matches(knowledgeBaseRegexp))
                        && (actionNameRegexp == null || adapter.getMeta().getName().matches(actionNameRegexp)))
                .collect(Collectors.toList());
    }
}
