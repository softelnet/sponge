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

package org.openksavi.sponge.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.kb.KnowledgeBaseReaderHolder;

public abstract class SpringUtils {

    private static final ResourcePatternResolver RESOURCE_RESOLVER =
            new PathMatchingResourcePatternResolver(SpringKnowledgeBaseFileProvider.class.getClassLoader());

    public static List<KnowledgeBaseReaderHolder> getReadersFromResourcePatternResolver(String fileName, Charset charset)
            throws IOException {
        return Arrays.stream(RESOURCE_RESOLVER.getResources(fileName)).filter(Resource::exists).map(resource -> {
            try {
                return new KnowledgeBaseReaderHolder(new InputStreamReader(resource.getInputStream()),
                        resource.getURL() != null ? resource.getURL().toString() : resource.toString());
            } catch (IOException e) {
                throw new ConfigException("Error reading " + resource, e);
            }
        }).collect(Collectors.toList());
    }

    protected SpringUtils() {
        //
    }
}
