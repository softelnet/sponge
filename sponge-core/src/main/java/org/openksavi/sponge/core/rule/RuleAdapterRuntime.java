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

package org.openksavi.sponge.core.rule;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.openksavi.sponge.core.util.Tree;
import org.openksavi.sponge.event.Event;

public interface RuleAdapterRuntime {

    boolean isCandidateForFirstEvent(Event event);

    boolean acceptAsFirst(Event event);

    Event getFirstEvent();

    Event getEvent(String eventAlias);

    Map<String, Event> getEventAliasMap();

    List<Event> getEventSequence();

    Tree<NodeValue> getEventTree();

    void onEvent(Event event);

    boolean runRule();

    void clear();

    void validate();

    public static class NodeValue {

        private Event event;

        private Integer index;

        public NodeValue(Event event, int index) {
            this.event = event;
            this.index = index;
        }

        public NodeValue(Event event) {
            this.event = event;
        }

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(event).append(index).toString();
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }
    }
}
