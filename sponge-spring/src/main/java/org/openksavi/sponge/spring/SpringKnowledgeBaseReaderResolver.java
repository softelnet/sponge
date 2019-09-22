/*
 * Copyright 2016-2019 The Sponge authors.
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.core.io.Resource;

import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.kb.KnowledgeBaseReaderHolder;

/**
 * A Spring based knowledge base reader resolver.
 */
public class SpringKnowledgeBaseReaderResolver {

    private static final SpringResourcePatternResolver RESOURCE_RESOLVER =
            new SpringResourcePatternResolver(SpringUtils.class.getClassLoader());

    private String filename;

    private Charset charset;

    public SpringKnowledgeBaseReaderResolver(String filename, Charset charset) {
        this.filename = filename;
        this.charset = charset;
    }

    public List<KnowledgeBaseReaderHolder> resolve() throws IOException {
        Resource[] resources = null;

        try {
            resources = RESOURCE_RESOLVER.getResources(filename);
        } catch (FileNotFoundException e) {
            return Collections.emptyList();
        }

        // Cache SPAR JAR files to read all knowledge base files from one archive without closing the archive.
        Map<String, JarFile> sparJarFileCache = new LinkedHashMap<>();

        try {
            return Arrays.stream(resources).map(resource -> {
                try {
                    InputStream is = null;
                    if (resource.exists()) {
                        is = resource.getInputStream();
                    } else {
                        is = tryCreateSparEntryInputStream(resource, sparJarFileCache);
                    }

                    return is != null ? new KnowledgeBaseReaderHolder(new InputStreamReader(is, charset),
                            resource.getURL() != null ? resource.getURL().toString() : resource.toString()) : null;
                } catch (IOException e) {
                    throw new ConfigException("Error reading " + resource, e);
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } finally {
            sparJarFileCache.values().forEach(jarFile -> {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    throw SpongeUtils.wrapException(e);
                }
            });
        }
    }

    protected InputStream tryCreateSparEntryInputStream(Resource resource, Map<String, JarFile> sparJarFileCache) throws IOException {
        if (!resource.isFile()) {
            return null;
        }

        String spec = resource.getFile().getCanonicalPath();

        int sparSeparatorIndex = spec.lastIndexOf(SpringConstants.SPAR_CONTENTS_SEPARATOR);
        if (sparSeparatorIndex < 0) {
            return null;
        }

        String archive = spec.substring(0, sparSeparatorIndex);

        if (!sparJarFileCache.containsKey(archive)) {
            File archiveFile = new File(archive);
            if (!archiveFile.isFile()) {
                return null;
            }

            sparJarFileCache.put(archive, new JarFile(archiveFile));
        }

        JarFile jar = sparJarFileCache.get(archive);

        String kbFile = spec.substring(archive.length() + SpringConstants.SPAR_CONTENTS_SEPARATOR.length());

        ZipEntry entry = Validate.notNull(jar.getEntry(kbFile), "The JAR entry %s not found", kbFile);

        // Read all knowledge base file contents to the memory.
        return new ByteArrayInputStream(IOUtils.readFully(jar.getInputStream(entry), (int) entry.getSize()));
    }
}
