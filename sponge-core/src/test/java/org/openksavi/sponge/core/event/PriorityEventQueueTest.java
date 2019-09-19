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

package org.openksavi.sponge.core.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.engine.event.PriorityEventQueueComparator;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventClonePolicy;

public class PriorityEventQueueTest {

    private AtomicLongEventIdGenerator idGenerator = new AtomicLongEventIdGenerator();

    private DefaultSpongeEngine engine;

    @BeforeEach
    public void beforeTest() {
        engine = new DefaultSpongeEngine();
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
        PriorityBlockingQueue<Event> queue = new PriorityBlockingQueue<>(20, new PriorityEventQueueComparator());

        Event e1p1 = addEvent(queue, 1, 1);
        Event e2p1 = addEvent(queue, 2, 1);
        Event e3p1 = addEvent(queue, 3, 1);
        Event e4p1 = addEvent(queue, 4, 1);

        Event e5p2 = addEvent(queue, 5, 2);
        Event e6p2 = addEvent(queue, 6, 2);
        Event e7p2 = addEvent(queue, 7, 2);
        Event e8p2 = addEvent(queue, 8, 2);

        Event e21p0 = addEvent(queue, 21, 0);

        Event e31p5 = addEvent(queue, 31, 5);

        // First event should have the greatest priority and the lowest id
        assertEquals(e31p5, queue.take());

        assertEquals(e5p2, queue.take());
        assertEquals(e6p2, queue.take());
        assertEquals(e7p2, queue.take());
        assertEquals(e8p2, queue.take());

        assertEquals(e1p1, queue.take());
        assertEquals(e2p1, queue.take());
        assertEquals(e3p1, queue.take());
        assertEquals(e4p1, queue.take());

        assertEquals(e21p0, queue.take());
    }
}
