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

    public static final String URL_PREFIX = "/sponge/v";

    public static final String APPLICATION_JSON_VALUE = "application/json";

    public static final String DEFAULT_REST_COMPONENT_ID = "undertow";

    public static final int DEFAULT_PORT = 8080;

    public static final boolean DEFAULT_AUTO_START = true;

    public static final boolean DEFAULT_IS_ACTION_PUBLIC = true;

    public static final boolean DEFAULT_IS_EVENT_PUBLIC = true;

    public static final String REST_PARAM_ACTIONS_METADATA_REQUIRED_NAME = "metadataRequired";

    public static final Boolean REST_PARAM_ACTIONS_METADATA_REQUIRED_DEFAULT = true;

    public static final String ACTION_IS_ACTION_PUBLIC = "RestApiIsActionPublic";

    public static final String ACTION_IS_EVENT_PUBLIC = "RestApiIsEventPublic";

    public static final String TAG_REST_COMPONENT_ID = "restComponentId";

    public static final String TAG_HOST = "host";

    public static final String TAG_PORT = "port";

    public static final String TAG_PRETTY_PRINT = "prettyPrint";

    public static final String TAG_PUBLIC_ACTIONS = "publicActions";

    public static final String TAG_PUBLIC_EVENTS = "publicEvents";

    public static final String TAG_AUTO_START = "autoStart";

    private RestApiConstants() {
        //
    }
}