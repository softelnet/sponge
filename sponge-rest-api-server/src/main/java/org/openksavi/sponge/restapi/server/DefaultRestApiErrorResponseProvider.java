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

package org.openksavi.sponge.restapi.server;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.model.response.BaseResponse;
import org.openksavi.sponge.restapi.server.util.RestApServeriUtils;

/**
 * A default error response provider.
 */
public class DefaultRestApiErrorResponseProvider implements RestApiErrorResponseProvider {

    @Override
    public void applyException(RestApiService service, BaseResponse response, Throwable exception) {
        response.setErrorCode(RestApiConstants.DEFAULT_ERROR_CODE);

        // There is a possibility that exceptions thrown in Camel would contain a full request with a password, so it must be hidden
        // here because it could be sent to a client.
        response.setErrorMessage(RestApServeriUtils.hidePassword(exception.getMessage()));

        if (service.getSettings().isIncludeDetailedErrorMessage()) {
            response.setDetailedErrorMessage(RestApServeriUtils.hidePassword(ExceptionUtils.getStackTrace(exception)));
        }

        // Specific error codes.
        applySpecificErrorCodes(service, response, exception);
    }

    protected void applySpecificErrorCodes(RestApiService service, BaseResponse response, Throwable exception) {
        if (exception instanceof RestApiInvalidAuthTokenServerException) {
            response.setErrorCode(RestApiConstants.ERROR_CODE_INVALID_AUTH_TOKEN);
        } else if (exception instanceof RestApiIncorrectKnowledgeBaseVersionServerException) {
            response.setErrorCode(RestApiConstants.ERROR_CODE_INCORRECT_KNOWLEDGE_BASE_VERSION);
        }
    }
}
