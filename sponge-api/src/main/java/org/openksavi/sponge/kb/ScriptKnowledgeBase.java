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

package org.openksavi.sponge.kb;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Script based knowledge base.
 */
public interface ScriptKnowledgeBase extends KnowledgeBase {

    /**
     * Returns knowledge base scripts.
     *
     * @return knowledge base scripts.
     */
    List<KnowledgeBaseScript> getScripts();

    /**
     * Adds a new knowledge base script.
     *
     * @param script knowledge base script.
     */
    void addScript(KnowledgeBaseScript script);

    /**
     * Loads a knowledge base from scripts.
     */
    void load();

    /**
     * Loads the knowledge base from the file.
     *
     * @param fileName file name.
     */
    void load(String fileName);

    /**
     * Loads the knowledge base from the file.
     *
     * @param fileName file name.
     * @param charset charset.
     */
    void load(String fileName, String charset);

    /**
     * Loads the knowledge base from the file.
     *
     * @param fileName file name.
     * @param charset charset.
     */
    void load(String fileName, Charset charset);

    /**
     * Reloads the knowledge base from scripts.
     */
    void reload();

    /**
     * Returns a script interpreter associated with this knowledge base.
     */
    @Override
    ScriptKnowledgeBaseInterpreter getInterpreter();
}
