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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.springframework.util.ResourceUtils;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.kb.KnowledgeBaseFileProvider;

/**
 * A knowledge base file provider using Spring {@code ResourceUtils}.
 */
public class SpringKnowledgeBaseFileProvider implements KnowledgeBaseFileProvider {

    @Override
    public Reader getReader(Engine engine, String fileName, Charset charset) throws IOException {
        return new InputStreamReader(new FileInputStream(ResourceUtils.getFile(fileName)), charset);
    }
}
