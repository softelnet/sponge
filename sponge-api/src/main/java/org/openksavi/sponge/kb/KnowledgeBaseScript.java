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

package org.openksavi.sponge.kb;

/**
 * Knowledge base script.
 */
public interface KnowledgeBaseScript {

    /**
     * Returns a knowledge base that uses this script.
     *
     * @return a knowledge base.
     */
    ScriptKnowledgeBase getKnowledgeBase();

    /**
     * Sets a knowledge base that uses this script.
     *
     * @param knowledgeBase
     *            a knowledge base.
     */
    void setKnowledgeBase(ScriptKnowledgeBase knowledgeBase);

    /**
     * Returns the filename.
     *
     * @return the filename.
     */
    String getFileName();

    /**
     * Sets the filename
     *
     * @param fileName
     *            the filename
     */
    void setFileName(String fileName);

    /**
     * Returns the charset.
     *
     * @return the charset.
     */
    String getCharset();

    /**
     * Sets the charset.
     *
     * @param charset
     *            the charset
     */
    void setCharset(String charset);
}
