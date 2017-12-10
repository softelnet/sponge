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

package org.openksavi.sponge.midi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MidiUtilsTest {

    @Test
    public void testGetKeyNote() {
        assertEquals("C5", MidiUtils.getKeyNote(60));

        assertEquals("C0", MidiUtils.getKeyNote(0));
        assertEquals("G10", MidiUtils.getKeyNote(127));

        assertEquals("B1", MidiUtils.getKeyNote(23));
        assertEquals("F7", MidiUtils.getKeyNote(89));
    }
}
