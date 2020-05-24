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

    public static final String OPERATION_VERSION = "version";

    public static final String OPERATION_FEATURES = "features";

    public static final String OPERATION_LOGIN = "login";

    public static final String OPERATION_LOGOUT = "logout";

    public static final String OPERATION_KNOWLEDGE_BASES = "knowledgeBases";

    public static final String OPERATION_ACTIONS = "actions";

    public static final String OPERATION_CALL = "call";

    public static final String OPERATION_CALL_NAMED = "callNamed";

    public static final String OPERATION_SEND = "send";

    public static final String OPERATION_IS_ACTION_ACTIVE = "isActionActive";

    public static final String OPERATION_PROVIDE_ACTION_ARGS = "provideActionArgs";

    public static final String OPERATION_EVENT_TYPES = "eventTypes";

    public static final String OPERATION_RELOAD = "reload";

    public static final String OPERATION_DOC = "doc";

    public static final String OPERATION_JSON_RPC = "jsonrpc";

    /** A generic error code. */
    public static final int ERROR_CODE_GENERIC = 1001;

    public static final int ERROR_CODE_INVALID_AUTH_TOKEN = 1002;

    public static final int ERROR_CODE_INVALID_KB_VERSION = 1003;

    public static final int ERROR_CODE_INVALID_USERNAME_PASSWORD = 1004;

    public static final int ERROR_CODE_INACTIVE_ACTION = 1005;

    public static final int ERROR_CODE_STANDARD_PARSE = -32700;

    public static final int ERROR_CODE_STANDARD_INVALID_REQUEST = -32600;

    public static final int ERROR_CODE_STANDARD_METHOD_NOT_FOUND = -32601;

    public static final int ERROR_CODE_STANDARD_INVALID_PARAMS = -32602;

    public static final int ERROR_CODE_STANDARD_INTERNAL = -32603;

    public static final String REMOTE_API_FEATURE_SPONGE_VERSION = "spongeVersion";

    public static final String REMOTE_API_FEATURE_API_VERSION = "apiVersion";

    public static final String REMOTE_API_FEATURE_GRPC_ENABLED = "grpcEnabled";

    public static final String REMOTE_API_FEATURE_NAME = "name";

    public static final String REMOTE_API_FEATURE_DESCRIPTION = "description";

    public static final String REMOTE_API_FEATURE_LICENSE = "license";

    public static final String REMOTE_EVENT_OBJECT_TYPE_CLASS_NAME = RemoteEvent.class.getName();

    public static final int HTTP_CODE_ERROR = 500;

    public static final String SERVICE_DISCOVERY_TYPE = "_sponge._tcp";

    public static final String SERVICE_DISCOVERY_PROPERTY_UUID = "uuid";

    public static final String SERVICE_DISCOVERY_PROPERTY_NAME = "name";

    public static final String SERVICE_DISCOVERY_PROPERTY_URL = "url";

    private RemoteApiConstants() {
        //
    }
}
