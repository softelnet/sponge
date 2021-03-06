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

package org.openksavi.sponge.core.kb;

import org.slf4j.Logger;

import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
import org.openksavi.sponge.kb.KnowledgeBaseInterpreter;
import org.openksavi.sponge.kb.KnowledgeBaseType;

/**
 * Base knowledge base.
 */
public abstract class BaseKnowledgeBase implements KnowledgeBase {

    private KnowledgeBaseInterpreter interpreter;

    private String name;

    private String label;

    private String description;

    private KnowledgeBaseType type;

    private Integer version;

    protected BaseKnowledgeBase() {
        //
    }

    @Override
    public KnowledgeBaseEngineOperations getEngineOperations() {
        return interpreter.getEngineOperations();
    }

    /**
     * Shortcut method for using in the knowledge bases. Returns the same result as {@link #getEngineOperations()}.
     *
     * @return engine operations.
     */
    public KnowledgeBaseEngineOperations getSponge() {
        return getEngineOperations();
    }

    @Override
    public KnowledgeBaseInterpreter getInterpreter() {
        return interpreter;
    }

    @Override
    public void setInterpreter(KnowledgeBaseInterpreter knowledgeBaseInterpreter) {
        interpreter = knowledgeBaseInterpreter;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public KnowledgeBaseType getType() {
        return type;
    }

    @Override
    public void setType(KnowledgeBaseType type) {
        this.type = type;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    public Logger getLogger() {
        return ((BaseKnowledgeBaseInterpreter) interpreter).getLogger();
    }
}
