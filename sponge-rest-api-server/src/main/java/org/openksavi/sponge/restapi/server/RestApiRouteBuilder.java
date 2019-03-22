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
import static org.apache.camel.model.rest.RestParamType.query;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpMessage;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestConfigurationDefinition;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.RestApiOperationType;
import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.model.request.GetActionsRequest;
import org.openksavi.sponge.restapi.model.request.GetKnowledgeBasesRequest;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;
import org.openksavi.sponge.restapi.model.request.LoginRequest;
import org.openksavi.sponge.restapi.model.request.LogoutRequest;
import org.openksavi.sponge.restapi.model.request.ProvideActionArgsRequest;
import org.openksavi.sponge.restapi.model.request.ReloadRequest;
import org.openksavi.sponge.restapi.model.request.SendEventRequest;
import org.openksavi.sponge.restapi.model.request.SpongeRequest;
import org.openksavi.sponge.restapi.model.response.ActionCallResponse;
import org.openksavi.sponge.restapi.model.response.GetActionsResponse;
import org.openksavi.sponge.restapi.model.response.GetKnowledgeBasesResponse;
import org.openksavi.sponge.restapi.model.response.GetVersionResponse;
import org.openksavi.sponge.restapi.model.response.LoginResponse;
import org.openksavi.sponge.restapi.model.response.LogoutResponse;
import org.openksavi.sponge.restapi.model.response.ProvideActionArgsResponse;
import org.openksavi.sponge.restapi.model.response.ReloadResponse;
import org.openksavi.sponge.restapi.model.response.SendEventResponse;
import org.openksavi.sponge.restapi.model.response.SpongeResponse;
import org.openksavi.sponge.restapi.util.RestApiUtils;
import org.openksavi.sponge.type.value.OutputStreamValue;

public class RestApiRouteBuilder extends RouteBuilder implements HasRestApiService {

    private static final Logger logger = LoggerFactory.getLogger(RestApiRouteBuilder.class);

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

    protected ObjectMapper getObjectMapper() {
        return apiService.getTypeConverter().getObjectMapper();
    }

    @Override
    public void configure() throws Exception {
        createErrorHandler();
        createRestConfiguration();
        createRestDefinition();
        createOnException();
    }

    protected void createErrorHandler() {
        //
    }

    protected void createOnException() {
        //
    }

    protected void createRestConfiguration() {
        // @formatter:off
        RestConfigurationDefinition restConfiguration = restConfiguration().component(getSettings().getRestComponentId())
            .bindingMode(RestBindingMode.off)
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

    protected void setupResponse(RestApiOperationType operationType, Exchange exchange, Object response) {
        try {
            String responseBody = getObjectMapper().writeValueAsString(response);

            exchange.getIn().setBody(responseBody);
            exchange.getIn().setHeader(Exchange.CONTENT_TYPE, RestApiConstants.CONTENT_TYPE_JSON);

            if (logger.isDebugEnabled()) {
                logger.debug("REST API {} response: {})", operationType.getCode(), RestApiUtils.obfuscatePassword(responseBody));
            }
        } catch (JsonProcessingException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    protected void setupStreamResponse(RestApiOperationType operationType, Exchange exchange, OutputStreamValue streamValue) {
        try {
            HttpServletResponse httpResponse = exchange.getIn(HttpMessage.class).getResponse();
            streamValue.getHeaders().forEach((name, value) -> {
                if (value != null) {
                    httpResponse.setHeader(name, String.valueOf(value));
                }
            });

            if (streamValue.getContentType() != null) {
                httpResponse.setContentType(streamValue.getContentType());
            }

            ServletOutputStream output = httpResponse.getOutputStream();
            if (streamValue.getOutputProducer() != null) {
                streamValue.getOutputProducer().produce(output);
            }

            output.flush();
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    protected <I extends SpongeRequest, O extends SpongeResponse> void createOperation(RestDefinition restDefinition,
            RestApiOperationType operationType, String operationDescription, Class<I> requestClass, String requestDescription,
            Class<O> responseClass, String responseDescription, BiFunction<I, Exchange, O> operationHandler) {
        createPostOperation(restDefinition, operationType, operationDescription, requestClass, requestDescription, responseClass,
                responseDescription, operationHandler);
        createGetOperation(restDefinition, operationType, operationDescription, requestClass, requestDescription, responseClass,
                responseDescription, operationHandler);
    }

    protected <I extends SpongeRequest, O extends SpongeResponse> void createPostOperation(RestDefinition restDefinition,
            RestApiOperationType operationType, String operationDescription, Class<I> requestClass, String requestDescription,
            Class<O> responseClass, String responseDescription, BiFunction<I, Exchange, O> operationHandler) {
        RouteDefinition operationRouteDefinition =
                restDefinition.post(operationType.getCode()).description(operationDescription).type(requestClass).outType(responseClass)
                        .param().name("body").type(body).description(requestDescription).endParam().responseMessage().code(200)
                        .message(responseDescription).endResponseMessage().route().routeId("sponge-post-" + operationType.getCode());

        setupOperationRouteBeforeExecution(operationRouteDefinition, operationType, requestClass, responseClass);
        operationRouteDefinition.process(createOperationExecutionProcessor(message -> message.getBody(String.class), operationType,
                requestClass, responseClass, operationHandler));
        setupOperationRouteAfterExecution(operationRouteDefinition, operationType, requestClass, responseClass);

        operationRouteDefinition.endRest();
    }

    protected <I extends SpongeRequest, O extends SpongeResponse> void createGetOperation(RestDefinition restDefinition,
            RestApiOperationType operationType, String operationDescription, Class<I> requestClass, String requestDescription,
            Class<O> responseClass, String responseDescription, BiFunction<I, Exchange, O> operationHandler) {
        RouteDefinition operationRouteDefinition = restDefinition.get(operationType.getCode()).description(operationDescription)
                .outType(responseClass).param().name("request").type(query).description(requestDescription).endParam().responseMessage()
                .code(200).message(responseDescription).endResponseMessage().route().routeId("sponge-get-" + operationType.getCode());

        setupOperationRouteBeforeExecution(operationRouteDefinition, operationType, requestClass, responseClass);
        operationRouteDefinition.process(createOperationExecutionProcessor(message -> {
            try {
                String requestParam = message.getHeader("request", String.class);

                // Decode the JSON url-encoded request parameter.
                return requestParam != null ? URLDecoder.decode(requestParam, StandardCharsets.UTF_8.name()) : "";
            } catch (UnsupportedEncodingException e) {
                throw SpongeUtils.wrapException(e);
            }
        }, operationType, requestClass, responseClass, operationHandler));
        setupOperationRouteAfterExecution(operationRouteDefinition, operationType, requestClass, responseClass);

        operationRouteDefinition.endRest();
    }

    protected <I extends SpongeRequest, O extends SpongeResponse> void setupOperationRouteBeforeExecution(
            RouteDefinition operationRouteDefinition, RestApiOperationType operationType, Class<I> requestClass, Class<O> responseClass) {
    }

    protected <I extends SpongeRequest, O extends SpongeResponse> void setupOperationRouteAfterExecution(
            RouteDefinition operationRouteDefinition, RestApiOperationType operationType, Class<I> requestClass, Class<O> responseClass) {
    }

    private <O extends SpongeResponse> OutputStreamValue getActionCallOutputStreamResponse(O response) {
        if (response instanceof ActionCallResponse) {
            Object actionCallResult = ((ActionCallResponse) response).getResult();
            if (actionCallResult instanceof OutputStreamValue) {
                return (OutputStreamValue) actionCallResult;
            }
        }

        return null;
    }

    protected <I extends SpongeRequest, O extends SpongeResponse> Processor createOperationExecutionProcessor(
            Function<Message, String> requestBodyProvider, RestApiOperationType operationType, Class<I> requestClass,
            Class<O> responseClass, BiFunction<I, Exchange, O> operationHandler) {
        return exchange -> {
            String requestBody = requestBodyProvider.apply(exchange.getIn());

            if (logger.isDebugEnabled()) {
                logger.debug("REST API {} request: {}", operationType.getCode(), RestApiUtils.obfuscatePassword(requestBody));
            }

            // Allow empty body.
            if (StringUtils.isBlank(requestBody)) {
                requestBody = "{}";
            }

            try {
                O response = operationHandler.apply(getObjectMapper().readValue(requestBody, requestClass), exchange);

                // Handle an action call that returns a stream.
                OutputStreamValue streamValue = getActionCallOutputStreamResponse(response);
                if (streamValue == null) {
                    setupResponse(operationType, exchange, response);
                } else {
                    setupStreamResponse(operationType, exchange, streamValue);
                }
            } catch (Throwable processingException) {
                logger.info("REST API error", processingException);
                try {
                    setupResponse(operationType, exchange, apiService.createGenericErrorResponse(processingException, exchange));
                } catch (Throwable e) {
                    logger.error("REST API send error response failure", e);
                    throw e;
                }
            }
        };
    }

    protected void createRestDefinition() {
        RestDefinition restDefinition = rest(getSettings().getPath()).description("Sponge REST API");

        createOperation(restDefinition, RestApiOperationType.VERSION, "Get the Sponge version", GetVersionRequest.class,
                "Get Sponge version request", GetVersionResponse.class, "The Sponge version response",
                (request, exchange) -> apiService.getVersion(request, exchange));
        createOperation(restDefinition, RestApiOperationType.LOGIN, "Login", LoginRequest.class, "Login request", LoginResponse.class,
                "The login response", (request, exchange) -> apiService.login(request, exchange));
        createOperation(restDefinition, RestApiOperationType.LOGOUT, "Logout", LogoutRequest.class, "Logout request", LogoutResponse.class,
                "The logout response", (request, exchange) -> apiService.logout(request, exchange));
        createOperation(restDefinition, RestApiOperationType.KNOWLEDGE_BASES, "Get knowledge bases", GetKnowledgeBasesRequest.class,
                "Get knowledge bases request", GetKnowledgeBasesResponse.class, "The get knowledge bases response",
                (request, exchange) -> apiService.getKnowledgeBases(request, exchange));
        createOperation(restDefinition, RestApiOperationType.ACTIONS, "Get actions", GetActionsRequest.class, "Get actions request",
                GetActionsResponse.class, "The get actions response", (request, exchange) -> apiService.getActions(request, exchange));
        createOperation(restDefinition, RestApiOperationType.CALL, "Call an action", ActionCallRequest.class, "Call action request",
                ActionCallResponse.class, "The action call response", (request, exchange) -> apiService.call(request, exchange));
        createOperation(restDefinition, RestApiOperationType.SEND, "Send a new event", SendEventRequest.class, "Send event request",
                SendEventResponse.class, "The send event response", (request, exchange) -> apiService.send(request, exchange));
        createOperation(restDefinition, RestApiOperationType.ACTION_ARGS, "Provide action arguments", ProvideActionArgsRequest.class,
                "The provide action arguments request", ProvideActionArgsResponse.class, "The provide action arguments response",
                (request, exchange) -> apiService.provideActionArgs(request, exchange));

        if (getSettings().isPublishReload()) {
            createOperation(restDefinition, RestApiOperationType.RELOAD, "Reload knowledge bases", ReloadRequest.class,
                    "Reload knowledge bases request", ReloadResponse.class, "The reload response",
                    (request, exchange) -> apiService.reload(request, exchange));
        }
    }
}
