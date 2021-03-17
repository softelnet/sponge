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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.remoteapi.JsonRpcConstants;
import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.model.request.ActionCallRequest;
import org.openksavi.sponge.remoteapi.model.request.ActionCallRequest.ActionCallParams;
import org.openksavi.sponge.remoteapi.model.request.BaseRequestParams;
import org.openksavi.sponge.remoteapi.model.request.GenericRequest;
import org.openksavi.sponge.remoteapi.model.request.GetActionsRequest;
import org.openksavi.sponge.remoteapi.model.request.GetActionsRequest.GetActionsParams;
import org.openksavi.sponge.remoteapi.model.request.GetEventTypesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetEventTypesRequest.GetEventTypesParams;
import org.openksavi.sponge.remoteapi.model.request.GetFeaturesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetKnowledgeBasesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetVersionRequest;
import org.openksavi.sponge.remoteapi.model.request.IsActionActiveRequest;
import org.openksavi.sponge.remoteapi.model.request.IsActionActiveRequest.IsActionActiveParams;
import org.openksavi.sponge.remoteapi.model.request.LoginRequest;
import org.openksavi.sponge.remoteapi.model.request.LogoutRequest;
import org.openksavi.sponge.remoteapi.model.request.ProvideActionArgsRequest;
import org.openksavi.sponge.remoteapi.model.request.ProvideActionArgsRequest.ProvideActionArgsParams;
import org.openksavi.sponge.remoteapi.model.request.ReloadRequest;
import org.openksavi.sponge.remoteapi.model.request.RequestHeader;
import org.openksavi.sponge.remoteapi.model.request.RequestParams;
import org.openksavi.sponge.remoteapi.model.request.SendEventRequest;
import org.openksavi.sponge.remoteapi.model.request.SendEventRequest.SendEventParams;
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
import org.openksavi.sponge.remoteapi.model.response.ResponseHeader;
import org.openksavi.sponge.remoteapi.model.response.SendEventResponse;
import org.openksavi.sponge.remoteapi.model.response.SpongeResponse;
import org.openksavi.sponge.remoteapi.server.util.FormDataMultiPartContext;
import org.openksavi.sponge.remoteapi.util.RemoteApiUtils;
import org.openksavi.sponge.type.value.OutputStreamValue;

@SuppressWarnings("rawtypes")
public class RemoteApiRouteBuilder extends RouteBuilder implements HasRemoteApiService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteApiRouteBuilder.class);

    private RemoteApiService apiService;

    private Map<String, RemoteApiOperation<?, ?, ?>> operations = new LinkedHashMap<>();

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
                Throwable processingException = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);

                logger.info("Remote API error", processingException);

                String methodName =
                        Validate.notNull(exchange.getProperty(RemoteApiServerConstants.EXCHANGE_PROPERTY_METHOD_NAME, String.class),
                                "The method name is not set in the Camel route");

                // A notification request should be a valid JSON-RPC request so in case of "Parse error" or "Invalid request"
                // an error response will be sent.
                setupResponse(methodName, exchange, apiService.createErrorResponse(processingException),
                        !isNotification(exchange) || shouldNotificationHaveErrorResponse(processingException));

                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, RemoteApiConstants.HTTP_RESPONSE_CODE_ERROR);
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
            // disableStreamCache is turned on to allow uploading large files in action calls (with InputStreamType arguments).
            // https://github.com/apache/camel/blob/master/components/camel-servlet/src/main/docs/servlet-component.adoc
            .endpointProperty("disableStreamCache", "true")
            .dataFormatProperty("prettyPrint", Boolean.toString(getSettings().isPrettyPrint()))
            .enableCORS(true)
            .contextPath("/" + (getSettings().getPath() != null ? getSettings().getPath() : ""))
            // Add swagger api doc out of the box.
            .apiContextPath("/" + RemoteApiConstants.ENDPOINT_DOC)
                .apiVendorExtension(false);
        // @formatter:on

        getSettings().getOpenApiProperties().forEach((name, value) -> configuration.apiProperty(name, value));

        configuration.apiProperty("api.version", apiService.getApiVersion());
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

    protected boolean isNotification(Exchange exchange) {
        return exchange.getProperty(RemoteApiServerConstants.EXCHANGE_PROPERTY_IS_NOTIFICATION, false, Boolean.class);
    }

    protected boolean shouldNotificationHaveErrorResponse(Throwable exception) {
        if (exception instanceof JsonRpcServerException) {
            int code = ((JsonRpcServerException) exception).getCode();

            if (code == JsonRpcConstants.ERROR_CODE_PARSE || code == JsonRpcConstants.ERROR_CODE_INVALID_REQUEST) {
                return true;
            }
        }

        return false;
    }

    protected void setupResponse(String method, Exchange exchange, SpongeResponse response, boolean includeResponse) {
        try {
            response.setId(exchange.getProperty(RemoteApiServerConstants.EXCHANGE_PROPERTY_REQUEST_ID));

            if (response.getResult() != null) {
                if (apiService.getSettings().isIncludeResponseTimes()) {
                    if (response.getResult().getHeader() == null) {
                        response.getResult().setHeader(new ResponseHeader());
                    }

                    response.getResult().getHeader()
                            .setRequestTime(exchange.getProperty(RemoteApiServerConstants.EXCHANGE_PROPERTY_REQUEST_TIME, Instant.class));
                    response.getResult().getHeader().setResponseTime(Instant.now());
                }

                // Put response features from the session to the response header.
                RemoteApiSession session = apiService.getSession();
                if (session != null && !session.getResponseFeatures().isEmpty()) {
                    if (response.getResult().getHeader() == null) {
                        response.getResult().setHeader(new ResponseHeader());
                    }

                    response.getResult().getHeader().setFeatures(session.getResponseFeatures());
                }
            }

            String responseBody = includeResponse ? getObjectMapper().writeValueAsString(response) : "";

            exchange.getIn().setBody(responseBody);
            exchange.getIn().setHeader(Exchange.CONTENT_TYPE, RemoteApiConstants.CONTENT_TYPE_JSON);

            if (logger.isDebugEnabled()) {
                logger.debug("Remote API {} response: {})", method, RemoteApiUtils.obfuscatePassword(responseBody));
            }
        } catch (JsonProcessingException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    protected void setupStreamResponse(String method, Exchange exchange, OutputStreamValue streamValue) {
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

    protected <I extends SpongeRequest<P>, P, O extends SpongeResponse> void createOperation(RestDefinition definition,
            RemoteApiOperation<I, P, O> operation) {
        createPostOperation(definition, operation);
        createGetOperation(definition, operation);
    }

    protected <I extends SpongeRequest<P>, P, O extends SpongeResponse> void
            initializeOperationRouteDefinition(RouteDefinition operationRouteDefinition, RemoteApiOperation<I, P, O> operation) {
        operationRouteDefinition.setProperty(RemoteApiServerConstants.EXCHANGE_PROPERTY_METHOD_NAME, constant(operation.getMethod()));
    }

    protected <I extends SpongeRequest<P>, P, O extends SpongeResponse> void createPostOperation(RestDefinition definition,
            RemoteApiOperation<I, P, O> operation) {
        RouteDefinition operationRouteDefinition = definition.post("/" + operation.getMethod()).description(operation.getDescription())
                .type(operation.getRequestClass()).outType(operation.getResponseClass()).param().name("body").type(body)
                .description(operation.getRequestDescription()).endParam().responseMessage().code(RemoteApiConstants.HTTP_RESPONSE_CODE_OK)
                .message(operation.getResponseDescription()).endResponseMessage().route().routeId("sponge-post-" + operation.getMethod());

        initializeOperationRouteDefinition(operationRouteDefinition, operation);
        setupOperationRouteBeforeExecution(operationRouteDefinition, operation);
        operationRouteDefinition.process(createOperationExecutionProcessor(message -> message.getBody(String.class), operation));
        setupOperationRouteAfterExecution(operationRouteDefinition, operation);

        operationRouteDefinition.endRest();
    }

    protected <I extends SpongeRequest<P>, P, O extends SpongeResponse> void createGetOperation(RestDefinition definition,
            RemoteApiOperation<I, P, O> operation) {
        definition.get("/" + operation.getMethod()).description(operation.getDescription()).outType(operation.getResponseClass());

        definition.param().name(JsonRpcConstants.MEMBER_JSONRPC).type(query).description("The JSON_RPC version").required(true).endParam();
        definition.param().name(JsonRpcConstants.MEMBER_METHOD).type(query).description("The JSON_RPC method").required(true).endParam();
        definition.param().name(JsonRpcConstants.MEMBER_PARAMS).type(query).description("The JSON_RPC params").endParam();
        definition.param().name(JsonRpcConstants.MEMBER_ID).type(query).description("The JSON_RPC id").endParam();

        definition.responseMessage().code(200).message(operation.getResponseDescription()).endResponseMessage();

        RouteDefinition operationRouteDefinition = definition.route().routeId("sponge-get-" + operation.getMethod());

        initializeOperationRouteDefinition(operationRouteDefinition, operation);
        setupOperationRouteBeforeExecution(operationRouteDefinition, operation);
        operationRouteDefinition.process(createOperationExecutionProcessor(message -> {
            try {
                Map<String, Object> headers = message.getHeaders();
                List<String> components = new ArrayList<>();

                if (headers.containsKey(JsonRpcConstants.MEMBER_JSONRPC)) {
                    components.add(
                            String.format("\"%s\":\"%s\"", JsonRpcConstants.MEMBER_JSONRPC, headers.get(JsonRpcConstants.MEMBER_JSONRPC)));
                }

                if (headers.containsKey(JsonRpcConstants.MEMBER_METHOD)) {
                    components.add(
                            String.format("\"%s\":\"%s\"", JsonRpcConstants.MEMBER_METHOD, headers.get(JsonRpcConstants.MEMBER_METHOD)));
                }

                if (headers.containsKey(JsonRpcConstants.MEMBER_PARAMS)) {
                    String urlDecoded =
                            URLDecoder.decode((String) headers.get(JsonRpcConstants.MEMBER_PARAMS), StandardCharsets.UTF_8.name());
                    String params =
                            new String(Base64.getDecoder().decode(urlDecoded.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

                    components.add(String.format("\"%s\":%s", JsonRpcConstants.MEMBER_PARAMS, params));
                }

                if (headers.containsKey(JsonRpcConstants.MEMBER_ID)) {
                    components.add(String.format("\"%s\":\"%s\"", JsonRpcConstants.MEMBER_ID, headers.get(JsonRpcConstants.MEMBER_ID)));
                }

                return components.stream().collect(Collectors.joining(",", "{", "}"));
            } catch (UnsupportedEncodingException e) {
                throw SpongeUtils.wrapException(e);
            }
        }, operation));
        setupOperationRouteAfterExecution(operationRouteDefinition, operation);

        operationRouteDefinition.endRest();
    }

    protected <I extends SpongeRequest<P>, P, O extends SpongeResponse> void
            setupOperationRouteBeforeExecution(RouteDefinition operationRouteDefinition, RemoteApiOperation<I, P, O> operation) {
    }

    protected <I extends SpongeRequest<P>, P, O extends SpongeResponse> void
            setupOperationRouteAfterExecution(RouteDefinition operationRouteDefinition, RemoteApiOperation<I, P, O> operation) {
    }

    private <O extends SpongeResponse> OutputStreamValue getActionCallOutputStreamResponse(O response) {
        if (response instanceof ActionCallResponse) {
            Object actionCallResult = ((ActionCallResponse) response).getResult().getValue();
            if (actionCallResult instanceof OutputStreamValue) {
                return (OutputStreamValue) actionCallResult;
            }
        }

        return null;
    }

    protected RemoteApiSession createSession(Exchange exchange) {
        return new CamelRemoteApiSession(null, exchange);
    }

    protected FormDataMultiPartContext readFormDataMultiPartContext(RemoteApiOperation operation, Exchange exchange) throws Exception {
        HttpServletRequest httpRequest = exchange.getIn(HttpMessage.class).getRequest();

        if (!ServletFileUpload.isMultipartContent(httpRequest)) {
            return null;
        }

        ServletFileUpload upload = new ServletFileUpload();

        FileItemIterator iter = upload.getItemIterator(httpRequest);

        FormDataMultiPartContext multiPartContext = new FormDataMultiPartContext();

        if (iter.hasNext()) {
            FileItemStream item = iter.next();
            if (item.isFormField()) {
                try (InputStream inputStream = item.openStream()) {
                    String json = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());

                    multiPartContext.setJson(json);
                }
            }
        }

        if (multiPartContext.getJson() == null) {
            throw new JsonRpcServerException(JsonRpcConstants.ERROR_CODE_INTERNAL, "Missing a required JSON-RPC form field");
        }

        multiPartContext.setFileItemIterator(iter);

        return multiPartContext;
    }

    @SuppressWarnings({ "unchecked" })
    protected Processor createOperationExecutionProcessor(Function<Message, String> requestBodyProvider, RemoteApiOperation operation) {
        return exchange -> {
            FormDataMultiPartContext formDataMultiPartContext = null;
            if (operation.isSupportsFormDataMultiPart()) {
                formDataMultiPartContext = readFormDataMultiPartContext(operation, exchange);
            }

            exchange.setProperty(RemoteApiServerConstants.EXCHANGE_PROPERTY_FORM_DATA_MULTI_PART_CONTEXT, formDataMultiPartContext);

            String requestBody =
                    formDataMultiPartContext != null ? formDataMultiPartContext.getJson() : requestBodyProvider.apply(exchange.getIn());

            if (logger.isDebugEnabled()) {
                logger.debug("Remote API {} request: {}", operation.getMethod(), RemoteApiUtils.obfuscatePassword(requestBody));
            }

            boolean isJsonRpcOperation = Objects.equals(operation.getMethod(), RemoteApiConstants.ENDPOINT_JSONRPC);

            // Allow empty body if not strict JSON-RPC.
            if (!isJsonRpcOperation && StringUtils.isBlank(requestBody)) {
                requestBody = "{}";
            }

            exchange.setProperty(RemoteApiServerConstants.EXCHANGE_PROPERTY_REQUEST_TIME, Instant.now());

            try {
                // Open a new session. The user will be set later in the service.
                apiService.openSession(createSession(exchange));

                RemoteApiOperation targetOperation;

                JsonNode requestNode;
                try {
                    requestNode = getObjectMapper().readTree(requestBody);
                } catch (JsonProcessingException e) {
                    throw new JsonRpcServerException(JsonRpcConstants.ERROR_CODE_PARSE, e.getMessage());
                }

                if (requestNode.isArray()) {
                    throw new JsonRpcServerException(JsonRpcConstants.ERROR_CODE_INTERNAL, "JSON-RPC batch is not supported");
                } else if (!requestNode.isObject()) {
                    throw new JsonRpcServerException(JsonRpcConstants.ERROR_CODE_INVALID_REQUEST, "The request should be an object");
                }

                GenericRequest genericRequest;

                try {
                    genericRequest = getObjectMapper().convertValue(requestNode, GenericRequest.class);
                } catch (Exception e) {
                    throw new JsonRpcServerException(JsonRpcConstants.ERROR_CODE_INVALID_REQUEST, e.getMessage());
                }

                exchange.setProperty(RemoteApiServerConstants.EXCHANGE_PROPERTY_REQUEST_ID, genericRequest.getId());

                if (isJsonRpcOperation) {
                    exchange.setProperty(RemoteApiServerConstants.EXCHANGE_PROPERTY_IS_NOTIFICATION,
                            !requestNode.has(JsonRpcConstants.MEMBER_ID));

                    if (genericRequest.getMethod() == null) {
                        throw new JsonRpcServerException(JsonRpcConstants.ERROR_CODE_INVALID_REQUEST, "JSON-RPC method is missing");
                    }

                    if (!requestNode.has(JsonRpcConstants.MEMBER_JSONRPC)) {
                        throw new JsonRpcServerException(JsonRpcConstants.ERROR_CODE_INVALID_REQUEST, "JSON-RPC version is missing");
                    }

                    if (!Objects.equals(genericRequest.getJsonrpc(), JsonRpcConstants.VERSION)) {
                        throw new JsonRpcServerException(JsonRpcConstants.ERROR_CODE_INVALID_REQUEST,
                                String.format("Only JSON-RPC version %s is supported", JsonRpcConstants.VERSION));
                    }

                    targetOperation = operations.get(genericRequest.getMethod());

                    // Excluding the jsonrpc endpoint as the target.
                    if (targetOperation == null || targetOperation == operation) {
                        throw new JsonRpcServerException(JsonRpcConstants.ERROR_CODE_METHOD_NOT_FOUND,
                                String.format("JSON-RPC method '%s' not found", genericRequest.getMethod()));
                    }
                } else {
                    exchange.setProperty(RemoteApiServerConstants.EXCHANGE_PROPERTY_IS_NOTIFICATION, false);

                    if (genericRequest.getMethod() != null) {
                        if (!genericRequest.getMethod().equals(operation.getMethod())) {
                            throw new JsonRpcServerException(JsonRpcConstants.ERROR_CODE_INVALID_REQUEST,
                                    String.format("Invalid JSON-RPC method '%s' for the '%s' endpoint", genericRequest.getMethod(),
                                            operation.getMethod()));
                        }
                    } else {
                        genericRequest.setMethod(operation.getMethod());
                    }

                    targetOperation = operation;
                }

                // Create target request class by reflection constructor and set members.
                SpongeRequest targetRequest = (SpongeRequest) targetOperation.getRequestClass().newInstance();
                targetRequest.setMethod(genericRequest.getMethod());
                targetRequest.setId(genericRequest.getId());

                try {
                    targetRequest.setParams((RequestParams) getObjectMapper().convertValue(genericRequest.getParams(),
                            targetOperation.getRequestParamsClass()));
                } catch (IllegalArgumentException e) {
                    throw new JsonRpcServerException(JsonRpcConstants.ERROR_CODE_INVALID_PARAMS, e.getMessage());
                }

                // Set empty params and header if none.
                if (targetRequest.getParams() == null) {
                    targetRequest.setParams(targetRequest.createParams());
                }

                if (targetRequest.getHeader() == null) {
                    targetRequest.setHeader(new RequestHeader());
                }

                SpongeResponse response = targetOperation.getHandler().handle(getRemoteApiService(), targetRequest, exchange);

                // Handle an action call that returns a stream.
                OutputStreamValue streamValue = getActionCallOutputStreamResponse(response);
                if (streamValue == null) {
                    setupResponse(operation.getMethod(), exchange, response, !isNotification(exchange));
                } else {
                    setupStreamResponse(operation.getMethod(), exchange, streamValue);
                }

                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, isNotification(exchange)
                        ? RemoteApiConstants.HTTP_RESPONSE_CODE_NO_RESPONSE : RemoteApiConstants.HTTP_RESPONSE_CODE_OK);
            } finally {
                // Close the session.
                apiService.closeSession();
            }
        };
    }

    protected <I extends SpongeRequest<P>, P, O extends SpongeResponse> void addOperation(RemoteApiOperation<I, P, O> operation) {
        Validate.isTrue(!operations.containsKey(operation.getMethod()), "The method '%s' has already been defined", operation.getMethod());

        operations.put(operation.getMethod(), operation);
    }

    @SuppressWarnings("unchecked")
    protected void createDefaultOperations() {
        // Add the JSON-RPC endpoint operation.
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.ENDPOINT_JSONRPC, "JSON-RPC endpoint", SpongeRequest.class, Map.class,
                "The JSON-RPC request", SpongeResponse.class, "The JSON-RPC response", (service, request, exchange) -> {
                    throw new UnsupportedOperationException("The JSON-RPC endpoint should delegate to a target operation");
                }));

        addOperation(new RemoteApiOperation<>(RemoteApiConstants.METHOD_VERSION, "Get the Sponge version", GetVersionRequest.class,
                BaseRequestParams.class, "The get Sponge version request", GetVersionResponse.class, "The Sponge version response",
                (service, request, exchange) -> service.getVersion(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.METHOD_FEATURES, "Get the API features", GetFeaturesRequest.class,
                BaseRequestParams.class, "The get API features request", GetFeaturesResponse.class, "The API features response",
                (service, request, exchange) -> service.getFeatures(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.METHOD_LOGIN, "Login", LoginRequest.class, BaseRequestParams.class,
                "The login request", LoginResponse.class, "The login response", (service, request, exchange) -> service.login(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.METHOD_LOGOUT, "Logout", LogoutRequest.class, BaseRequestParams.class,
                "The logout request", LogoutResponse.class, "The logout response",
                (service, request, exchange) -> service.logout(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.METHOD_KNOWLEDGE_BASES, "Get knowledge bases",
                GetKnowledgeBasesRequest.class, BaseRequestParams.class, "The get knowledge bases request", GetKnowledgeBasesResponse.class,
                "The get knowledge bases response", (service, request, exchange) -> service.getKnowledgeBases(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.METHOD_ACTIONS, "Get actions", GetActionsRequest.class,
                GetActionsParams.class, "The get actions request", GetActionsResponse.class, "The get actions response",
                (service, request, exchange) -> service.getActions(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.METHOD_CALL, "Call an action", ActionCallRequest.class,
                ActionCallParams.class, "The call action request", ActionCallResponse.class, "The action call response",
                (service, request, exchange) -> service.call(request), true));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.METHOD_IS_ACTION_ACTIVE, "Is action active", IsActionActiveRequest.class,
                IsActionActiveParams.class, "The action active request", IsActionActiveResponse.class, "The action active response",
                (service, request, exchange) -> service.isActionActive(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.METHOD_PROVIDE_ACTION_ARGS, "Provide action arguments",
                ProvideActionArgsRequest.class, ProvideActionArgsParams.class, "The provide action arguments request",
                ProvideActionArgsResponse.class, "The provide action arguments response",
                (service, request, exchange) -> service.provideActionArgs(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.METHOD_SEND, "Send a new event", SendEventRequest.class,
                SendEventParams.class, "The send event request", SendEventResponse.class, "The send event response",
                (service, request, exchange) -> service.send(request)));
        addOperation(new RemoteApiOperation<>(RemoteApiConstants.METHOD_EVENT_TYPES, "Get event types", GetEventTypesRequest.class,
                GetEventTypesParams.class, "The get event types request", GetEventTypesResponse.class, "The get event types response",
                (service, request, exchange) -> service.getEventTypes(request)));

        if (getSettings().isPublishReload()) {
            addOperation(new RemoteApiOperation<>(RemoteApiConstants.METHOD_RELOAD, "Reload knowledge bases", ReloadRequest.class,
                    BaseRequestParams.class, "The reload request", ReloadResponse.class, "The reload response",
                    (service, request, exchange) -> service.reload(request)));
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

        operations.forEach((name, operation) -> createOperation(definition, operation));
    }
}
