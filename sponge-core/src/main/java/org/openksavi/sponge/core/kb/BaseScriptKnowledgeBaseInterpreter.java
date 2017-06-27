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

package org.openksavi.sponge.core.kb;

import java.io.Reader;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseConstants;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBaseScript;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;
import org.openksavi.sponge.rule.EventMode;

/**
 * Script based knowledge base interpreter.
 */
public abstract class BaseScriptKnowledgeBaseInterpreter extends BaseKnowledgeBaseInterpreter implements ScriptKnowledgeBaseInterpreter {

    private static final Logger logger = LoggerFactory.getLogger(BaseScriptKnowledgeBaseInterpreter.class);

    public static final String PROP_PATH_SEPARATOR = ", \t";

    /** Synchronization processor. */
    protected Object interpteterSynchro = new Object();

    /**
     * Creates a new knowledge base interpreter.
     *
     * @param engineOperations
     *            an engine operations.
     * @param type
     *            a knowledge base type.
     */
    protected BaseScriptKnowledgeBaseInterpreter(KnowledgeBaseEngineOperations engineOperations, KnowledgeBaseType type) {
        super(engineOperations, type);

        prepareInterpreter();
    }

    /**
     * Prepares the interpreter.
     */
    protected abstract void prepareInterpreter();

    @Override
    public void load(List<KnowledgeBaseScript> scripts) {
        if (scripts.size() > 1) {
            logger.debug("Loading knowledge base '{}' from files: {}.", scripts.get(0).getKnowledgeBase().getName(),
                    scripts.stream().map(script -> script.getFileName()).collect(Collectors.joining(", ", "'", "'")));
        }
        synchronized (interpteterSynchro) {
            scripts.forEach(script -> loadKnowledgeBaseScript(script));
        }
    }

    @Override
    public void reload(List<KnowledgeBaseScript> scripts) {
        load(scripts);
    }

    private void loadKnowledgeBaseScript(KnowledgeBaseScript script) {
        logger.info("Loading knowledge base '{}' file '{}'.", script.getKnowledgeBase().getName(), script.getFileName());

        if (script.getCharset() != null) {
            load(script.getFileName(), script.getCharset());
        } else {
            load(script.getFileName());
        }
    }

    /**
     * Loads the knowledge base from the file.
     *
     * @param fileName
     *            file name.
     */
    @Override
    public void load(String fileName) {
        load(fileName, Charset.defaultCharset());
    }

    /**
     * Loads the knowledge base from the file.
     *
     * @param fileName
     *            file name.
     * @param charset
     *            charset.
     */
    @Override
    public void load(String fileName, String charset) {
        load(fileName, Charset.forName(charset));
    }

    @Override
    public void load(String fileName, Charset charset) {
        synchronized (interpteterSynchro) {
            Engine engine = getEngineOperations().getEngine();
            try (Reader reader = engine.getKnowledgeBaseFileProvider().getReader(engine, fileName, charset)) {
                eval(reader, fileName);
            } catch (Throwable e) {
                throw Utils.wrapException("load", e);
            }
        }
    }

    @Override
    public void onInit() {
        invokeOptionalFunction(KnowledgeBaseConstants.FUN_ON_INIT);
    }

    @Override
    public void onLoad() {
        // Scan to auto-enable processors if this functionality is turned on.
        if (getEngineOperations().getEngine().getConfigurationManager().getAutoEnable()) {
            scanToAutoEnable();
        }

        invokeOptionalFunction(KnowledgeBaseConstants.FUN_ON_LOAD);
    }

    /**
     * Calls onStartup method in the knowledge base.
     */
    @Override
    public void onStartup() {
        invokeOptionalFunction(KnowledgeBaseConstants.FUN_ON_STARTUP);
    }

    /**
     * Calls onShutdown method in the knowledge base.
     */
    @Override
    public void onShutdown() {
        invokeOptionalFunction(KnowledgeBaseConstants.FUN_ON_SHUTDOWN);
    }

    /**
     * Calls onBeforeReload method in the knowledge base.
     */
    @Override
    public void onBeforeReload() {
        invokeOptionalFunction(KnowledgeBaseConstants.FUN_ON_BEFORE_RELOAD);
    }

    /**
     * Calls onAfterReload method in the knowledge base.
     */
    @Override
    public void onAfterReload() {
        invokeOptionalFunction(KnowledgeBaseConstants.FUN_ON_AFTER_RELOAD);
    }

    @Override
    public <T> T eval(String expression, Class<T> cls) {
        return eval(expression);
    }

    public abstract <T> T eval(Reader reader, String fileName);

    protected abstract ScriptKnowledgeBaseInterpreter createInstance(Engine engine, KnowledgeBase knowledgeBase);

    protected List<Class<?>> getStandardImportClasses() {
        //@formatter:off
        return Arrays.asList(EventMode.class, EventClonePolicy.class, Utils.class, SpongeException.class,
                Event.class, Configuration.class,
                Duration.class, Instant.class, ChronoUnit.class);
        //@formatter:on
    }
}