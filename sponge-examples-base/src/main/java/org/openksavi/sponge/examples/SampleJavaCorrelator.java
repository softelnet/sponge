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

package org.openksavi.sponge.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JCorrelator;

public class SampleJavaCorrelator extends JCorrelator {

    private List<Event> eventLog;

    @Override
    public void onConfigure() {
        withEvents("filesystemFailure", "diskFailure");
        withMaxInstances(1);
    }

    @Override
    public boolean onAcceptAsFirst(Event event) {
        return event.getName().equals("filesystemFailure");
    }

    @Override
    public void onInit() {
        eventLog = new ArrayList<>();
    }

    @Override
    public void onEvent(Event event) {
        eventLog.add(event);
        getLogger().debug("{} - event: {}, log: {}", hashCode(), event.getName(), eventLog);
        getSponge().getVariable(AtomicInteger.class, "hardwareFailureJavaCount").incrementAndGet();
        if (eventLog.size() >= 4) {
            getSponge().getVariable(AtomicInteger.class, "hardwareFailureJavaFinishCount").incrementAndGet();
            finish();
        }
    }
}
