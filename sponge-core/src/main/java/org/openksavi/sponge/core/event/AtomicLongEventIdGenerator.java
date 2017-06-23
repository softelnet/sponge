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

package org.openksavi.sponge.core.event;

import java.util.concurrent.atomic.AtomicLong;

import org.openksavi.sponge.event.EventIdGenerator;

/**
 * Event ID generator implementation.
 */
public class AtomicLongEventIdGenerator implements EventIdGenerator {

    private long baseTimestamp = System.currentTimeMillis();

    private AtomicLong lastId = new AtomicLong(0);

    @Override
    public String getNext() {
        return new EventId(baseTimestamp, lastId.incrementAndGet()).toString();
    }

    public long getBaseTimestamp() {
        return baseTimestamp;
    }

    public AtomicLong getLastId() {
        return lastId;
    }
}
