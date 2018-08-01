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

import kotlin.jvm.JvmClassMappingKt;

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

    /**
     * Method required for accessing the engine operations in Kotlin-based processors.
     *
     * @return the engine operations.
     */
    @Override
    public final KotlinKnowledgeBaseEngineOperations getSponge() {
        return getEngineOperations();
    }

    @Override
    public void scanToAutoEnable() {
        KotlinUtils.scanNestedToAutoEnable(JvmClassMappingKt.getKotlinClass(getClass()), getEngineOperations(), logger);
    }
}
