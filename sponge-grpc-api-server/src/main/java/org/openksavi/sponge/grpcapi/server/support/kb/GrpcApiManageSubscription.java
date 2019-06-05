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

import java.util.List;
import java.util.stream.Collectors;

import org.openksavi.sponge.action.ProvideArgsContext;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.type.BooleanType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.provided.ProvidedMeta;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.type.value.AnnotatedValue;

public class GrpcApiManageSubscription extends JAction {

    @Override
    public void onConfigure() {
        withLabel("Manage events subscription").withDescription("Manages event notifications.");
        // TODO Provided arg for event types.
        withArgs(
                new ListType<>("eventNames", new StringType()).withLabel("Event types").withDescription("Event types.")
                        .withProvided(new ProvidedMeta().withElementValueSet()),
                new BooleanType("subscribe").withLabel("Subscribe").withDescription("Turns on/off event subscriptions."));
        withNoResult();// new IntegerType().withLabel("Subscription ID").withDescription("The subscription ID."));
        withFeatures(SpongeUtils.immutableMapOf("intent", "subscription", "callLabel", "Save"));
    }

    public void onCall(List<String> eventNames, Boolean subscribe) {
    }

    @Override
    public void onProvideArgs(ProvideArgsContext context) {
        if (context.getNames().contains("eventNames")) {
            List<AnnotatedValue<String>> annotatedElementValueSet = getSponge().getEventTypes().entrySet().stream()
                    .map(entry -> new AnnotatedValue<>(entry.getKey())
                            .withLabel(entry.getValue().getLabel() != null ? entry.getValue().getLabel() : entry.getKey()))
                    .collect(Collectors.toList());
            context.getProvided().put("eventNames", new ProvidedValue<>().withAnnotatedElementValueSet(annotatedElementValueSet));
        }
    }
}
