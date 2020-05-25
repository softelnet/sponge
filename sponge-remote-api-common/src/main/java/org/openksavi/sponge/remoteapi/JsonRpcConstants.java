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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JSON-RPC 2.0 constants.
 */
public final class JsonRpcConstants {

    public static final int ERROR_CODE_PARSE = -32700;

    public static final int ERROR_CODE_INVALID_REQUEST = -32600;

    public static final int ERROR_CODE_METHOD_NOT_FOUND = -32601;

    public static final int ERROR_CODE_INVALID_PARAMS = -32602;

    public static final int ERROR_CODE_INTERNAL = -32603;

    public static final String MEMBER_METHOD = "method";

    public static final String MEMBER_ID = "id";

    public static final String MEMBER_JSONRPC = "jsonrpc";

    public static final String MEMBER_PARAMS = "params";

    public static final String VERSION = "2.0";

    public static final Map<Integer, String> ERROR_MESSAGES = new LinkedHashMap<>();

    static {
        ERROR_MESSAGES.put(JsonRpcConstants.ERROR_CODE_PARSE, "Parse error");
        ERROR_MESSAGES.put(JsonRpcConstants.ERROR_CODE_INVALID_REQUEST, "Invalid request");
        ERROR_MESSAGES.put(JsonRpcConstants.ERROR_CODE_METHOD_NOT_FOUND, "Method not found");
        ERROR_MESSAGES.put(JsonRpcConstants.ERROR_CODE_INVALID_PARAMS, "Invalid params");
        ERROR_MESSAGES.put(JsonRpcConstants.ERROR_CODE_INTERNAL, "Internal error");
    }

    private JsonRpcConstants() {
        //
    }
}
