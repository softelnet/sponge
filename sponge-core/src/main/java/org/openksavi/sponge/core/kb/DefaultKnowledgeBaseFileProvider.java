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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBaseFileProvider;
import org.openksavi.sponge.kb.KnowledgeBaseReaderHolder;

/**
 * A default knowledge base file provider. Search order: relative to the current directory or classpath, in the XML configuration file
 * directory, in the Sponge home directory. Supports wildcards in filenames (not directories) according to the glob pattern.
 */
public class DefaultKnowledgeBaseFileProvider implements KnowledgeBaseFileProvider {

    @Override
    public List<KnowledgeBaseReaderHolder> getReaders(SpongeEngine engine, String filename, Charset charset) {
        Validate.notBlank(filename, "A filename or pattern cannot be empty");
        Path filePath = Validate.notNull(Paths.get(filename), "A file path is null");
        Validate.notBlank(filePath.getFileName() != null ? filePath.getFileName().toString() : null,
                "A filename or pattern cannot be empty");
        // Absolute path won't be looked in the config directory or the Sponge home (handled below) for wildcards.
        boolean isAbsolutePath = filePath.getParent() != null && filePath.getParent().isAbsolute();

        List<KnowledgeBaseReaderHolder> readers = null;

        // 1. Try to read relative to the current directory or classpath.
        // 1.1. As a non wildcard.
        if (!(readers = getNonWildcardReaders(null, filename, charset)).isEmpty()) {
            return readers;
        }

        // 1.2. As a wildcard.
        if (!(readers = getWildcardReaders(filePath.getParent(), filename, charset)).isEmpty()) {
            return readers;
        }

        // 2. Try to read in the XML configuration file directory.
        String configDir = SpongeUtils.getConfigurationFileDir(engine);
        if (configDir != null) {
            // 2.1. As a non wildcard.
            if (!(readers = getNonWildcardReaders(configDir, filename, charset)).isEmpty()) {
                return readers;
            }

            // 2.2. As a wildcard.
            if (!isAbsolutePath && !(readers = getWildcardReaders(
                    filePath.getParent() != null ? Paths.get(configDir, filePath.getParent().toString()) : Paths.get(configDir), filename,
                    charset)).isEmpty()) {
                return readers;
            }
        }

        // 3. Try to read in the Sponge home directory.
        String home = engine.getConfigurationManager().getHome();
        if (home != null) {
            // 3.1. As a non wildcard.
            if (!(readers = getNonWildcardReaders(home, filename, charset)).isEmpty()) {
                return readers;
            }

            // 3.2. As a wildcard.
            if (!isAbsolutePath && !(readers = getWildcardReaders(
                    filePath.getParent() != null ? Paths.get(home, filePath.getParent().toString()) : Paths.get(home), filename, charset))
                            .isEmpty()) {
                return readers;
            }
        }

        return Collections.emptyList();
    }

    protected List<KnowledgeBaseReaderHolder> getNonWildcardReaders(String dir, String filename, Charset charset) {
        String fullFilename = dir != null ? Paths.get(dir, filename).toString() : filename;
        Reader reader = SpongeUtils.getReader(fullFilename, charset);

        return reader != null ? Arrays.asList(new KnowledgeBaseReaderHolder(reader, fullFilename)) : Collections.emptyList();
    }

    protected List<KnowledgeBaseReaderHolder> getWildcardReaders(Path dir, String filename, Charset charset) {
        Path finalDir = dir != null ? dir : Paths.get(".");
        String filenamePattern = Paths.get(filename).getFileName().toString();
        List<Path> files = new ArrayList<>();

        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(finalDir, filenamePattern)) {
            dirStream.forEach(files::add);
        } catch (NotDirectoryException | NoSuchFileException e) {
            // Not directory or non-existing directory.
            return Collections.emptyList();
        } catch (IOException e) {
            throw new SpongeException("Error searching files in " + finalDir, e);
        }

        return files.stream().map(path -> new KnowledgeBaseReaderHolder(SpongeUtils.getReader(path.toString(), charset), path.toString()))
                .collect(Collectors.toList());
    }
}
