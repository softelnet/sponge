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

import java.util.UUID;
import java.util.function.Supplier;

import org.openksavi.sponge.engine.Session;
import org.openksavi.sponge.engine.Variables;

/**
 * A default implementation of session.
 */
public class DefaultSession implements Session {

    private static final long serialVersionUID = 3402263770070460564L;

    protected String uuid;

    protected Variables variables = new MapVariables();

    protected DefaultSession() {
        //
    }

    @Override
    public void setVariable(String name, Object value) {
        variables.set(name, value);
    }

    @Override
    public Object getVariable(String name) {
        return variables.get(name);
    }

    @Override
    public <T> T getVariable(Class<T> cls, String name) {
        return variables.get(cls, name);
    }

    @Override
    public <T> T getVariable(String name, T defaultValue) {
        return variables.get(name, defaultValue);
    }

    @Override
    public <T> T getVariable(Class<T> cls, String name, T defaultValue) {
        return variables.get(cls, name, defaultValue);
    }

    @Override
    public boolean existsVariable(String name) {
        return variables.exists(name);
    }

    @Override
    public void removeVariable(String name) {
        variables.remove(name);
    }

    @Override
    public <T> void setVariableIfNone(String name, Supplier<T> supplier) {
        variables.setIfNone(name, supplier);
    }

    public String getUuid() {
        return uuid;
    }

    public static Session createNewSession() {
        DefaultSession session = new DefaultSession();

        session.uuid = UUID.randomUUID().toString();

        return session;
    }

    @Override
    public void updateVariable(String name) {
        // The default implementation doesn't do anything here.
    }
}
