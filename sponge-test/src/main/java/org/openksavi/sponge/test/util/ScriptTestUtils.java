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

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.core.engine.EngineBuilder;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseType;

/**
 * Script tests utility methods.
 */
public class ScriptTestUtils {

    public static String getConfigFileName(KnowledgeBaseType type, String config) {
        if (type.isScript()) {
            return "examples/script/" + type.getFileExtension() + "/" + config + ".xml";
        } else {
            return "org/openksavi/sponge/" + type.getLanguage() + "/examples/" + config + ".xml";
        }
    }

    public static String getScriptKnowledgeBaseDir(KnowledgeBaseType type) {
        return "examples/script/" + type.getFileExtension();
    }

    public static String getScriptKnowledgeBaseFileName(KnowledgeBaseType type, String knowledgeBaseFile) {
        return getScriptKnowledgeBaseDir(type) + "/" + knowledgeBaseFile + "." + type.getFileExtension();
    }

    public static String getNoScriptKnowledgeBaseClassName(KnowledgeBaseType type, String knowledgeBaseSimpleClassNameUnderscore) {
        return "org.openksavi.sponge." + type.getLanguage() + ".examples."
                + SpongeUtils.toUpperCamelCaseFromUnderscore(knowledgeBaseSimpleClassNameUnderscore);
    }

    public static Engine startWithKnowledgeBase(KnowledgeBaseType type, String knowledgeBaseFile) {
        EngineBuilder<DefaultEngine> builder = DefaultEngine.builder();
        if (type.isScript()) {
            builder.knowledgeBase(TestUtils.DEFAULT_KB, type, getScriptKnowledgeBaseFileName(type, knowledgeBaseFile));
        } else {
            builder.knowledgeBase(
                    SpongeUtils.createInstance(getNoScriptKnowledgeBaseClassName(type, knowledgeBaseFile), KnowledgeBase.class));
        }
        Engine engine = builder.build();
        engine.startup();
        return engine;
    }

    public static Engine startWithConfig(KnowledgeBaseType type, String config) {
        Engine engine = DefaultEngine.builder().config(getConfigFileName(type, config)).build();
        engine.startup();
        return engine;
    }
}
