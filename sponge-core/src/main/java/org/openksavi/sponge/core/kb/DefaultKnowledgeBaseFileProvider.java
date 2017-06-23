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

package org.openksavi.sponge.core.kb;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.kb.KnowledgeBaseFileProvider;

/**
 * A default knowledge base file provider.
 */
public class DefaultKnowledgeBaseFileProvider implements KnowledgeBaseFileProvider {

    @Override
    public Reader getReader(Engine engine, String fileName, Charset charset) throws IOException {
        Reader reader = Utils.getReader(fileName, charset);

        if (reader == null) {
            String home = engine.getConfigurationManager().getHome();
            if (home != null) {
                reader = Utils.getReader(Paths.get(home, fileName).toString(), charset);
            }
        }

        if (reader == null) {
            throw new ConfigException(fileName + " not found");
        }

        return reader;
    }
}
