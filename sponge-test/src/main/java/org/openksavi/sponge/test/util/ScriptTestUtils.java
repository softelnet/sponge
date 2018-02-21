/*
 * Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.test.util;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.engine.EngineBuilder;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseType;

/**
 * Script tests utility methods.
 */
public class ScriptTestUtils {

    public static String getConfigFileName(KnowledgeBaseType type, String config) {
        if (type.isScript()) {
            return "examples/script/" + type.getFileExtensions().get(0) + "/" + config + ".xml";
        } else {
            return "org/openksavi/sponge/" + type.getLanguage() + "/examples/" + config + ".xml";
        }
    }

    public static String getScriptKnowledgeBaseDir(KnowledgeBaseType type) {
        return "examples/script/" + type.getFileExtensions().get(0);
    }

    public static String getScriptKnowledgeBaseFileName(KnowledgeBaseType type, String knowledgeBaseFile) {
        return getScriptKnowledgeBaseDir(type) + "/" + knowledgeBaseFile + "." + type.getFileExtensions().get(0);
    }

    public static String getNoScriptKnowledgeBaseClassName(KnowledgeBaseType type, String knowledgeBaseSimpleClassNameUnderscore) {
        return "org.openksavi.sponge." + type.getLanguage() + ".examples."
                + SpongeUtils.toUpperCamelCaseFromUnderscore(knowledgeBaseSimpleClassNameUnderscore);
    }

    public static SpongeEngine buildWithKnowledgeBase(KnowledgeBaseType type, String knowledgeBaseFile) {
        EngineBuilder<DefaultSpongeEngine> builder = DefaultSpongeEngine.builder();
        if (type.isScript()) {
            builder.knowledgeBase(TestUtils.DEFAULT_KB, type, getScriptKnowledgeBaseFileName(type, knowledgeBaseFile));
        } else {
            builder.knowledgeBase(
                    SpongeUtils.createInstance(getNoScriptKnowledgeBaseClassName(type, knowledgeBaseFile), KnowledgeBase.class));
        }
        return builder.build();
    }

    public static SpongeEngine startWithKnowledgeBase(KnowledgeBaseType type, String knowledgeBaseFile) {
        SpongeEngine engine = buildWithKnowledgeBase(type, knowledgeBaseFile);
        engine.startup();
        return engine;
    }

    public static SpongeEngine startWithConfig(KnowledgeBaseType type, String config) {
        SpongeEngine engine = DefaultSpongeEngine.builder().config(getConfigFileName(type, config)).build();
        engine.startup();
        return engine;
    }
}
