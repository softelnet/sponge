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

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.model.RestActionCall;
import org.openksavi.sponge.restapi.model.RestActionsResult;
import org.openksavi.sponge.restapi.model.RestCallResult;
import org.openksavi.sponge.restapi.model.RestEvent;
import org.openksavi.sponge.restapi.model.RestReloadResult;
import org.openksavi.sponge.restapi.model.RestSendResult;
import org.openksavi.sponge.restapi.model.RestVersionResult;

public class RestApiRouteBuilder extends RouteBuilder {

    private SpongeEngine engine;

    private RestApiService spongeRestService;

    private RestApiSettings settings;

    public RestApiRouteBuilder(SpongeEngine engine, RestApiService spongeRestService, RestApiSettings settings) {
        this.engine = engine;
        this.spongeRestService = spongeRestService;
        this.settings = settings;
    }

    @Override
    public void configure() throws Exception {
        // @formatter:off
        RestConfigurationDefinition restDefinition = restConfiguration().component(settings.getRestComponentId())
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", Boolean.toString(settings.isPrettyPrint()))
            .contextPath("/").port(settings.getPort())
            // Add swagger api-doc out of the box.
            .apiContextPath("/api-doc").apiProperty("api.title", "Sponge REST API").apiProperty("api.version", String.valueOf(settings.getVersion()))
                .apiProperty("cors", "true");

        if (settings.getHost() != null) {
            restDefinition.host(settings.getHost());
        }

        rest(RestApiConstants.URL_PREFIX + settings.getVersion()).description("Sponge REST service")
            .consumes(RestApiConstants.APPLICATION_JSON_VALUE).produces(RestApiConstants.APPLICATION_JSON_VALUE)
            .get("/version").description("Sponge version").outType(RestVersionResult.class)
                .responseMessage().code(200).message("Sponge version").endResponseMessage()
                .route()
                    .setBody((exchange) -> spongeRestService.getVersion())
                .endRest()
            .get("/actions").description("Get actions").outType(RestActionsResult.class)
                .param().name(RestApiConstants.REST_PARAM_ACTIONS_METADATA_REQUIRED_NAME).type(body)
                    .description("Action metadata required").dataType("boolean")
                    .defaultValue(RestApiConstants.REST_PARAM_ACTIONS_METADATA_REQUIRED_DEFAULT.toString())
                    .endParam()
                .responseMessage().code(200).message("Actions").endResponseMessage()
                .route()
                    .setBody((exchange) -> spongeRestService.getActions(exchange.getIn().getHeader(
                            RestApiConstants.REST_PARAM_ACTIONS_METADATA_REQUIRED_NAME, Boolean.class)))
                .endRest()
            .post("/call").description("Call the action").type(RestActionCall.class).outType(RestCallResult.class)
                .param().name("body").type(body).description("The action to call").endParam()
                .responseMessage().code(200).message("Action result").endResponseMessage()
                .route()
                    .setBody((exchange) -> spongeRestService.call(exchange.getIn().getBody(RestActionCall.class)))
                .endRest()
            .post("/send").description("Send a new event").type(RestEvent.class).outType(RestSendResult.class)
                .param().name("body").type(body).description("The event to send").endParam()
                .responseMessage().code(200).message("Event id").endResponseMessage()
                .route()
                    .setBody((exchange) -> spongeRestService.send(exchange.getIn().getBody(RestEvent.class)))
                .endRest()
            .post("/reload").description("Reload Sponge knowledge bases").outType(RestReloadResult.class)
                .responseMessage().code(200).message("Sponge knowledge bases reloaded").endResponseMessage()
                .route()
                    .setBody((exchange) -> spongeRestService.reload())
                    .endRest();
        // @formatter:on
    }
}
