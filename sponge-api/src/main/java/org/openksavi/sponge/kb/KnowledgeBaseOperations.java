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

package org.openksavi.sponge.kb;

/**
 * Knowledge base operations.
 */
public interface KnowledgeBaseOperations {

    /**
     * Calls onInit method in the knowledge base. The implementation could for example initialize variables, etc.
     */
    void onInit();

    /**
     * Calls onLoad method in the knowledge base. The implementation could for example manually enable filters, triggers, etc.
     */
    void onLoad();

    /**
     * Calls onStartup method in the knowledge base. An implementation could for example send events, etc.
     */
    void onStartup();

    /**
     * Called just after onStartup. If this function returns {@code true} for every knowledge base, then the engine starts its threads and
     * performs an endless loop in order to process events. Otherwise, the engine will invoke a shutdown without starting an event
     * processing.
     *
     * @return {@code true} if the engine should start event processing.
     */
    boolean onRun();

    /**
     * Calls onClear method in the knowledge base.
     */
    void onClear();

    /**
     * Calls onBeforeReload method in the knowledge base.
     */
    void onBeforeReload();

    /**
     * Calls onAfterReload method in the knowledge base.
     */
    void onAfterReload();

    /**
     * Calls onShutdown method in the knowledge base.
     */
    void onShutdown();
}
