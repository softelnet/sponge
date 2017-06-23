/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.nashorn.spi;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseInterpreter;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.nashorn.JavaScriptConstants;
import org.openksavi.sponge.nashorn.core.NashornKnowledgeBaseInterpreter;
import org.openksavi.sponge.spi.KnowledgeBaseInterpreterFactory;

/**
 * Jython-based knowledge base interpreter factory.
 */
public class NashornKnowledgeBaseInterpreterFactory implements KnowledgeBaseInterpreterFactory {

    @Override
    public KnowledgeBaseType getSupportedType() {
        return JavaScriptConstants.TYPE;
    }

    @Override
    public KnowledgeBaseInterpreter createKnowledgeBaseInterpreter(Engine engine, KnowledgeBase knowledgeBase) {
        return new NashornKnowledgeBaseInterpreter(engine, knowledgeBase);
    }
}
