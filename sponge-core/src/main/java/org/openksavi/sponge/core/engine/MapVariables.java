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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.commons.lang3.ObjectUtils;

import org.openksavi.sponge.engine.Variables;

/**
 * Thread-safe variable map.
 */
public class MapVariables implements Variables {

    private static final long serialVersionUID = 6240397591532960287L;

    private Map<String, Object> variables = Collections.synchronizedMap(new HashMap<>());

    private String normalizeName(String name) {
        return name.trim();
    }

    @Override
    public void set(String name, Object value) {
        variables.put(normalizeName(name), value);
    }

    @Override
    public Object get(String name) {
        return doGet(name, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> cls, String name) {
        return (T) get(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String name, T defaultValue) {
        return (T) ObjectUtils.defaultIfNull(doGet(name, false), defaultValue);
    }

    @Override
    public <T> T get(Class<T> cls, String name, T defaultValue) {
        return get(name, defaultValue);
    }

    protected Object doGet(String name, boolean required) {
        String normalizedName = normalizeName(name);
        synchronized (variables) {
            if (required && !variables.containsKey(normalizedName)) {
                throw new IllegalArgumentException("Variable '" + normalizedName + "' not found");
            }

            return variables.get(normalizedName);
        }
    }

    @Override
    public void remove(String name) {
        variables.remove(normalizeName(name));
    }

    @Override
    public boolean has(String name) {
        return variables.containsKey(normalizeName(name));
    }

    @Override
    public <T> void setIfNone(String name, Supplier<T> supplier) {
        synchronized (variables) {
            if (!has(name)) {
                set(name, supplier.get());
            }
        }
    }
}
