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

import org.openksavi.sponge.core.trigger.BaseTrigger;
import org.openksavi.sponge.kotlin.core.KotlinKnowledgeBaseEngineOperations;

/**
 * Kotlin-specific implementation of the trigger.
 */
public abstract class KTrigger extends BaseTrigger {

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
    public final KTrigger withName(String name) {
        return (KTrigger) super.withName(name);
    }

    @Override
    public final KTrigger withLabel(String label) {
        return (KTrigger) super.withLabel(label);
    }

    @Override
    public final KTrigger withDescription(String description) {
        return (KTrigger) super.withDescription(description);
    }

    @Override
    public final KTrigger withVersion(Integer version) {
        return (KTrigger) super.withVersion(version);
    }

    @Override
    public final KTrigger withFeatures(Map<String, Object> features) {
        return (KTrigger) super.withFeatures(features);
    }

    @Override
    public final KTrigger withFeature(String name, Object value) {
        return (KTrigger) super.withFeature(name, value);
    }

    @Override
    public final KTrigger withCategory(String category) {
        return (KTrigger) super.withCategory(category);
    }

    @Override
    public final KTrigger withEvents(List<String> eventNames) {
        return (KTrigger) super.withEvents(eventNames);
    }

    public final KTrigger withEvents(String... events) {
        return withEvents(Arrays.asList(events));
    }

    @Override
    public final KTrigger withEvent(String eventName) {
        return (KTrigger) super.withEvent(eventName);
    }
}
