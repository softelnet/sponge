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
import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.model.request.GetActionsRequest;
import org.openksavi.sponge.restapi.model.request.GetKnowledgeBasesRequest;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;
import org.openksavi.sponge.restapi.model.request.LoginRequest;
import org.openksavi.sponge.restapi.model.request.LogoutRequest;
import org.openksavi.sponge.restapi.model.request.ReloadRequest;
import org.openksavi.sponge.restapi.model.request.SendEventRequest;
import org.openksavi.sponge.restapi.model.response.ActionCallResponse;
import org.openksavi.sponge.restapi.model.response.GetActionsResponse;
import org.openksavi.sponge.restapi.model.response.GetKnowledgeBasesResponse;
import org.openksavi.sponge.restapi.model.response.GetVersionResponse;
import org.openksavi.sponge.restapi.model.response.LoginResponse;
import org.openksavi.sponge.restapi.model.response.LogoutResponse;
import org.openksavi.sponge.restapi.model.response.ReloadResponse;
import org.openksavi.sponge.restapi.model.response.SendEventResponse;

public class RestApiRouteBuilder extends RouteBuilder implements HasRestApiService {

    private RestApiService apiService;

    public RestApiRouteBuilder() {
        //
    }

    @Override
    public RestApiService getRestApiService() {
        return apiService;
    }

    @Override
    public void setRestApiService(RestApiService apiService) {
        this.apiService = apiService;
    }

    public RestApiSettings getSettings() {
        return apiService.getSettings();
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
        RestConfigurationDefinition restConfiguration = restConfiguration().component(getSettings().getRestComponentId())
            .bindingMode(RestBindingMode.json)
            .dataFormatProperty("prettyPrint", Boolean.toString(getSettings().isPrettyPrint()))
            .enableCORS(true)
            .contextPath("/")
            // Add swagger api-doc out of the box.
            .apiContextPath("/api-doc").apiProperty("api.title", "Sponge REST API")
                .apiProperty("api.version", String.valueOf(getSettings().getVersion()));
        // @formatter:on

        if (getSettings().getPort() != null) {
            restConfiguration.port(getSettings().getPort());
        }

        if (getSettings().getHost() != null) {
            restConfiguration.host(getSettings().getHost());
        }

        setupRestConfiguration(restConfiguration);
    }

    /**
     * Could be re-implemented to provide for example custom security.
     *
     * @param restConfiguration the REST configuration.
     */
    protected void setupRestConfiguration(RestConfigurationDefinition restConfiguration) {
        if (getSettings().getSslConfiguration() != null) {
            restConfiguration.scheme("https");

            if (getSettings().getSslContextParametersBeanName() != null) {
                restConfiguration.endpointProperty("sslContextParameters", "#" + getSettings().getSslContextParametersBeanName());
            }
        }
    }

    protected void createRestDefinition() {
        // @formatter:off
        RestDefinition restDefinition = rest(getSettings().getPath()).description("Sponge REST API")
            .consumes(RestApiConstants.APPLICATION_JSON_VALUE).produces(RestApiConstants.APPLICATION_JSON_VALUE)
            .post(RestApiConstants.OPERATION_VERSION).description("Get the Sponge version").type(GetVersionRequest.class)
                    .outType(GetVersionResponse.class)
                .param().name("body").type(body).description("Get Sponge version request").endParam()
                .responseMessage().code(200).message("The Sponge version response").endResponseMessage()
                .route().id("version")
                    .setBody(exchange -> apiService.getVersion(exchange.getIn().getBody(GetVersionRequest.class), exchange))
                .endRest()
            .post(RestApiConstants.OPERATION_LOGIN).description("Login").type(LoginRequest.class).outType(LoginResponse.class)
                .param().name("body").type(body).description("Login request").endParam()
                .responseMessage().code(200).message("The login response").endResponseMessage()
                .route().id("login")
                    .setBody(exchange -> apiService.login(exchange.getIn().getBody(LoginRequest.class), exchange))
                .endRest()
            .post(RestApiConstants.OPERATION_LOGOUT).description("Logout").type(LogoutRequest.class).outType(LogoutResponse.class)
                .param().name("body").type(body).description("Logout request").endParam()
                .responseMessage().code(200).message("The logout response").endResponseMessage()
                .route().id("logout")
                    .setBody(exchange -> apiService.logout(exchange.getIn().getBody(LogoutRequest.class), exchange))
                .endRest()
            .post(RestApiConstants.OPERATION_KNOWLEDGE_BASES).description("Get knowledge bases").type(GetKnowledgeBasesRequest.class)
                    .outType(GetKnowledgeBasesResponse.class)
                .param().name("body").type(body).description("Get knowledge bases request").endParam()
                .responseMessage().code(200).message("The get knowledge bases response").endResponseMessage()
                .route().id("knowledgeBases")
                    .setBody(exchange -> apiService.getKnowledgeBases(exchange.getIn().getBody(GetKnowledgeBasesRequest.class), exchange))
                .endRest()
            .post(RestApiConstants.OPERATION_ACTIONS).description("Get actions").type(GetActionsRequest.class).outType(GetActionsResponse.class)
                .param().name("body").type(body).description("Get actions request").endParam()
                .responseMessage().code(200).message("The get actions response").endResponseMessage()
                .route().id("actions")
                    .setBody(exchange -> apiService.getActions(exchange.getIn().getBody(GetActionsRequest.class), exchange))
                .endRest()
            .post(RestApiConstants.OPERATION_CALL).description("Call an action").type(ActionCallRequest.class).outType(ActionCallResponse.class)
                .param().name("body").type(body).description("Call action request").endParam()
                .responseMessage().code(200).message("The action call response").endResponseMessage()
                .route().id("call")
                    .setBody(exchange -> apiService.call(exchange.getIn().getBody(ActionCallRequest.class), exchange))
                .endRest()
            .post(RestApiConstants.OPERATION_SEND).description("Send a new event").type(SendEventRequest.class).outType(SendEventResponse.class)
                .param().name("body").type(body).description("Send event request").endParam()
                .responseMessage().code(200).message("The send event response").endResponseMessage()
                .route().id("send")
                    .setBody(exchange -> apiService.send(exchange.getIn().getBody(SendEventRequest.class), exchange))
                .endRest();

        if (getSettings().isPublishReload()) {
            restDefinition.post(RestApiConstants.OPERATION_RELOAD).description("Reload knowledge bases")
                    .type(ReloadRequest.class).outType(ReloadResponse.class)
                .param().name("body").type(body).description("Reload knowledge bases request").endParam()
                .responseMessage().code(200).message("The reload response").endResponseMessage()
                .route().id("reload")
                    .setBody(exchange -> apiService.reload(exchange.getIn().getBody(ReloadRequest.class), exchange))
                    .endRest();
        }
        // @formatter:on
    }
}
