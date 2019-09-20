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

import java.io.IOException;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;

/**
 * An extension to Spring PathMatchingResourcePatternResolver that supports the Sponge spar: protocol for loading knowledge base files from
 * JAR archives.
 */
public class SpringResourcePatternResolver extends PathMatchingResourcePatternResolver {

    private static final ResourcePatternResolver BASE_RESOURCE_RESOLVER =
            new PathMatchingResourcePatternResolver(SpringKnowledgeBaseFileProvider.class.getClassLoader());

    public SpringResourcePatternResolver(@Nullable ClassLoader classLoader) {
        super(classLoader);

        ((DefaultResourceLoader) getResourceLoader()).addProtocolResolver((String location, ResourceLoader resourceLoader) -> {
            if (!location.startsWith(SpringConstants.URL_PROTOCOL_SPAR_WITH_SEPARATOR)) {
                return null;
            }

            if (location.contains(".jar" + SpringConstants.SPAR_CONTENTS_SEPARATOR)) {
                // Resolve a JAR archive as a file.
                return BASE_RESOURCE_RESOLVER
                        .getResource("file:" + location.substring(SpringConstants.URL_PROTOCOL_SPAR_WITH_SEPARATOR.length()));
            }

            return null;
        });
    }

    @Override
    protected boolean isJarResource(Resource resource) throws IOException {
        if (SpringUtils.isSparArchive(resource)) {
            return true;
        }

        return super.isJarResource(resource);
    }
}
