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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.kb.KnowledgeBaseScript;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.kb.ScriptKnowledgeBase;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;

/**
 * Script based knowledge base.
 */
public class DefaultScriptKnowledgeBase extends BaseKnowledgeBase implements ScriptKnowledgeBase {

    /** Knowledge base scripts. */
    private List<KnowledgeBaseScript> scripts = Collections.synchronizedList(new ArrayList<>());

    public DefaultScriptKnowledgeBase(String name, KnowledgeBaseType type) {
        setName(name);
        setType(type);
    }

    public DefaultScriptKnowledgeBase(String name) {
        setName(name);
    }

    @Override
    public List<KnowledgeBaseScript> getScripts() {
        return scripts;
    }

    @Override
    public void addScript(KnowledgeBaseScript script) {
        if (script.getKnowledgeBase() != null && !Objects.equals(script.getKnowledgeBase(), this)) {
            throw new SpongeException("A different knowledge base is already assigned to this script instance");
        }

        script.setKnowledgeBase(this);
        scripts.add(script);
    }

    @Override
    public ScriptKnowledgeBaseInterpreter getInterpreter() {
        return (ScriptKnowledgeBaseInterpreter) super.getInterpreter();
    }

    @Override
    public final void onInit() {
        getInterpreter().onInit();
    }

    @Override
    public final void onLoad() {
        getInterpreter().onLoad();
    }

    /**
     * Clears the knowledge base state.
     */
    @Override
    public final void onClear() {
        getInterpreter().onClear();
    }

    /**
     * Calls onStartup method in the knowledge base. The implementation could for example send events, etc.
     */
    @Override
    public final void onStartup() {
        getInterpreter().onStartup();
    }

    /**
     * Calls onShutdown method in the knowledge base.
     */
    @Override
    public final void onShutdown() {
        getInterpreter().onShutdown();
    }

    /**
     * Calls onBeforeReload method in the knowledge base.
     */
    @Override
    public final void onBeforeReload() {
        getInterpreter().onBeforeReload();
    }

    /**
     * Calls onAfterReload method in the knowledge base.
     */
    @Override
    public final void onAfterReload() {
        getInterpreter().onAfterReload();
    }

    @Override
    public void load() {
        getInterpreter().load(scripts);
    }

    @Override
    public void reload() {
        getInterpreter().reload(scripts);
    }

    @Override
    public void load(String fileName) {
        getInterpreter().load(fileName);
    }

    @Override
    public void load(String fileName, String charset) {
        getInterpreter().load(fileName, charset);
    }

    @Override
    public void load(String fileName, Charset charset) {
        getInterpreter().load(fileName, charset);
    }
}