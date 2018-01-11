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

import org.openksavi.sponge.kb.KnowledgeBaseType;

/**
 * Non script based knowledge base.
 */
public abstract class BaseNonScriptKnowledgeBase extends BaseKnowledgeBase {

    public BaseNonScriptKnowledgeBase(String name, KnowledgeBaseType type) {
        setName(name);
        setType(type);
    }

    public BaseNonScriptKnowledgeBase(KnowledgeBaseType type) {
        setName(getClass().getName());
        setType(type);
    }

    @Override
    public void onInit() {
        //
    }

    @Override
    public void onClear() {
        //
    }

    @Override
    public void onLoad() {
        //
    }

    @Override
    public void onStartup() {
        //
    }

    @Override
    public void onShutdown() {
        //
    }

    @Override
    public void onBeforeReload() {
        //
    }

    @Override
    public void onAfterReload() {
        //
    }
}
