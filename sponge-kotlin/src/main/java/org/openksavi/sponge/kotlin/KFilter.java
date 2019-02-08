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

import org.openksavi.sponge.core.filter.BaseFilter;
import org.openksavi.sponge.kotlin.core.KotlinKnowledgeBaseEngineOperations;

/**
 * Kotlin-specific implementation of the filter.
 */
public abstract class KFilter extends BaseFilter {

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
    public final KFilter withName(String name) {
        return (KFilter) super.withName(name);
    }

    @Override
    public final KFilter withLabel(String label) {
        return (KFilter) super.withLabel(label);
    }

    @Override
    public final KFilter withDescription(String description) {
        return (KFilter) super.withDescription(description);
    }

    @Override
    public final KFilter withVersion(Integer version) {
        return (KFilter) super.withVersion(version);
    }

    @Override
    public final KFilter withFeatures(Map<String, Object> features) {
        return (KFilter) super.withFeatures(features);
    }

    @Override
    public final KFilter withFeature(String name, Object value) {
        return (KFilter) super.withFeature(name, value);
    }

    @Override
    public final KFilter withCategory(String category) {
        return (KFilter) super.withCategory(category);
    }

    @Override
    public final KFilter withEvents(List<String> eventNames) {
        return (KFilter) super.withEvents(eventNames);
    }

    public final KFilter withEvents(String... events) {
        return withEvents(Arrays.asList(events));
    }

    @Override
    public final KFilter withEvent(String eventName) {
        return (KFilter) super.withEvent(eventName);
    }
}
