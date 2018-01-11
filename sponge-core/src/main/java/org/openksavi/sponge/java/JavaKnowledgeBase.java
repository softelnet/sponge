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

package org.openksavi.sponge.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.Processor;
import org.openksavi.sponge.core.kb.BaseNonScriptKnowledgeBase;
import org.openksavi.sponge.core.util.SpongeUtils;

/**
 * Abstract Java knowledge base.
 */
public abstract class JavaKnowledgeBase extends BaseNonScriptKnowledgeBase {

    private static final Logger logger = LoggerFactory.getLogger(JavaKnowledgeBase.class);

    protected JavaKnowledgeBase(String name) {
        super(name, JavaConstants.TYPE);
    }

    protected JavaKnowledgeBase() {
        super(JavaConstants.TYPE);
    }

    @Override
    public JavaKnowledgeBaseEngineOperations getEngineOperations() {
        return (JavaKnowledgeBaseEngineOperations) super.getEngineOperations();
    }

    @Override
    public JavaKnowledgeBaseEngineOperations getEps() {
        return getEngineOperations();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void scanToAutoEnable() {
        List<String> autoEnabled = new ArrayList<>();
        Arrays.stream(getClass().getDeclaredClasses()).filter(cls -> !SpongeUtils.isAbstract(cls)).forEachOrdered(cls -> {
            if (JavaConstants.PROCESSOR_CLASSES.values().stream().filter(processorClass -> ClassUtils.isAssignable(cls, processorClass))
                    .findFirst().isPresent()) {
                autoEnabled.add(cls.getName());
                getEngineOperations().enableJava((Class<? extends Processor>) cls);
            }
        });

        if (logger.isDebugEnabled() && !autoEnabled.isEmpty()) {
            logger.debug("Auto-enabling: {}", autoEnabled);
        }
    }
}
