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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.kb.DefaultKnowledgeBase;
import org.openksavi.sponge.core.kb.DefaultScriptKnowledgeBase;
import org.openksavi.sponge.core.kb.FileKnowledgeBaseScript;
import org.openksavi.sponge.core.kb.KnowledgeBaseScriptProviderMapping;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.KnowledgeBaseManager;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseInterpreter;
import org.openksavi.sponge.kb.KnowledgeBaseScript;
import org.openksavi.sponge.kb.KnowledgeBaseScriptProvider;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.kb.ScriptKnowledgeBase;
import org.openksavi.sponge.spi.KnowledgeBaseInterpreterFactory;
import org.openksavi.sponge.spi.KnowledgeBaseInterpreterFactoryProvider;

/**
 * Knowledge base manager.
 */
public class DefaultKnowledgeBaseManager extends BaseEngineModule implements KnowledgeBaseManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultKnowledgeBaseManager.class);

    /** Configuration knowledge bases tag. */
    private static final String CFG_KNOWLEDGE_BASES = "knowledgeBases";

    /** Configuration knowledge base name attribute name. */
    private static final String CFG_KNOWLEDGE_BASE_ATTR_NAME = "name";

    /** Configuration knowledge base type attribute label. */
    private static final String CFG_KNOWLEDGE_BASE_ATTR_LABEL = "label";

    /** Configuration knowledge base type attribute name. */
    private static final String CFG_KNOWLEDGE_BASE_ATTR_TYPE = "type";

    /** Configuration knowledge base class attribute name. */
    private static final String CFG_KNOWLEDGE_BASE_ATTR_CLASS = "class";

    /** Configuration knowledge base description. */
    private static final String CFG_KNOWLEDGE_BASE_DESCRIPTION = "description";

    /** Configuration knowledge base file tag. */
    private static final String CFG_KNOWLEDGE_BASE_FILE = "file";

    /** Configuration knowledge base file charset attribute name. */
    private static final String CFG_KB_FILE_ATTR_CHARSET = "charset";

    /** Configuration knowledge base file required attribute name. */
    private static final String CFG_KB_FILE_ATTR_REQUIRED = "required";

    /** Knowledge base interpreter factories. */
    protected Map<String, KnowledgeBaseInterpreterFactory> knowledgeBaseInterpreterFactories =
            Collections.synchronizedMap(new LinkedHashMap<>());

    /** Knowledge bases in a map (name, KnowledgeBase). */
    protected Map<String, KnowledgeBase> knowledgeBases = Collections.synchronizedMap(new LinkedHashMap<>());

    protected KnowledgeBase defaultKnowledgeBase;

    /** Knowledge base script provider mapping. */
    protected KnowledgeBaseScriptProviderMapping knowledgeBaseScriptProviderMapping;

    /**
     * Creates a new knowledge base manager.
     *
     * @param engine the engine.
     */
    public DefaultKnowledgeBaseManager(SpongeEngine engine) {
        super("KnowledgeBaseManager", engine);

        // Add default, Java-based knowledge base (mainly for plugin registration).
        defaultKnowledgeBase = new DefaultKnowledgeBase();
        knowledgeBases.put(defaultKnowledgeBase.getName(), defaultKnowledgeBase);

        knowledgeBaseScriptProviderMapping = new KnowledgeBaseScriptProviderMapping(engine);
    }

    /**
     * Configures this knowledge base manager.
     *
     * @param configuration configuration.
     */
    @Override
    public void configure(Configuration configuration) {
        for (Configuration knowledgeBaseNode : configuration.getChildConfigurationsOf(CFG_KNOWLEDGE_BASES)) {
            addKnowledgeBase(createKnowledgeBaseFromConfiguration(knowledgeBaseNode));
        }
    }

    protected KnowledgeBase createKnowledgeBaseFromConfiguration(Configuration configuration) {
        String name = configuration.getAttribute(CFG_KNOWLEDGE_BASE_ATTR_NAME, null);
        if (StringUtils.isEmpty(name)) {
            throw new SpongeException("Knowledge base name must not be empty");
        }

        String label = configuration.getAttribute(CFG_KNOWLEDGE_BASE_ATTR_LABEL, null);
        String description = configuration.getString(CFG_KNOWLEDGE_BASE_DESCRIPTION, null);
        String typeCode = configuration.getAttribute(CFG_KNOWLEDGE_BASE_ATTR_TYPE, null);

        List<Configuration> fileNodes = configuration.getConfigurationsAt(CFG_KNOWLEDGE_BASE_FILE);

        String kbClass = configuration.getAttribute(CFG_KNOWLEDGE_BASE_ATTR_CLASS, null);

        KnowledgeBase knowledgeBase = kbClass == null ? createScriptKnowledgeBaseFromConfiguration(name, typeCode, fileNodes)
                : createNonScriptKnowledgeBaseFromConfiguration(name, typeCode, kbClass, fileNodes);

        if (label != null) {
            knowledgeBase.setLabel(label);
        }

        if (description != null) {
            knowledgeBase.setDescription(description);
        }

        return knowledgeBase;
    }

    protected DefaultScriptKnowledgeBase createScriptKnowledgeBaseFromConfiguration(String name, String typeCode,
            List<Configuration> fileNodes) {

        List<KnowledgeBaseScript> scripts = new ArrayList<>();
        for (Configuration fileNode : fileNodes) {
            String filename = fileNode.getValue();

            if (StringUtils.isEmpty(filename)) {
                throw new SpongeException("Knowledge base filename must not be empty");
            }

            String charset = fileNode.getAttribute(CFG_KB_FILE_ATTR_CHARSET, null);

            scripts.add(new FileKnowledgeBaseScript(filename, charset != null ? Charset.forName(charset) : null,
                    fileNode.getBooleanAttribute(CFG_KB_FILE_ATTR_REQUIRED, FileKnowledgeBaseScript.DEFAULT_REQUIRED)));
        }

        DefaultScriptKnowledgeBase knowledgeBase;
        if (scripts.isEmpty()) {
            if (StringUtils.isEmpty(typeCode)) {
                throw new SpongeException("Knowledge base type for script knowledge bases with no files must not be empty");
            }

            knowledgeBase = new DefaultScriptKnowledgeBase(name, getKnowledgeBaseInterpreterFactory(typeCode).getSupportedType());
        } else {
            KnowledgeBaseType inferredKnowledgeBaseType = inferKnowledgeBaseType(name, scripts);
            if (!StringUtils.isEmpty(typeCode) && !inferredKnowledgeBaseType.getTypeCode().equals(typeCode)) {
                throw new SpongeException("The inferred knowledge base type '" + inferredKnowledgeBaseType.getTypeCode()
                        + "' is different that the specified '" + typeCode + "'");
            }

            knowledgeBase = new DefaultScriptKnowledgeBase(name, inferredKnowledgeBaseType);
        }

        scripts.forEach(script -> knowledgeBase.addScript(script));

        return knowledgeBase;
    }

    protected KnowledgeBase createNonScriptKnowledgeBaseFromConfiguration(String name, String typeCode, String kbClass,
            List<Configuration> fileNodes) {
        KnowledgeBase knowledgeBase = SpongeUtils.createInstance(kbClass, KnowledgeBase.class);

        if (typeCode != null) {
            KnowledgeBaseType type = getKnowledgeBaseInterpreterFactory(typeCode).getSupportedType();
            if (!Objects.equals(knowledgeBase.getType(), type)) {
                throw new SpongeException(
                        "The knowledge base class specifies type '" + knowledgeBase.getType() + "' but '" + type + "' is expected");
            }
        }

        if (!fileNodes.isEmpty()) {
            throw new SpongeException("Knowledge base files are not allowed for a non script knowledge base");
        }

        return knowledgeBase;
    }

    /**
     * Reloads script-based knowledge bases.
     */
    @Override
    public void reload() {
        onBeforeReload();

        // Reload script knowledge bases.
        knowledgeBases.values().forEach(knowledgeBase -> {
            if (knowledgeBase instanceof ScriptKnowledgeBase) {
                SpongeUtils.doInWrappedException(knowledgeBase, () -> ((ScriptKnowledgeBase) knowledgeBase).reload(), "reload");
            }
        });

        onLoad();

        onAfterReload();
    }

    /**
     * Starts up this managed entity.
     */
    @Override
    public void doStartup() {
        onInit();
        onLoad();
    }

    /**
     * Shuts down this managed entity.
     */
    @Override
    public void doShutdown() {
        onClear();

        knowledgeBases.clear();
    }

    @Override
    public void onInit() {
        onInitializeKnowledgeBases();
    }

    @Override
    public void onLoad() {
        // Before invoking onLoad callback method, scan to auto-enable processors if this functionality is turned on.
        if (getEngine().getConfigurationManager().getAutoEnable()) {
            knowledgeBases.values().forEach(kb -> SpongeUtils.doInWrappedException(kb, () -> kb.scanToAutoEnable(), "scanToAutoEnable"));
        }

        knowledgeBases.values().forEach(kb -> SpongeUtils.doInWrappedException(kb, () -> kb.onLoad(), "onLoad"));

    }

    @Override
    public void onClear() {
        knowledgeBases.values().forEach(kb -> SpongeUtils.doInWrappedException(kb, () -> kb.onClear(), "onClear"));
    }

    @Override
    public void onStartup() {
        knowledgeBases.values().forEach(kb -> SpongeUtils.doInWrappedException(kb, () -> kb.onStartup(), "onStartup"));
    }

    @Override
    public boolean onRun() {
        for (KnowledgeBase knowledgeBase : knowledgeBases.values()) {
            try {
                if (!knowledgeBase.onRun()) {
                    return false;
                }
            } catch (Throwable e) {
                throw SpongeUtils.wrapException("onRun", knowledgeBase.getInterpreter(), e);
            }
        }

        return true;
    }

    @Override
    public void onShutdown() {
        knowledgeBases.values().forEach(kb -> SpongeUtils.doInWrappedException(kb, () -> kb.onShutdown(), "onShutdown"));
    }

    @Override
    public void onBeforeReload() {
        knowledgeBases.values().forEach(kb -> SpongeUtils.doInWrappedException(kb, () -> kb.onBeforeReload(), "onBeforeReload"));
    }

    @Override
    public void onAfterReload() {
        knowledgeBases.values().forEach(kb -> SpongeUtils.doInWrappedException(kb, () -> kb.onAfterReload(), "onAfterReload"));
    }

    /**
     * Initialize knowledge bases.
     */
    protected void onInitializeKnowledgeBases() {
        for (KnowledgeBase knowledgeBase : knowledgeBases.values()) {
            if (knowledgeBase instanceof ScriptKnowledgeBase) {
                ScriptKnowledgeBase scriptKnowledgeBase = (ScriptKnowledgeBase) knowledgeBase;

                if (knowledgeBase.getType() == null) {
                    knowledgeBase.setType(inferKnowledgeBaseType(scriptKnowledgeBase.getName(), scriptKnowledgeBase.getScripts()));
                }

                verifyKnowledgeBaseFileTypes(scriptKnowledgeBase);
            }

            if (knowledgeBase.getInterpreter() == null) {
                knowledgeBase.setInterpreter(createKnowledgeBaseInterpreter(knowledgeBase.getType().getTypeCode(), knowledgeBase));
            }

            if (knowledgeBase instanceof ScriptKnowledgeBase) {
                SpongeUtils.doInWrappedException(knowledgeBase, () -> ((ScriptKnowledgeBase) knowledgeBase).load(), "load");
            }

            SpongeUtils.doInWrappedException(knowledgeBase, () -> knowledgeBase.onInit(), "onInit");
        }
    }

    @Override
    public void addKnowledgeBase(KnowledgeBase knowledgeBase) {
        if (knowledgeBases.containsKey(knowledgeBase.getName())) {
            throw new SpongeException("Knowledge base '" + knowledgeBase.getName() + "' already exists");
        }

        knowledgeBases.put(knowledgeBase.getName(), knowledgeBase);
    }

    @Override
    public Throwable unwrapKnowledgeBaseException(Throwable exception) {
        for (KnowledgeBase knowledgeBase : knowledgeBases.values()) {
            if (knowledgeBase instanceof ScriptKnowledgeBase) {
                KnowledgeBaseInterpreter interpreter = ((ScriptKnowledgeBase) knowledgeBase).getInterpreter();
                if (interpreter.isKnowledgeBaseException(exception)) {
                    return interpreter.getJavaException(exception);
                }
            }
        }

        return null;
    }

    @Override
    public void setKnowledgeBaseInterpreterFactoryProviders(List<KnowledgeBaseInterpreterFactoryProvider> providers) {
        providers.forEach(provider -> {
            KnowledgeBaseInterpreterFactory factory = provider.getKnowledgeBaseInterpreterFactory();
            knowledgeBaseInterpreterFactories.put(factory.getSupportedType().getTypeCode(), factory);
        });
    }

    protected KnowledgeBaseInterpreterFactory getKnowledgeBaseInterpreterFactory(String typeCode) {
        KnowledgeBaseInterpreterFactory factory = knowledgeBaseInterpreterFactories.get(typeCode);

        if (factory == null) {
            throw new SpongeException("No knowledge base interpreter factory registered for type code '" + typeCode + "'");
        }

        return factory;
    }

    public KnowledgeBaseInterpreter createKnowledgeBaseInterpreter(String typeCode, KnowledgeBase knowledgeBase) {
        return getKnowledgeBaseInterpreterFactory(typeCode).createKnowledgeBaseInterpreter(getEngine(), knowledgeBase);
    }

    @Override
    public KnowledgeBase getKnowledgeBase(String name) {
        KnowledgeBase result = knowledgeBases.get(name);
        if (result == null) {
            throw new IllegalArgumentException("Unknown knowledge base of name " + name);
        }

        return result;
    }

    @Override
    public ScriptKnowledgeBase getScriptKnowledgeBase(String name) {
        return (ScriptKnowledgeBase) getKnowledgeBase(name);
    }

    @Override
    public KnowledgeBase getMainKnowledgeBase() {
        return knowledgeBases.values().stream().filter(knowledgeBase -> knowledgeBase != defaultKnowledgeBase).findFirst().get();
    }

    @Override
    public List<KnowledgeBase> getKnowledgeBases() {
        return new ArrayList<>(knowledgeBases.values());
    }

    @Override
    public KnowledgeBase getDefaultKnowledgeBase() {
        return defaultKnowledgeBase;
    }

    @Override
    public void setInterpreterGlobalVariable(String name, Object value) {
        knowledgeBases.values().forEach(knowledgeBase -> knowledgeBase.getInterpreter().setVariable(name, value));
    }

    @Override
    public KnowledgeBaseScriptProvider<KnowledgeBaseScript> getKnowledgeBaseScriptProvider(KnowledgeBaseScript script) {
        return knowledgeBaseScriptProviderMapping.getProvider(script);
    }

    public KnowledgeBaseType inferKnowledgeBaseType(String knowledgeBaseName, List<KnowledgeBaseScript> scripts) {
        if (scripts.isEmpty()) {
            throw new SpongeException("Cannot infer knowledge base '" + knowledgeBaseName + "' type because it has no scripts");
        }

        // Find first file-based script.
        Optional<FileKnowledgeBaseScript> firstFileKnowledgeBaseScriptO =
                scripts.stream().filter(FileKnowledgeBaseScript.class::isInstance).map(FileKnowledgeBaseScript.class::cast).findFirst();

        if (!firstFileKnowledgeBaseScriptO.isPresent()) {
            throw new SpongeException("Cannot infer knowledge base '" + knowledgeBaseName + "' type because it has no file-based scripts");
        }

        String filename = firstFileKnowledgeBaseScriptO.get().getFilename();
        String extension = FilenameUtils.getExtension(filename);

        Optional<KnowledgeBaseType> typeO = knowledgeBaseInterpreterFactories.values().stream()
                .filter(factory -> factory.getSupportedType().getFileExtensions().stream()
                        .filter(ext -> StringUtils.equalsIgnoreCase(ext, extension)).findFirst().isPresent())
                .map(factory -> factory.getSupportedType()).findFirst();

        if (!typeO.isPresent()) {
            throw new SpongeException("Unsupported file extension '" + extension + "' for file '" + filename + "' in knowledge base '"
                    + knowledgeBaseName + "'");
        }

        return typeO.get();
    }

    public void verifyKnowledgeBaseFileTypes(ScriptKnowledgeBase scriptKnowledgeBase) {
        if (scriptKnowledgeBase.getScripts().isEmpty()) {
            return;
        }

        List<String> extensions = scriptKnowledgeBase.getType().getFileExtensions();

        // Checking only for file based scripts.
        if (!scriptKnowledgeBase.getScripts().stream().filter(FileKnowledgeBaseScript.class::isInstance)
                .map(FileKnowledgeBaseScript.class::cast)
                .allMatch(script -> extensions.stream()
                        .filter(ext -> StringUtils.equalsIgnoreCase(ext, FilenameUtils.getExtension(script.getFilename()))).findFirst()
                        .isPresent())) {
            logger.warn("Incompatible file extensions found for files in knowledge base '" + scriptKnowledgeBase.getName() + "'");
        }
    }
}
