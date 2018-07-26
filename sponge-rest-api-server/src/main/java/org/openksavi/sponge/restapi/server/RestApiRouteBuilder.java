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

package org.openksavi.sponge.restapi.server;

import static org.apache.camel.model.rest.RestParamType.body;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.apache.camel.model.rest.RestDefinition;

import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.model.request.RestActionCallRequest;
import org.openksavi.sponge.restapi.model.request.RestGetActionsRequest;
import org.openksavi.sponge.restapi.model.request.RestGetKnowledgeBasesRequest;
import org.openksavi.sponge.restapi.model.request.RestGetVersionRequest;
import org.openksavi.sponge.restapi.model.request.RestLoginRequest;
import org.openksavi.sponge.restapi.model.request.RestLogoutRequest;
import org.openksavi.sponge.restapi.model.request.RestReloadRequest;
import org.openksavi.sponge.restapi.model.request.RestSendEventRequest;
import org.openksavi.sponge.restapi.model.response.RestActionCallResponse;
import org.openksavi.sponge.restapi.model.response.RestGetActionsResponse;
import org.openksavi.sponge.restapi.model.response.RestGetKnowledgeBasesResponse;
import org.openksavi.sponge.restapi.model.response.RestGetVersionResponse;
import org.openksavi.sponge.restapi.model.response.RestLoginResponse;
import org.openksavi.sponge.restapi.model.response.RestLogoutResponse;
import org.openksavi.sponge.restapi.model.response.RestReloadResponse;
import org.openksavi.sponge.restapi.model.response.RestSendEventResponse;

public class RestApiRouteBuilder extends RouteBuilder {

    private RestApiService apiService;

    private RestApiSettings settings;

    public RestApiRouteBuilder() {
        //
    }

    public RestApiService getApiService() {
        return apiService;
    }

    public void setApiService(RestApiService apiService) {
        this.apiService = apiService;
    }

    public RestApiSettings getSettings() {
        return settings;
    }

    public void setSettings(RestApiSettings settings) {
        this.settings = settings;
    }

    @Override
    public void configure() throws Exception {
        createErrorHandler();
        createRestConfiguration();
        createRestDefinition();
    }

    protected void createErrorHandler() {
        //
    }

    protected void createRestConfiguration() {
        // @formatter:off
        RestConfigurationDefinition restConfiguration = restConfiguration().component(settings.getRestComponentId())
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", Boolean.toString(settings.isPrettyPrint()))
            .enableCORS(true)
            .contextPath("/")
            // Add swagger api-doc out of the box.
            .apiContextPath("/api-doc").apiProperty("api.title", "Sponge REST API").apiProperty("api.version", String.valueOf(settings.getVersion()));
        // @formatter:on

        if (settings.getPort() != null) {
            restConfiguration.port(settings.getPort());
        }

        if (settings.getHost() != null) {
            restConfiguration.host(settings.getHost());
        }

        setupRestConfiguration(restConfiguration);
    }

    /**
     * Could be re-implemented to provide for example custom security.
     *
     * @param restConfiguration the REST configuration.
     */
    protected void setupRestConfiguration(RestConfigurationDefinition restConfiguration) {
        if (settings.getSslConfiguration() != null) {
            restConfiguration.scheme("https");

            if (settings.getSslContextParametersBeanName() != null) {
                restConfiguration.endpointProperty("sslContextParameters", "#" + settings.getSslContextParametersBeanName());
            }
        }
    }

    protected void createRestDefinition() {
        // @formatter:off
        RestDefinition restDefinition = rest(RestApiConstants.BASE_URL).description("Sponge REST API")
            .consumes(RestApiConstants.APPLICATION_JSON_VALUE).produces(RestApiConstants.APPLICATION_JSON_VALUE)
            .post("/version").description("Get the Sponge version").type(RestGetVersionRequest.class).outType(RestGetVersionResponse.class)
                .param().name("body").type(body).description("Get Sponge version request").endParam()
                .responseMessage().code(200).message("The Sponge version response").endResponseMessage()
                .route().id("version")
                    .setBody(exchange -> apiService.getVersion(exchange.getIn().getBody(RestGetVersionRequest.class), exchange))
                .endRest()
            .post("/login").description("Login").type(RestLoginRequest.class).outType(RestLoginResponse.class)
                .param().name("body").type(body).description("Login request").endParam()
                .responseMessage().code(200).message("The login response").endResponseMessage()
                .route().id("login")
                    .setBody(exchange -> apiService.login(exchange.getIn().getBody(RestLoginRequest.class), exchange))
                .endRest()
            .post("/logout").description("Logout").type(RestLogoutRequest.class).outType(RestLogoutResponse.class)
                .param().name("body").type(body).description("Logout request").endParam()
                .responseMessage().code(200).message("The logout response").endResponseMessage()
                .route().id("logout")
                    .setBody(exchange -> apiService.logout(exchange.getIn().getBody(RestLogoutRequest.class), exchange))
                .endRest()
            .post("/knowledgeBases").description("Get knowledge bases").type(RestGetKnowledgeBasesRequest.class)
                    .outType(RestGetKnowledgeBasesResponse.class)
                .param().name("body").type(body).description("Get knowledge bases request").endParam()
                .responseMessage().code(200).message("The get knowledge bases response").endResponseMessage()
                .route().id("knowledgeBases")
                    .setBody(exchange -> apiService.getKnowledgeBases(exchange.getIn().getBody(RestGetKnowledgeBasesRequest.class), exchange))
                .endRest()
            .post("/actions").description("Get actions").type(RestGetActionsRequest.class).outType(RestGetActionsResponse.class)
                .param().name("body").type(body).description("Get actions request").endParam()
                .responseMessage().code(200).message("The get actions response").endResponseMessage()
                .route().id("actions")
                    .setBody(exchange -> apiService.getActions(exchange.getIn().getBody(RestGetActionsRequest.class), exchange))
                .endRest()
            .post("/call").description("Call an action").type(RestActionCallRequest.class).outType(RestActionCallResponse.class)
                .param().name("body").type(body).description("Call action request").endParam()
                .responseMessage().code(200).message("The action call response").endResponseMessage()
                .route().id("call")
                    .setBody(exchange -> apiService.call(exchange.getIn().getBody(RestActionCallRequest.class), exchange))
                .endRest()
            .post("/send").description("Send a new event").type(RestSendEventRequest.class).outType(RestSendEventResponse.class)
                .param().name("body").type(body).description("Send event request").endParam()
                .responseMessage().code(200).message("The send event response").endResponseMessage()
                .route().id("send")
                    .setBody(exchange -> apiService.send(exchange.getIn().getBody(RestSendEventRequest.class), exchange))
                .endRest();

        if (settings.isPublishReload()) {
            restDefinition.post("/reload").description("Reload knowledge bases").type(RestReloadRequest.class).outType(RestReloadResponse.class)
                .param().name("body").type(body).description("Reload knowledge bases request").endParam()
                .responseMessage().code(200).message("The reload response").endResponseMessage()
                .route().id("reload")
                    .setBody(exchange -> apiService.reload(exchange.getIn().getBody(RestReloadRequest.class), exchange))
                    .endRest();
        }
        // @formatter:on
    }
}
