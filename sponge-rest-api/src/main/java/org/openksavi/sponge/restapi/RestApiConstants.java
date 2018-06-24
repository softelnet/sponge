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

/**
 * Sponge REST API constants.
 */
public final class RestApiConstants {

    public static final int API_VERSION = 1;

    public static final String BASE_URL = String.format("sponge.json/v%d", API_VERSION);

    public static final String APPLICATION_JSON_VALUE = "application/json";

    public static final String DEFAULT_REST_COMPONENT_ID = "jetty";

    public static final int DEFAULT_PORT = 1836;

    public static final boolean DEFAULT_AUTO_START = true;

    public static final boolean DEFAULT_IS_ACTION_PUBLIC = true;

    public static final boolean DEFAULT_IS_EVENT_PUBLIC = true;

    public static final String DEFAULT_SSL_CONTEXT_PARAMETERS_BEAN_NAME = "spongeRestApiSslContextParameters";

    public static final boolean DEFAULT_PRETTY_PRINT = true;

    public static final boolean DEFAULT_PUBLISH_RELOAD = true;

    public static final boolean DEFAULT_ALLOW_ANONYMOUS = true;

    public static final String REST_PARAM_ACTIONS_METADATA_REQUIRED_NAME = "metadataRequired";

    public static final Boolean REST_PARAM_ACTIONS_METADATA_REQUIRED_DEFAULT = true;

    public static final String PRIVATE_ACTION_NAME_PREFIX = "RestApi";

    public static final String ACTION_IS_ACTION_PUBLIC = PRIVATE_ACTION_NAME_PREFIX + "IsActionPublic";

    public static final String ACTION_IS_EVENT_PUBLIC = PRIVATE_ACTION_NAME_PREFIX + "IsEventPublic";

    public static final String ACTION_CAN_USE_KNOWLEDGE_BASE = PRIVATE_ACTION_NAME_PREFIX + "CanUseKnowledgeBase";

    public static final String ACTION_CAN_SEND_EVENT = PRIVATE_ACTION_NAME_PREFIX + "CanSendEvent";

    public static final String TAG_REST_COMPONENT_ID = "restComponentId";

    public static final String TAG_HOST = "host";

    public static final String TAG_PORT = "port";

    public static final String TAG_PRETTY_PRINT = "prettyPrint";

    public static final String TAG_PUBLIC_ACTIONS = "publicActions";

    public static final String TAG_PUBLIC_EVENTS = "publicEvents";

    public static final String TAG_AUTO_START = "autoStart";

    public static final String TAG_SSL_CONFIGURATION = "sslConfiguration";

    public static final String TAG_PUBLISH_RELOAD = "publishReload";

    public static final String TAG_ALLOW_ANONYMOUS = "allowAnonymous";

    public static final String TAG_ROUTE_BUILDER_CLASS = "routeBuilderClass";

    public static final String TAG_API_SERVICE_CLASS = "apiServiceClass";

    public static final String TAG_SECURITY_SERVICE_CLASS = "securityServiceClass";

    public static final String DEFAULT_ANONYMOUS_USERNAME = "anonymous";

    public static final String DEFAULT_ROLE_ADMIN = "admin";

    public static final boolean DEFAULT_INCLUDE_DETAILED_ERROR_MESSAGE = true;

    public static final String PREDEFINED_ROLE_GUEST = "guest";

    private RestApiConstants() {
        //
    }
}
