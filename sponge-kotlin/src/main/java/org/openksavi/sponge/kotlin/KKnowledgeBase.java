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

package org.openksavi.sponge.kotlin;

import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.JvmClassMappingKt;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.kb.BaseNonScriptKnowledgeBase;
import org.openksavi.sponge.kotlin.core.KotlinConstants;
import org.openksavi.sponge.kotlin.core.KotlinKnowledgeBaseEngineOperations;
import org.openksavi.sponge.kotlin.core.KotlinUtils;

/**
 * Abstract non script Kotlin knowledge base.
 */
public abstract class KKnowledgeBase extends BaseNonScriptKnowledgeBase {

    private static final Logger logger = LoggerFactory.getLogger(KKnowledgeBase.class);

    protected KKnowledgeBase(String name) {
        super(name, KotlinConstants.TYPE_NON_SCRIPT);
    }

    protected KKnowledgeBase() {
        super(KotlinConstants.TYPE_NON_SCRIPT);
    }

    @Override
    public KotlinKnowledgeBaseEngineOperations getEngineOperations() {
        return (KotlinKnowledgeBaseEngineOperations) super.getEngineOperations();
    }

    @Override
    public KotlinKnowledgeBaseEngineOperations getEps() {
        return getEngineOperations();
    }

    @Override
    public void scanToAutoEnable() {
        List<String> autoEnabled = new ArrayList<>();
        JvmClassMappingKt.getKotlinClass(getClass()).getNestedClasses().stream().filter(kclass -> !kclass.isAbstract())
                .forEachOrdered(kclass -> {
                    if (KotlinConstants.PROCESSOR_CLASSES.values().stream()
                            .filter(processorClass -> ClassUtils.isAssignable(JvmClassMappingKt.getJavaClass(kclass), processorClass))
                            .findFirst().isPresent()) {
                        autoEnabled.add(KotlinUtils.createProcessorName(kclass));
                        getEngineOperations().enable(kclass);
                    }
                });

        if (logger.isDebugEnabled() && !autoEnabled.isEmpty()) {
            logger.debug("Auto-enabling: {}", autoEnabled);
        }
    }
}
