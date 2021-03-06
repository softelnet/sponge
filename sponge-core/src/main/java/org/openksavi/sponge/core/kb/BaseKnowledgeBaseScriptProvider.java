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

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBaseScript;
import org.openksavi.sponge.kb.KnowledgeBaseScriptProvider;

/**
 * A base knowledge base script provider.
 */
public abstract class BaseKnowledgeBaseScriptProvider<T extends KnowledgeBaseScript> implements KnowledgeBaseScriptProvider<T> {

    protected SpongeEngine engine;

    protected T script;

    public BaseKnowledgeBaseScriptProvider(SpongeEngine engine, T script) {
        this.engine = engine;
        this.script = script;
    }

    public SpongeEngine getEngine() {
        return engine;
    }

    public T getScript() {
        return script;
    }
}
