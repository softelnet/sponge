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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.core.action.BaseAction;
import org.openksavi.sponge.kotlin.core.KotlinKnowledgeBaseEngineOperations;

/**
 * Kotlin-specific implementation of the action.
 */
public abstract class KAction extends BaseAction {

    /**
     * Method required for accessing the engine operations in Kotlin-based processors.
     *
     * @return the engine operations.
     */
    @Override
    public final KotlinKnowledgeBaseEngineOperations getSponge() {
        return (KotlinKnowledgeBaseEngineOperations) super.getSponge();
    }

    @Override
    public final KAction withName(String name) {
        return (KAction) super.withName(name);
    }

    @Override
    public final KAction withLabel(String label) {
        return (KAction) super.withLabel(label);
    }

    @Override
    public final KAction withDescription(String description) {
        return (KAction) super.withDescription(description);
    }

    @Override
    public final KAction withVersion(Integer version) {
        return (KAction) super.withVersion(version);
    }

    @Override
    public final KAction withFeatures(Map<String, Object> features) {
        return (KAction) super.withFeatures(features);
    }

    @Override
    public final KAction withFeature(String name, Object value) {
        return (KAction) super.withFeature(name, value);
    }

    @Override
    public final KAction withCategory(String category) {
        return (KAction) super.withCategory(category);
    }

    @Override
    public final KAction withArgs(List<ArgMeta<?>> argsMeta) {
        return (KAction) super.withArgs(argsMeta);
    }

    public final KAction withArgs(ArgMeta<?>... argsMeta) {
        return withArgs(Arrays.asList(argsMeta));
    }

    @Override
    public final KAction withArg(ArgMeta<?> argMeta) {
        return (KAction) super.withArg(argMeta);
    }

    @Override
    public final KAction withNoArgs() {
        return (KAction) super.withNoArgs();
    }

    @Override
    public final KAction withResult(ResultMeta<?> resultMeta) {
        return (KAction) super.withResult(resultMeta);
    }

    @Override
    public final KAction withNoResult() {
        return (KAction) super.withNoResult();
    }
}
