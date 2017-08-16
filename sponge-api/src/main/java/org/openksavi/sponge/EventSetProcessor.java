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

package org.openksavi.sponge;

/**
 * Event set processor.
 */
public interface EventSetProcessor<T extends EventSetProcessorAdapter<?>> extends EventProcessor<T>, EventSetProcessorOperations {

    /**
     * Returns event set processor adapter group to which belong this event set processor instance.
     *
     * @return event set processor group.
     */
    EventSetProcessorAdapterGroup<EventSetProcessorAdapter<?>> getGroup();
}
