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

package org.openksavi.sponge.core.kb;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBaseReaderHolder;

/**
 * A String-based knowledge base script provider.
 */
public class StringKnowledgeBaseScriptProvider extends BaseKnowledgeBaseScriptProvider<StringKnowledgeBaseScript> {

    public StringKnowledgeBaseScriptProvider(SpongeEngine engine, StringKnowledgeBaseScript script) {
        super(engine, script);
    }

    @Override
    public List<KnowledgeBaseReaderHolder> getReaders() throws IOException {
        return Arrays.asList(new KnowledgeBaseReaderHolder(new StringReader(script.getBody().toString()), script.getName()));
    }
}
