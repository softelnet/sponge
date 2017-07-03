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

import java.util.concurrent.atomic.AtomicBoolean;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.EngineModule;

/**
 * Engine module.
 */
public abstract class BaseEngineModule implements EngineModule {

    /** The engine. */
    private Engine engine;

    /** Name. */
    private String name;

    /** If this managed entity is running. */
    private AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Creates a new engine module.
     *
     * @param name name.
     * @param engine the engine.
     */
    protected BaseEngineModule(String name, Engine engine) {
        this.name = name;
        this.engine = engine;
    }

    /**
     * Creates a new engine module.
     *
     * @param name name.
     */
    protected BaseEngineModule(String name) {
        this(name, null);
    }

    /**
     * Creates a new engine module.
     */
    protected BaseEngineModule() {
    }

    /**
     * Sets the engine.
     *
     * @param engine the engine.
     */
    @Override
    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    /**
     * Returns the engine.
     *
     * @return the engine.
     */
    @Override
    public Engine getEngine() {
        return engine;
    }

    /**
     * Returns the name.
     *
     * @return name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets module name.
     *
     * @param name module name.
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Starts up this managed entity.
     */
    @Override
    public void startup() {
        setRunning(true);
    }

    /**
     * Shuts down this managed entity.
     */
    @Override
    public void shutdown() {
        setRunning(false);
    }

    /**
     * Informs whether this managed entity is running.
     *
     * @return if this managed entity is running.
     */
    @Override
    public boolean isRunning() {
        return running.get();
    }

    protected void setRunning(boolean running) {
        this.running.set(running);
    }

    @Override
    public String toString() {
        return name != null ? name : getClass().getSimpleName();
    }
}
