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

package org.openksavi.sponge.remoteapi;

import org.openksavi.sponge.remoteapi.model.RemoteEvent;

/**
 * Sponge Remote API constants.
 */
public final class RemoteApiConstants {

    public static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";

    public static final int DEFAULT_PORT = 1836;

    public static final String PROTOCOL_VERSION = "1";

    public static final String ENDPOINT_JSONRPC = "jsonrpc";

    public static final String ENDPOINT_DOC = "doc";

    public static final String METHOD_VERSION = "version";

    public static final String METHOD_FEATURES = "features";

    public static final String METHOD_LOGIN = "login";

    public static final String METHOD_LOGOUT = "logout";

    public static final String METHOD_KNOWLEDGE_BASES = "knowledgeBases";

    public static final String METHOD_ACTIONS = "actions";

    public static final String METHOD_CALL = "call";

    public static final String METHOD_SEND = "send";

    public static final String METHOD_IS_ACTION_ACTIVE = "isActionActive";

    public static final String METHOD_PROVIDE_ACTION_ARGS = "provideActionArgs";

    public static final String METHOD_EVENT_TYPES = "eventTypes";

    public static final String METHOD_RELOAD = "reload";

    public static final String OPERATION_ID_VERSION = "getVersion";

    public static final String OPERATION_ID_FEATURES = "getFeatures";

    public static final String OPERATION_ID_LOGIN = "login";

    public static final String OPERATION_ID_LOGOUT = "logout";

    public static final String OPERATION_ID_KNOWLEDGE_BASES = "getKnowledgeBases";

    public static final String OPERATION_ID_ACTIONS = "getActions";

    public static final String OPERATION_ID_CALL = "call";

    public static final String OPERATION_ID_SEND = "send";

    public static final String OPERATION_ID_IS_ACTION_ACTIVE = "isActionActive";

    public static final String OPERATION_ID_PROVIDE_ACTION_ARGS = "provideActionArgs";

    public static final String OPERATION_ID_EVENT_TYPES = "getEventTypes";

    public static final String OPERATION_ID_RELOAD = "reload";

    /** A generic error code. */
    public static final int ERROR_CODE_GENERIC = 1001;

    public static final int ERROR_CODE_INVALID_AUTH_TOKEN = 1002;

    public static final int ERROR_CODE_INVALID_KB_VERSION = 1003;

    public static final int ERROR_CODE_INVALID_USERNAME_PASSWORD = 1004;

    public static final int ERROR_CODE_INACTIVE_ACTION = 1005;

    public static final String REMOTE_API_FEATURE_SPONGE_VERSION = "spongeVersion";

    public static final String REMOTE_API_FEATURE_API_VERSION = "apiVersion";

    public static final String REMOTE_API_FEATURE_PROTOCOL_VERSION = "protocolVersion";

    public static final String REMOTE_API_FEATURE_GRPC_ENABLED = "grpcEnabled";

    public static final String REMOTE_API_FEATURE_NAME = "name";

    public static final String REMOTE_API_FEATURE_DESCRIPTION = "description";

    public static final String REMOTE_API_FEATURE_LICENSE = "license";

    public static final String REMOTE_EVENT_OBJECT_TYPE_CLASS_NAME = RemoteEvent.class.getName();

    public static final int HTTP_RESPONSE_CODE_OK = 200;

    public static final int HTTP_RESPONSE_CODE_ERROR = 500;

    public static final int HTTP_RESPONSE_CODE_NO_RESPONSE = 204;

    public static final String SERVICE_DISCOVERY_TYPE = "_sponge._tcp";

    public static final String SERVICE_DISCOVERY_PROPERTY_UUID = "uuid";

    public static final String SERVICE_DISCOVERY_PROPERTY_NAME = "name";

    public static final String SERVICE_DISCOVERY_PROPERTY_URL = "url";

    public static final String HTTP_HEADER_CONTENT_DISPOSITION = "Content-Disposition";

    public static final String ERROR_DATA_FIELD_DETAILED_ERROR_MESSAGE = "detailedErrorMessage";

    private RemoteApiConstants() {
        //
    }
}
