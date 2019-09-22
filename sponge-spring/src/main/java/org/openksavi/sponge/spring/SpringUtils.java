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
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

import org.springframework.core.io.Resource;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.kb.KnowledgeBaseReaderHolder;

public abstract class SpringUtils {

    public static List<KnowledgeBaseReaderHolder> getReadersFromResourcePatternResolver(String filename, Charset charset)
            throws IOException {
        return new SpringKnowledgeBaseReaderResolver(filename, charset).resolve();
    }

    public static boolean isSparArchive(Resource resource) {
        try {
            return Objects.equals(resource.getURL().getProtocol(), "file") && (resource.getURL().getFile().endsWith(".jar")
                    || resource.getURL().getFile().contains(".jar" + SpringConstants.SPAR_CONTENTS_SEPARATOR));
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    protected SpringUtils() {
        //
    }
}
