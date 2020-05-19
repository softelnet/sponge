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

package org.openksavi.sponge.grpcapi.server.support.kb;

import java.time.Instant;
import java.util.Map;

import org.openksavi.sponge.action.ProvideArgsContext;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.remoteapi.model.RemoteEvent;
import org.openksavi.sponge.type.DateTimeKind;
import org.openksavi.sponge.type.DateTimeType;
import org.openksavi.sponge.type.DynamicType;
import org.openksavi.sponge.type.ObjectType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.provided.ProvidedMeta;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.type.value.DynamicValue;

public class GrpcApiViewEvent extends JAction {

    @Override
    public void onConfigure() {
        withLabel("Event").withDescription("Shows the event.");
        withArgs(new ObjectType<>("event", RemoteEvent.class).withFeature("visible", false),
                new StringType("type").withLabel("Type").withDescription("Event type.").withReadOnly()
                        .withProvided(new ProvidedMeta().withValue().withDependency("event")),
                new DateTimeType("time", DateTimeKind.INSTANT).withLabel("Time").withDescription("Event time.").withReadOnly()
                        .withProvided(new ProvidedMeta().withValue().withDependency("event")),
                new DynamicType("attributes").withLabel("Attributes").withDescription("Event attributes.").withReadOnly()
                        .withProvided(new ProvidedMeta().withValue().withDependency("event")),
                new StringType("label").withNullable().withLabel("Event label").withDescription("Event label.").withReadOnly()
                        .withProvided(new ProvidedMeta().withValue().withDependency("event")),
                new StringType("description").withNullable().withLabel("Event description").withDescription("Event description.")
                        .withReadOnly().withProvided(new ProvidedMeta().withValue().withDependency("event")));

        withNoResult();
        withFeatures(SpongeUtils.immutableMapOf("visible", false, "intent", "defaultEventHandler", "callLabel", "Dismiss", "refreshLabel",
                null));
    }

    public void onCall(RemoteEvent event, String type, Instant time, DynamicValue<Map<String, Object>> attributes, String label,
            String description) {
        // No implementation required.
    }

    @Override
    public void onProvideArgs(ProvideArgsContext context) {
        RemoteEvent event = (RemoteEvent) context.getCurrent().get("event");
        RecordType eventType = getSponge().getEventType(event.getName());

        if (context.getProvide().contains("type")) {
            context.getProvided().put("type",
                    new ProvidedValue<>().withValue(eventType.getLabel() != null ? eventType.getLabel() : eventType.getName()));
        }

        if (context.getProvide().contains("time")) {
            context.getProvided().put("time", new ProvidedValue<>().withValue(event.getTime()));
        }

        if (context.getProvide().contains("attributes")) {
            context.getProvided().put("attributes", new ProvidedValue<>().withValue(new DynamicValue<>(event.getAttributes(), eventType)));
        }

        if (context.getProvide().contains("label")) {
            context.getProvided().put("label", new ProvidedValue<>().withValue(event.getLabel()));
        }

        if (context.getProvide().contains("description")) {
            context.getProvided().put("description", new ProvidedValue<>().withValue(event.getDescription()));
        }
    }
}
