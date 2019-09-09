/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.restapi.model;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Sponge Remote API event.
 */
public class RemoteEvent {

    private String id;

    private String name;

    private Instant time;

    private int priority;

    private String label;

    private String description;

    private Map<String, Object> attributes = Collections.synchronizedMap(new LinkedHashMap<>());

    public RemoteEvent(String id, String name, Instant time, int priority, String label, String description,
            Map<String, Object> attributes) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.priority = priority;
        this.label = label;
        this.description = description;
        setAttributes(attributes);
    }

    public RemoteEvent(String id, String name, Instant time, int priority, String label, String description) {
        this(id, name, time, priority, label, description, Collections.emptyMap());
    }

    public RemoteEvent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = Collections.synchronizedMap(new LinkedHashMap<>(attributes));
    }
}
