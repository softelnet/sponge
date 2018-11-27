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

    public static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";

    public static final int DEFAULT_PORT = 1836;

    public static final String DEFAULT_PATH = String.format("sponge.json/v%d", API_VERSION);

    public static final String OPERATION_VERSION = "version";

    public static final String OPERATION_LOGIN = "login";

    public static final String OPERATION_LOGOUT = "logout";

    public static final String OPERATION_KNOWLEDGE_BASES = "knowledgeBases";

    public static final String OPERATION_ACTIONS = "actions";

    public static final String OPERATION_CALL = "call";

    public static final String OPERATION_SEND = "send";

    public static final String OPERATION_RELOAD = "reload";

    /** A generic error code. */
    public static final String DEFAULT_ERROR_CODE = "SPONGE001";

    public static final String ERROR_CODE_INVALID_AUTH_TOKEN = "SPONGE002";

    public static final String ERROR_CODE_INCORRECT_KNOWLEDGE_BASE_VERSION = "SPONGE003";

    private RestApiConstants() {
        //
    }
}
