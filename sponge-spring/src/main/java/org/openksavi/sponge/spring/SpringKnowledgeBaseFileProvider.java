/*
 * Copyright 2016 Softelnet.
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

package org.openksavi.sponge.spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.springframework.util.ResourceUtils;

import org.openksavi.sponge.core.kb.DefaultKnowledgeBaseFileProvider;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBaseFileProvider;

/**
 * A knowledge base file provider using Spring {@code ResourceUtils}.
 */
public class SpringKnowledgeBaseFileProvider implements KnowledgeBaseFileProvider {

    private KnowledgeBaseFileProvider defaultKnowledgeBaseFileProvider = new DefaultKnowledgeBaseFileProvider();

    @Override
    public Reader getReader(SpongeEngine engine, String fileName, Charset charset) throws IOException {
        File file = null;
        try {
            file = ResourceUtils.getFile(fileName);
        } catch (FileNotFoundException e) {
            // Handled hereafter.
        }

        if (file != null && file.exists()) {
            return new InputStreamReader(new FileInputStream(file), charset);
        } else {
            Reader springReaolverReader = SpringUtils.getReaderFromResourcePatternResolver(fileName, charset);
            if (springReaolverReader != null) {
                return springReaolverReader;
            }

            return defaultKnowledgeBaseFileProvider.getReader(engine, fileName, charset);
        }
    }
}
