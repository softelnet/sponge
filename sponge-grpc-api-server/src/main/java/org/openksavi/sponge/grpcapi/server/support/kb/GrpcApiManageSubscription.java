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

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.ProvideArgsContext;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.features.Features;
import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.restapi.server.RestApiService;
import org.openksavi.sponge.restapi.server.security.UserContext;
import org.openksavi.sponge.type.BooleanType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.provided.ProvidedMeta;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.type.value.AnnotatedValue;

public class GrpcApiManageSubscription extends JAction {

    private GrpcApiServerPlugin plugin;

    @Override
    public void onConfigure() {
        withLabel("Manage events subscription").withDescription("Manages event notifications.");
        withArgs(
                new ListType<>("eventNames", new StringType()).withLabel("Event types").withDescription("Event types.").withUnique()
                        .withProvided(new ProvidedMeta().withElementValueSet()),
                new BooleanType("subscribe").withLabel("Subscribe").withDescription("Turns on/off event subscriptions.")
                        .withDefaultValue(true));
        withNoResult();
        withFeatures(
                SpongeUtils.immutableMapOf("intent", "subscription", "clearLabel", null, "callLabel", "Save", "icon", "cellphone-message"));
    }

    @Override
    public void onInit() {
        plugin = getSponge().getPlugin(GrpcApiServerPlugin.class);
    }

    public void onCall(List<String> eventNames, Boolean subscribe) {
        Validate.isTrue(plugin.isServerRunning(), "The gRPC service is not runnning");
    }

    @Override
    public void onProvideArgs(ProvideArgsContext context) {
        if (context.getProvide().contains("eventNames")) {
            // Get the user from the current thread local session.
            UserContext userContext = getRestApiService().getSession().getUserAuthentication().getUserContext();

            List<AnnotatedValue<String>> annotatedElementValueSet = getSponge().getEventTypes().entrySet().stream()
                    // Get only visible event types.
                    .filter(entry -> ((Boolean) entry.getValue().getFeatures().getOrDefault(Features.VISIBLE, Boolean.TRUE)))
                    // Check permissions.
                    .filter(entry -> getRestApiService().canSubscribeEvent(userContext, entry.getKey()))
                    .map(entry -> new AnnotatedValue<>(entry.getKey())
                            .withValueLabel(entry.getValue().getLabel() != null ? entry.getValue().getLabel() : entry.getKey()))
                    .collect(Collectors.toList());
            context.getProvided().put("eventNames", new ProvidedValue<>().withAnnotatedElementValueSet(annotatedElementValueSet));
        }
    }

    private RestApiService getRestApiService() {
        return plugin.getRestApiServerPlugin().getService();
    }
}
