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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBaseFileProvider;

/**
 * A default knowledge base file provider. Search order: relative to the current directory or classpath, in the XML configuration file
 * directory, in the Sponge home directory.
 */
public class DefaultKnowledgeBaseFileProvider implements KnowledgeBaseFileProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultKnowledgeBaseFileProvider.class);

    @Override
    public Reader getReader(SpongeEngine engine, String fileName, Charset charset) throws IOException {
        // Try to read relative to the current directory or classpath.
        Reader reader = SpongeUtils.getReader(fileName, charset);

        // Try to read in the XML configuration file directory.
        if (reader == null) {
            URL configurationFileUrl = engine.getConfigurationManager().getConfigurationFileUrl();
            if (configurationFileUrl != null) {
                File configFile = FileLocatorUtils.fileFromURL(configurationFileUrl);
                if (configFile != null) {
                    String configDir = configFile.getParent();
                    if (configDir != null) {
                        reader = SpongeUtils.getReader(Paths.get(configDir, fileName).toString(), charset);
                    }
                } else {
                    logger.warn("Configuration file URL {} cannot be converted to File", configurationFileUrl);
                }
            }
        }

        // Try to read in the Sponge home directory.
        if (reader == null) {
            String home = engine.getConfigurationManager().getHome();
            if (home != null) {
                reader = SpongeUtils.getReader(Paths.get(home, fileName).toString(), charset);
            }
        }

        return reader;
    }
}
