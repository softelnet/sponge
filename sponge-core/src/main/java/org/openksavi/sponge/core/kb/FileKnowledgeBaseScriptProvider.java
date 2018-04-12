/*
 * Copyright 2016-2018 The Sponge authors.
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

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.engine.SpongeEngine;

/**
 * A file-based knowledge base script provider.
 */
public class FileKnowledgeBaseScriptProvider extends BaseKnowledgeBaseScriptProvider<FileKnowledgeBaseScript> {

    private static final Logger logger = LoggerFactory.getLogger(FileKnowledgeBaseScriptProvider.class);

    public FileKnowledgeBaseScriptProvider(SpongeEngine engine, FileKnowledgeBaseScript script) {
        super(engine, script);
    }

    @Override
    public Reader getReader() throws IOException {
        Charset charset = script.getCharset() != null ? script.getCharset() : Charset.defaultCharset();
        Reader reader = engine.getKnowledgeBaseFileProvider().getReader(engine, script.getFileName(), charset);

        if (reader == null) {
            if (script.isRequired()) {
                throw new ConfigException("Knowledge base file " + script.getFileName() + " not found");
            } else {
                logger.warn("Knowledge base file " + script.getFileName() + " not found but is set as optional.");
            }
        }

        return reader;
    }
}
