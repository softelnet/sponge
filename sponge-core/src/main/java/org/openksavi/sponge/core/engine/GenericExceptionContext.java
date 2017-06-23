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

package org.openksavi.sponge.core.engine;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ExceptionContext;

/**
 * Exception context.
 */
public class GenericExceptionContext implements ExceptionContext {

    private Engine engine;

    private String sourceName;

    private Object sourceObject;

    public GenericExceptionContext(Engine engine, String sourceName) {
        this(engine, sourceName, null);
    }

    public GenericExceptionContext(Engine engine, String sourceName, Object sourceObject) {
        this.engine = engine;
        this.sourceName = sourceName;
        this.sourceObject = sourceObject;
    }

    @Override
    public Engine getEngine() {
        return engine;
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }

    @Override
    public Object getSourceObject() {
        return sourceObject;
    }
}
