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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.Service.State;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.EngineModule;
import org.openksavi.sponge.engine.SpongeEngine;

/**
 * A base engine module.
 */
public abstract class BaseEngineModule implements EngineModule {

    /** The engine. */
    private SpongeEngine engine;

    /** Name. */
    private String name;

    /** Display name. */
    private String displayName;

    /** Description. */
    private String description;

    /** Guava service. */
    private Service service = new AbstractIdleService() {

        @Override
        protected void startUp() throws Exception {
            doStartup();
        }

        @Override
        protected void shutDown() throws Exception {
            doShutdown();
        }

        @Override
        protected String serviceName() {
            return BaseEngineModule.this.getClass().getSimpleName();
        }
    };

    private AtomicBoolean afterManualShutdown = new AtomicBoolean(false);

    /** Lock. */
    private Lock lock = new ReentrantLock(true);

    /**
     * Creates a new module.
     *
     * @param name name.
     * @param engine the engine.
     */
    protected BaseEngineModule(String name, SpongeEngine engine) {
        this.name = name;
        this.engine = engine;
    }

    /**
     * Creates a new module.
     *
     * @param name name.
     */
    protected BaseEngineModule(String name) {
        this(name, null);
    }

    /**
     * Creates a new module.
     */
    protected BaseEngineModule() {
    }

    /**
     * Sets the engine.
     *
     * @param engine the engine.
     */
    @Override
    public void setEngine(SpongeEngine engine) {
        this.engine = engine;
    }

    /**
     * Returns the engine.
     *
     * @return the engine.
     */
    @Override
    public SpongeEngine getEngine() {
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

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    protected void doStartup() {
        //
    }

    protected void doShutdown() {
        //
    }

    /**
     * Starts up this managed entity.
     */
    @Override
    public void startup() {
        if (isStarting() || isRunning()) {
            return;
        }

        lock.lock();
        try {
            service.startAsync().awaitRunning();
        } catch (IllegalStateException e) {
            // If Guava Service startup has failed, throw only the cause exception.
            throw isFailed() && e.getCause() != null ? SpongeUtils.wrapException("startup", e.getCause()) : e;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Shuts down this managed entity.
     */
    @Override
    public void shutdown() {
        if (isStopping() || isTerminated()) {
            return;
        }

        lock.lock();
        try {
            if (afterManualShutdown.get()) {
                return;
            }

            // Stop Guava Service in a Guava way only if it isn't in FAILED state.
            if (!isFailed()) {
                try {
                    service.stopAsync().awaitTerminated();
                } catch (IllegalStateException e) {
                    // If Guava Service stopping has failed, throw only the cause exception.
                    throw isFailed() && e.getCause() != null ? SpongeUtils.wrapException("shutdown", e.getCause()) : e;
                }
            } else {
                try {
                    doShutdown();
                } finally {
                    // Shutdown only once, even if there has been an error.
                    afterManualShutdown.set(true);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public final boolean isNew() {
        return service.state() == State.NEW;
    }

    @Override
    public final boolean isStarting() {
        return service.state() == State.STARTING;
    }

    /**
     * Informs whether this managed entity is running.
     *
     * @return if this managed entity is running.
     */
    @Override
    public final boolean isRunning() {
        return service.isRunning();
    }

    @Override
    public final boolean isStopping() {
        return service.state() == State.STOPPING;
    }

    @Override
    public final boolean isTerminated() {
        return service.state() == State.TERMINATED;
    }

    @Override
    public final boolean isFailed() {
        return service.state() == State.FAILED;
    }

    public State getState() {
        return service.state();
    }

    public Service getService() {
        return service;
    }

    public boolean isNewOrStartingOrRunning() {
        return isNew() || isStarting() || isRunning();
    }

    @Override
    public String toString() {
        return name != null ? name : getClass().getSimpleName();
    }
}
