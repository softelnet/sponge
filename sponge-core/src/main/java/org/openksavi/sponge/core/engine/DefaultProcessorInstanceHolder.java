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

package org.openksavi.sponge.core.engine;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.engine.ProcessorInstanceHolder;

public class DefaultProcessorInstanceHolder implements ProcessorInstanceHolder {

    private Processor<?> processor;

    private String name;

    private boolean javaDefined;

    public DefaultProcessorInstanceHolder(Processor<?> processor, String name, boolean javaDefined) {
        this.processor = processor;
        this.name = name;
        this.javaDefined = javaDefined;
    }

    @Override
    public Processor<?> getProcessor() {
        return processor;
    }

    public void setProcessor(Processor<?> processor) {
        this.processor = processor;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isJavaDefined() {
        return javaDefined;
    }

    public void setJavaDefined(boolean javaDefined) {
        this.javaDefined = javaDefined;
    }
}
