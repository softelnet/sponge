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

import org.openksavi.sponge.event.Event;

public class ShapeFilter extends org.openksavi.sponge.java.JavaFilter {

    @Override
    public void configure() {
        setEventNames("e1", "e2", "e3");
    }

    @Override
    public boolean accepts(Event event) {
        String shape = event.get("shape", String.class);
        if (shape == null) {
            getLogger().debug("No shape for event: " + event.toString() + "; event rejected");
            return false;
        }

        getLogger().debug("Shape is set in event" + event.toString() + "; event accepted");

        return true;
    }
}
