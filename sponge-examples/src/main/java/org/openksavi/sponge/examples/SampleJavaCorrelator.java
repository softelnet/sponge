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

package org.openksavi.sponge.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.event.Event;

public class SampleJavaCorrelator extends org.openksavi.sponge.java.JavaCorrelator {

    private static final Logger logger = LoggerFactory.getLogger(SampleJavaCorrelator.class);

    private static final String VAR_INSTANCE_STARTED = SampleJavaCorrelator.class.getSimpleName() + "InstanceStarted";

    private List<Event> eventLog = new ArrayList<>();

    @Override
    public void configure() {
        setEventNames("filesystemFailure", "diskFailure");
    }

    @Override
    public void init() {
        getEPS().setVariableIfNone(VAR_INSTANCE_STARTED, () -> new AtomicBoolean(false));
    }

    @Override
    public boolean acceptsAsFirst(Event event) {
        return getEPS().getVariable(AtomicBoolean.class, VAR_INSTANCE_STARTED).compareAndSet(false, true);
    }

    @Override
    public void onEvent(Event event) {
        eventLog.add(event);
        logger.debug("{} - event: {}, log: {}", hashCode(), event.getName(), eventLog);
        getEPS().getVariable(AtomicInteger.class, "hardwareFailureJavaCount").incrementAndGet();
        if (eventLog.size() >= 4) {
            finish();
        }
    }
}
