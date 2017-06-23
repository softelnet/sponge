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

package org.openksavi.sponge.rule;

import org.openksavi.sponge.EventSetProcessorAdapter;

/**
 * Rule adapter.
 */
public interface RuleAdapter extends EventSetProcessorAdapter<Rule>, RuleOperations {

    /** Default event mode: first. */
    static final EventMode DEFAULT_MODE = EventMode.FIRST;

    /**
     * Returns event count.
     *
     * @return event count.
     */
    int getEventCount();
}
