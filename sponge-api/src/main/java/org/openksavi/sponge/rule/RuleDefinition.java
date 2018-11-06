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

package org.openksavi.sponge.rule;

import java.util.List;

import org.openksavi.sponge.EventSetProcessorDefinition;

/**
 * Rule definition.
 */
public interface RuleDefinition extends EventSetProcessorDefinition, RuleOperations {

    /**
     * Returns the event specifications.
     *
     * @return the event specifications.
     */
    List<RuleEventSpec> getEventSpecs();

    /**
     * Returns the event specification for the specified event index.
     *
     * @param index event index.
     *
     * @return the event specification.
     */
    RuleEventSpec getEventSpec(int index);

    /**
     * Sets the event specifications.
     *
     * @param eventSpecs the event specifications.
     */
    void setEventSpecs(List<RuleEventSpec> eventSpecs);
}
