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

package org.openksavi.sponge.groovy;

import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;

/**
 * Groovy-specific implementation of the correlator.
 */
public abstract class GroovyCorrelator extends org.openksavi.sponge.core.correlator.BaseCorrelator {

    /**
     * Method required for accessing EPS in Groovy-based processors.
     *
     * @return EPS.
     */
    public final KnowledgeBaseEngineOperations getEPS() {
        return getEps();
    }
}
