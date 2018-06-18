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

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.openksavi.sponge.restapi.model.response.BaseRestResponse;

/**
 * A default error response provider.
 */
public class DefaultRestApiErrorResponseProvider implements RestApiErrorResponseProvider {

    /** A generic error code. */
    protected static final String DEFAULT_ERROR_CODE = "SPONGE001";

    @Override
    public void applyException(BaseRestResponse response, Throwable exception) {
        response.setErrorCode(DEFAULT_ERROR_CODE);
        response.setErrorMessage(exception.getMessage());
        response.setDetailedErrorMessage(ExceptionUtils.getStackTrace(exception));
    }
}
