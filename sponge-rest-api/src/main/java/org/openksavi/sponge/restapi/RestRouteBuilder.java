/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.restapi;

import static org.apache.camel.model.rest.RestParamType.body;

import javax.inject.Inject;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.model.RestAction;
import org.openksavi.sponge.restapi.model.RestActionMetadata;
import org.openksavi.sponge.restapi.model.RestEvent;

public class RestRouteBuilder extends RouteBuilder {

    @Inject
    private SpongeEngine engine;

    @Override
    public void configure() throws Exception {
        // @formatter:off
        restConfiguration().component("undertow")
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", "true")
            .contextPath("/")./*host("localhost").*/port(RestApiConstants.DEFAULT_PORT)
            // Add swagger api-doc out of the box.
            .apiContextPath("/api-doc").apiProperty("api.title", "User API").apiProperty("api.version", engine.getVersion())
                .apiProperty("cors", "true");

        rest("/sponge").description("Sponge REST service")
            .consumes(RestApiConstants.APPLICATION_JSON_VALUE).produces(RestApiConstants.APPLICATION_JSON_VALUE)
            .post("/call").description("Call the action").type(RestAction.class)
                .param().name("body").type(body).description("The action to call").endParam()
                .responseMessage().code(200).message("Action result").endResponseMessage()
                .to("bean:spongeRestService?method=call")

            .post("/send").description("Send a new event").type(RestEvent.class)
                .param().name("body").type(body).description("The event to send").endParam()
                .responseMessage().code(200).message("Event id").endResponseMessage()
                .to("bean:spongeRestService?method=send")

            .get("/actions").description("Get actions").outType(RestActionMetadata[].class)
                .responseMessage().code(200).message("Actions").endResponseMessage()
                .to("bean:spongeRestService?method=getActions")

            .get("/version").description("Sponge version").outType(String.class)
                .responseMessage().code(200).message("Sponge version").endResponseMessage()
                .to("bean:spongeRestService?method=getVersion");
        // @formatter:on
    }
}
