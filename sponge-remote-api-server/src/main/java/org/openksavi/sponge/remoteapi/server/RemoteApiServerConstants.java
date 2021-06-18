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

/**
 * Sponge Remote API constants.
 */
public final class RemoteApiServerConstants {

    public static final String DEFAULT_COMPONENT_ID = "jetty";

    public static final boolean DEFAULT_AUTO_START = true;

    public static final boolean DEFAULT_IS_ACTION_PUBLIC = true;

    public static final boolean DEFAULT_IS_EVENT_PUBLIC = true;

    public static final String DEFAULT_SSL_CONTEXT_PARAMETERS_BEAN_NAME = "spongeRemoteApiSslContextParameters";

    public static final boolean DEFAULT_PRETTY_PRINT = false;

    public static final boolean DEFAULT_PUBLISH_RELOAD = false;

    public static final boolean DEFAULT_ALLOW_ANONYMOUS = true;

    public static final Boolean API_PARAM_ACTIONS_METADATA_REQUIRED_DEFAULT = false;

    public static final String INTERNAL_ACTION_NAME_PREFIX = "RemoteApi";

    public static final String ACTION_IS_ACTION_PUBLIC = INTERNAL_ACTION_NAME_PREFIX + "IsActionPublic";

    public static final String ACTION_IS_EVENT_PUBLIC = INTERNAL_ACTION_NAME_PREFIX + "IsEventPublic";

    public static final String ACTION_CAN_USE_KNOWLEDGE_BASE = INTERNAL_ACTION_NAME_PREFIX + "CanUseKnowledgeBase";

    public static final String ACTION_CAN_SEND_EVENT = INTERNAL_ACTION_NAME_PREFIX + "CanSendEvent";

    public static final String ACTION_CAN_SUBSCRIBE_EVENT = INTERNAL_ACTION_NAME_PREFIX + "CanSubscribeEvent";

    public static final String TAG_COMPONENT_ID = "componentId";

    public static final String TAG_HOST = "host";

    public static final String TAG_PORT = "port";

    public static final String TAG_PATH = "path";

    public static final String TAG_NAME = "name";

    public static final String TAG_DESCRIPTION = "description";

    public static final String TAG_VERSION = "version";

    public static final String TAG_LICENSE = "license";

    public static final String TAG_PRETTY_PRINT = "prettyPrint";

    public static final String TAG_PUBLIC_ACTIONS = "publicActions";

    public static final String TAG_PUBLIC_EVENTS = "publicEvents";

    public static final String TAG_AUTO_START = "autoStart";

    public static final String TAG_SSL_CONFIGURATION = "sslConfiguration";

    public static final String TAG_PUBLISH_RELOAD = "publishReload";

    public static final String TAG_ALLOW_ANONYMOUS = "allowAnonymous";

    public static final String TAG_ROUTE_BUILDER_CLASS = "routeBuilderClass";

    public static final String TAG_API_SERVICE_CLASS = "apiServiceClass";

    public static final String TAG_SECURITY_PROVIDER_CLASS = "securityProviderClass";

    public static final String TAG_AUTH_TOKEN_SERVICE_CLASS = "authTokenServiceClass";

    public static final String TAG_AUTH_TOKEN_EXPIRATION_DURATION_SECONDS = "authTokenExpirationDurationSeconds";

    public static final String TAG_INCLUDE_RESPONSE_TIMES = "includeResponseTimes";

    public static final String TAG_REGISTER_SERVICE_DISCOVERY = "registerServiceDiscovery";

    public static final String TAG_SERVICE_DISCOVERY_URL = "serviceDiscoveryUrl";

    public static final String TAG_IGNORE_UNKNOWN_ARGS = "ignoreUnknownArgs";

    public static final String TAG_COPY_HTTP_REQUEST_HEADERS = "copyHttpRequestHeaders";

    public static final String TAG_CORS_ENABLED = "corsEnabled";

    public static final String TAG_OPEN_API_DOCS_FOR_GET_VERB_OPERATIONS = "openApiDocsForGetVerbOperations";

    public static final String TAG_OPEN_API_OPERATION_ID_SUFFIX_FOR_GET_VERB_OPERATIONS = "openApiOperationIdSuffixForGetVerbOperations";

    public static final String TAG_OPEN_API_DOCS_FOR_ENDPOINTS = "openApiDocsForEndpoints";

    public static final String DEFAULT_NAME = "Sponge";

    public static final String DEFAULT_ANONYMOUS_USERNAME = "anonymous";

    public static final String DEFAULT_ROLE_ADMIN = "admin";

    public static final String DEFAULT_ROLE_ANONYMOUS = "anonymous";

    public static final boolean DEFAULT_INCLUDE_DETAILED_ERROR_MESSAGE = false;

    public static final boolean DEFAULT_INCLUDE_RESPONSE_TIMES = false;

    public static final String DEFAULT_GET_VERB_OPERATION_ID_SUFFIX = "_GET";

    public static final String EXCHANGE_PROPERTY_METHOD_NAME = "methodName";

    public static final String EXCHANGE_PROPERTY_REQUEST_ID = "requestId";

    public static final String EXCHANGE_PROPERTY_REQUEST_TIME = "requestTime";

    public static final String EXCHANGE_PROPERTY_IS_NOTIFICATION = "isNotification";

    public static final String EXCHANGE_PROPERTY_FORM_DATA_MULTI_PART_CONTEXT = "formDataMultiPartContext";

    public static final String PROP_HOST = "sponge.remoteApiServer.host";

    public static final String PROP_PORT = "sponge.remoteApiServer.port";

    public static final String PROP_PATH = "sponge.remoteApiServer.path";

    public static final String PROP_NAME = "sponge.remoteApiServer.name";

    public static final String PROP_VERSION = "sponge.remoteApiServer.version";

    public static final String PROP_REGISTER_SERVICE_DISCOVERY = "sponge.remoteApiServer.registerServiceDiscovery";

    public static final String PROP_SERVICE_DISCOVERY_URL = "sponge.remoteApiServer.serviceDiscoveryUrl";

    private RemoteApiServerConstants() {
        //
    }
}
