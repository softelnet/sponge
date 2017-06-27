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

import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.core.event.AtomicLongEventIdGenerator;
import org.openksavi.sponge.core.event.AttributeMapEvent;
import org.openksavi.sponge.core.event.BaseControlEvent;
import org.openksavi.sponge.core.event.EventId;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;

public class PriorityEventQueueTest {

    private AtomicLongEventIdGenerator idGenerator = new AtomicLongEventIdGenerator();

    private DefaultEngine engine;

    @Before
    public void beforeTest() {
        engine = new DefaultEngine();
        engine.init();
    }

    @SuppressWarnings("serial")
    private abstract static class TestControlEvent extends BaseControlEvent {

        protected TestControlEvent(String name, EventClonePolicy clonePolicy) {
            super(name, clonePolicy);
        }
    }

    private Event event(int id, int priority) {
        String eventName = "e" + id;

        EventClonePolicy eventClonePolicy = engine.getDefaultParameters().getEventClonePolicy();
        Event event = (priority == AttributeMapEvent.DEFAULT_PRIORITY) ? new AttributeMapEvent(eventName, eventClonePolicy)
                : mock(TestControlEvent.class, withSettings().useConstructor(eventName, eventClonePolicy).defaultAnswer(CALLS_REAL_METHODS))
                        .setPriority(priority);

        event.setId(new EventId(idGenerator.getBaseTimestamp(), id).toString());

        return event;
    }

    private Event addEvent(Queue<Event> queue, int id, int priority) {
        Event event = event(id, priority);
        queue.add(event);
        return event;
    }

    @Test
    public void testPriorityBlockingQueue() throws InterruptedException {
        PriorityBlockingQueue<Event> queue = new PriorityBlockingQueue<>();

        Event e1_1 = addEvent(queue, 1, 1);
        Event e2_1 = addEvent(queue, 2, 1);
        Event e3_1 = addEvent(queue, 3, 1);
        Event e4_1 = addEvent(queue, 4, 1);

        Event e5_2 = addEvent(queue, 5, 2);
        Event e6_2 = addEvent(queue, 6, 2);
        Event e7_2 = addEvent(queue, 7, 2);
        Event e8_2 = addEvent(queue, 8, 2);

        Event e21_0 = addEvent(queue, 21, 0);

        Event e31_5 = addEvent(queue, 31, 5);

        // First event should have the greatest priority and the lowest id
        Assert.assertEquals(e31_5, queue.take());

        Assert.assertEquals(e5_2, queue.take());
        Assert.assertEquals(e6_2, queue.take());
        Assert.assertEquals(e7_2, queue.take());
        Assert.assertEquals(e8_2, queue.take());

        Assert.assertEquals(e1_1, queue.take());
        Assert.assertEquals(e2_1, queue.take());
        Assert.assertEquals(e3_1, queue.take());
        Assert.assertEquals(e4_1, queue.take());

        Assert.assertEquals(e21_0, queue.take());
    }
}