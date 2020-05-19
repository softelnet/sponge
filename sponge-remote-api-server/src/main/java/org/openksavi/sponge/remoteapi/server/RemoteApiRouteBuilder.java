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

package org.openksavi.sponge.remoteapi.server;

import static org.apache.camel.model.rest.RestParamType.body;
import static org.apache.camel.model.rest.RestParamType.query;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.model.request.ActionCallRequest;
import org.openksavi.sponge.remoteapi.model.request.BodySpongeRequest;
import org.openksavi.sponge.remoteapi.model.request.GetActionsRequest;
import org.openksavi.sponge.remoteapi.model.request.GetEventTypesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetFeaturesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetKnowledgeBasesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetVersionRequest;
import org.openksavi.sponge.remoteapi.model.request.IsActionActiveRequest;
import org.openksavi.sponge.remoteapi.model.request.LoginRequest;
import org.openksavi.sponge.remoteapi.model.request.LogoutRequest;
import org.openksavi.sponge.remoteapi.model.request.ProvideActionArgsRequest;
import org.openksavi.sponge.remoteapi.model.request.ReloadRequest;
import org.openksavi.sponge.remoteapi.model.request.RequestHeader;
import org.openksavi.sponge.remoteapi.model.request.SendEventRequest;
import org.openksavi.sponge.remoteapi.model.request.SpongeRequest;
import org.openksavi.sponge.remoteapi.model.response.ActionCallResponse;
import org.openksavi.sponge.remoteapi.model.response.GetActionsResponse;
import org.openksavi.sponge.remoteapi.model.response.GetEventTypesResponse;
import org.openksavi.sponge.remoteapi.model.response.GetFeaturesResponse;
import org.openksavi.sponge.remoteapi.model.response.GetKnowledgeBasesResponse;
import org.openksavi.sponge.remoteapi.model.response.GetVersionResponse;
import org.openksavi.sponge.remoteapi.model.response.IsActionActiveResponse;
import org.openksavi.sponge.remoteapi.model.response.LoginResponse;
import org.openksavi.sponge.remoteapi.model.response.LogoutResponse;
import org.openksavi.sponge.remoteapi.model.response.ProvideActionArgsResponse;
import org.openksavi.sponge.remoteapi.model.response.ReloadResponse;
import org.openksavi.sponge.remoteapi.model.response.SendEventResponse;
import org.openksavi.sponge.remoteapi.model.response.SpongeResponse;
import org.openksavi.sponge.remoteapi.util.RemoteApiUtils;
import org.openksavi.sponge.type.value.OutputStreamValue;

public class RemoteApiRouteBuilder extends RouteBuilder implements HasRemoteApiService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteApiRouteBuilder.class);

    private RemoteApiService apiService;

    private List<RemoteApiOperation<?, ?>> operations = new ArrayList<>();

    public RemoteApiRouteBuilder() {
        //
    }

    @Override
    public RemoteApiService getRemoteApiService() {
        return apiService;
    }

    @Override
    public void setRemoteApiService(RemoteApiService apiService) {
        this.apiService = apiService;
    }

    public RemoteApiSettings getSettings() {
        return apiService.getSettings();
    }

    protected ObjectMapper getObjectMapper() {
        return apiService.getTypeConverter().getObjectMapper();
    }

    @Override
    public void configure() throws Exception {
        createErrorHandler();
        createRemoteApiConfiguration();
        createRemoteApiDefinition();
        createOnException();
    }

    protected void createErrorHandler() {
        //
    }

    protected Processor createDefaultOnExceptionProcessor() {
        return exchange -> {
            try {
                Throwable processingException =
                        exchange.getIn().getHeader(RemoteApiServerConstants.EXCHANGE_HEADER_EXCEPTION, Throwable.class);
                if (processingException == null) {
                    processingException = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
                }

                logger.info("Remote API error", processingException);

                String operationName =
                        Validate.notNull(exchange.getIn().getHeader(RemoteApiServerConstants.EXCHANGE_HEADER_OPERATION_NAME, String.class),
                                "The operation name is not set in the Camel route");

                setupResponse(operationName, exchange, apiService.createGenericErrorResponse(processingException));

                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, RemoteApiConstants.HTTP_CODE_ERROR);
            } catch (Throwable e) {
                logger.error("Remote API send error response failure", e);
                throw e;
            }
        };
    }

    protected void createOnException() {
        onException(Throwable.class).process(createDefaultOnExceptionProcessor()).handled(true);
    }

    protected void createRemoteApiConfiguration() {
        // @formatter:off
        RestConfigurationDefinition configuration = restConfiguration().component(getSettings().getComponentId())
            .bindingMode(RestBindingMode.off)
            .dataFormatProperty("prettyPrint", Boolean.toString(getSettings().isPrettyPrint()))
            .enableCORS(true)
            .contextPath("/" + (getSettings().getPath() != null ? getSettings().getPath() : ""))
            // Add swagger api doc out of the box.
            .apiContextPath("/" + RemoteApiConstants.OPERATION_DOC)
                .apiVendorExtension(false);
        // @formatter:on

        getSettings().getOpenApiProperties().forEach((name, value) -> configuration.apiProperty(name, value));

        configuration.apiProperty("api.version", String.valueOf(getSettings().getVersion()));

        configuration.apiProperty("api.title",
                getSettings().getName() != null ? getSettings().getName() : RemoteApiServerConstants.DEFAULT_NAME);

        if (getSettings().getDescription() != null) {
            configuration.apiProperty("api.description", getSettings().getDescription());
        }

        if (getSettings().getPort() != null) {
            configuration.port(getSettings().getPort());
        }

        if (getSettings().getHost() != null) {
            configuration.host(getSettings().getHost());
        }

        setupRemoteApiConfiguration(configuration);
    }

    /**
     * Could be re-implemented to provide for example custom security.
     *
     * @param configuration the configuration.
     */
    protected void setupRemoteApiConfiguration(RestConfigurationDefinition configuration) {
        if (getSettings().getSslConfiguration() != null) {
            configuration.scheme("https");

            if (getSettings().getSslContextParametersBeanName() != null) {
                configuration.endpointProperty("sslContextParameters", "#" + getSettings().getSslContextParametersBeanName());
            }
        }
    }

    protected void setupResponse(String operationName, Exchange exchange, SpongeResponse response) {
        try {
            if (apiService.getSettings().isIncludeResponseTimes()) {
                response.getHeader()
                        .setRequestTime(exchange.getIn().getHeader(RemoteApiServerConstants.EXCHANGE_HEADER_REQUEST_TIME, Instant.class));
                response.getHeader().setResponseTime(Instant.now());
            }

            // Put response features from the session to the response header.
            RemoteApiSession session = apiService.getSession();
            if (session != null && !session.getResponseFeatures().isEmpty()) {
                response.getHeader().setFeatures(session.getResponseFeatures());
            }

            String responseBody = getObjectMapper().writeValueAsString(response);

            exchange.getIn().setBody(responseBody);
            exchange.getIn().setHeader(Exchange.CONTENT_TYPE, RemoteApiConstants.CONTENT_TYPE_JSON);

            if (logger.isDebugEnabled()) {
                logger.debug("Remote API {} response: {})", operationName, RemoteApiUtils.obfuscatePassword(responseBody));
            }
        } catch (JsonProcessingException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    protected void setupStreamResponse(String operationName, Exchange exchange, OutputStreamValue streamValue) {
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

    protected <I extends SpongeRequest, O extends SpongeResponse> void createOperation(RestDefinition definition,
            RemoteApiOperation<I, O> operation) {
        createPostOperation(definition, operation);
        createGetOperation(definition, operation);
    }

    protected <I extends SpongeRequest, O extends SpongeResponse> void
            initializeOperationRouteDefinition(RouteDefinition operationRouteDefinition, RemoteApiOperation<I, O> operation) {
        operationRouteDefinition.setHeader(RemoteApiServerConstants.EXCHANGE_HEADER_OPERATION_NAME, constant(operation.getName()));
    }

    protected <I extends SpongeRequest, O extends SpongeResponse> void createPostOperation(RestDefinition definition,
            RemoteApiOperation<I, O> operation) {
        RouteDefinition operationRouteDefinition = definition.post("/" + operation.getName()).description(operation.getDescription())
                .type(operation.getRequestClass()).outType(operation.getResponseClass()).param().name("body").type(body)
                .description(operation.getRequestDescription()).endParam().responseMessage().code(200)
                .message(operation.getResponseDescription()).endResponseMessage().route().routeId("sponge-post-" + operation.getName());

        initializeOperationRouteDefinition(operationRouteDefinition, operation);
        setupOperationRouteBeforeExecution(operationRouteDefinition, operation);
        operationRouteDefinition.process(createOperationExecutionProcessor(message -> message.getBody(String.class), operation));
        setupOperationRouteAfterExecution(operationRouteDefinition, operation);

        operationRouteDefinition.endRest();
    }

    protected <I extends SpongeRequest, O extends SpongeResponse> void createGetOperation(RestDefinition definition,
            RemoteApiOperation<I, O> operation) {
        RouteDefinition operationRouteDefinition = definition.get("/" + operation.getName()).description(operation.getDescription())
                .outType(operation.getResponseClass()).param().name("request").type(query).description(operation.getRequestDescription())
                .endParam().responseMessage().code(200).message(operation.getResponseDescription()).endResponseMessage().route()
                .routeId("sponge-get-" + operation.getName());

        initializeOperationRouteDefinition(operationRouteDefinition, operation);
        setupOperationRouteBeforeExecution(operationRouteDefinition, operation);
        operationRouteDefinition.process(createOperationExecutionProcessor(message -> {
            try {
                String requestParam = message.getHeader("request", String.class);

                // Decode the JSON url-encoded request parameter.
                return requestParam != null ? URLDecoder.decode(requestParam, StandardCharsets.UTF_8.name()) : "";
            } catch (UnsupportedEncodingException e) {
                throw SpongeUtils.wrapException(e);
            }
        }, operation));
        setupOperationRouteAfterExecution(operationRouteDefinition, operation);

        operationRouteDefinition.endRest();
    }

    protected <I extends SpongeRequest, O extends SpongeResponse> void
            setupOperationRouteBeforeExecution(RouteDefinition operationRouteDefinition, RemoteApiOperation<I, O> operation) {
    }

    protected <I extends SpongeRequest, O extends SpongeResponse> void
            setupOperationRouteAfterExecution(RouteDefinition operationRouteDefinition, RemoteApiOperation<I, O> operation) {
    }

    private <O extends SpongeResponse> OutputStreamValue getActionCallOutputStreamResponse(O response) {
        if (response instanceof ActionCallResponse) {
            Object actionCallResult = ((ActionCallResponse) response).getBody().getResult();
            if (actionCallResult instanceof OutputStreamValue) {
                return (OutputStreamValue) actionCallResult;
            }
        }

        return null;
    }

    protected RemoteApiSession createSession(Exchange exchange) {
        return new CamelRemoteApiSession(null, exchange);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <I extends SpongeRequest, O extends SpongeResponse> Processor
            createOperationExecutionProcessor(Function<Message, String> requestBodyProvider, RemoteApiOperation<I, O> operation) {
        return exchange -> {
            String requestBody = requestBodyProvider.apply(exchange.getIn());

            if (logger.isDebugEnabled()) {
                logger.debug("Remote API {} request: {}", operation.getName(), RemoteApiUtils.obfuscatePassword(requestBody));
            }

            // Allow empty body.
            if (StringUtils.isBlank(requestBody)) {
                requestBody = "{}";
            }

            exchange.getIn().setHeader(RemoteApiServerConstants.EXCHANGE_HEADER_REQUEST_TIME, Instant.now());

            try {
                // Open a new session. The user will be set later in the service.
                apiService.openSession(createSession(exchange));

                I request = getObjectMapper().readValue(requestBody, operation.getRequestClass());

                // Set empty header if none.
                if (request != null && request.getHeader() == null) {
                    request.setHeader(new RequestHeader());
                }

                // Set empty body if none.
                if (request instanceof BodySpongeRequest) {
                    BodySpongeRequest bodyRequest = (BodySpongeRequest) request;
                    if (bodyRequest.getBody() == null) {
                        bodyRequest.setBody(bodyRequest.createBody());
                    }
                }

                O response = operation.getOperationHandler().apply(request, exchange);

                // Handle an action call that returns a stream.
                OutputStreamValue streamValue = getActionCallOutputStreamResponse(response);
                if (streamValue == null) {
                    setupResponse(operation.getName(), exchange, response);
                } else {
                    setupStreamResponse(operation.getName(), exchange, streamValue);
                }
            } finally {
                // Close the session.
                apiService.closeSession();
            }
        };
    }

    protected <I extends SpongeRequest, O extends SpongeResponse> void addOperation(RemoteApiOperation<I, O> operation) {
        Validate.isTrue(operations.stream().allMatch(o -> !Objects.equals(o.getName(), operation.getName())),
                "The operation '%s' has already been defined", operation.getName());

        operations.add(operation);
    }

    protected void createDefaultOperations() {
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.OPERATION_VERSION, "Get the Sponge version", GetVersionRequest.class,
                "The get Sponge version request", GetVersionResponse.class, "The Sponge version response",
                (request, exchange) -> apiService.getVersion(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.OPERATION_FEATURES, "Get the API features", GetFeaturesRequest.class,
                "The get API features request", GetFeaturesResponse.class, "The API features response",
                (request, exchange) -> apiService.getFeatures(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.OPERATION_LOGIN, "Login", LoginRequest.class, "The login request",
                LoginResponse.class, "The login response", (request, exchange) -> apiService.login(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.OPERATION_LOGOUT, "Logout", LogoutRequest.class, "The logout request",
                LogoutResponse.class, "The logout response", (request, exchange) -> apiService.logout(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.OPERATION_KNOWLEDGE_BASES, "Get knowledge bases",
                GetKnowledgeBasesRequest.class, "The get knowledge bases request", GetKnowledgeBasesResponse.class,
                "The get knowledge bases response", (request, exchange) -> apiService.getKnowledgeBases(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.OPERATION_ACTIONS, "Get actions", GetActionsRequest.class,
                "The get actions request", GetActionsResponse.class, "The get actions response",
                (request, exchange) -> apiService.getActions(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.OPERATION_CALL, "Call an action", ActionCallRequest.class,
                "The call action request", ActionCallResponse.class, "The action call response",
                (request, exchange) -> apiService.call(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.OPERATION_SEND, "Send a new event", SendEventRequest.class,
                "The send event request", SendEventResponse.class, "The send event response",
                (request, exchange) -> apiService.send(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.OPERATION_IS_ACTION_ACTIVE, "Is action active",
                IsActionActiveRequest.class, "The action active request", IsActionActiveResponse.class, "The action active response",
                (request, exchange) -> apiService.isActionActive(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.OPERATION_PROVIDE_ACTION_ARGS, "Provide action arguments",
                ProvideActionArgsRequest.class, "The provide action arguments request", ProvideActionArgsResponse.class,
                "The provide action arguments response", (request, exchange) -> apiService.provideActionArgs(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.OPERATION_EVENT_TYPES, "Get event types", GetEventTypesRequest.class,
                "The get event types request", GetEventTypesResponse.class, "The get event types response",
                (request, exchange) -> apiService.getEventTypes(request)));

        if (getSettings().isPublishReload()) {
            addOperation(new RemoteApiOperation<>(RemoteApiConstants.OPERATION_RELOAD, "Reload knowledge bases", ReloadRequest.class,
                    "The reload request", ReloadResponse.class, "The reload response", (request, exchange) -> apiService.reload(request)));
        }
    }

    /**
     * Overwrite this method to add custom operations.
     */
    protected void createCustomOperations() {
        //
    }

    protected void createRemoteApiDefinition() {
        RestDefinition definition = rest().description(getSettings().getDescription());

        createDefaultOperations();
        createCustomOperations();

        operations.forEach(operation -> createOperation(definition, operation));
    }
}
