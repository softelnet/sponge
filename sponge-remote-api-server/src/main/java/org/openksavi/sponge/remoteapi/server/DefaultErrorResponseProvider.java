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

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.openksavi.sponge.action.InactiveActionException;
import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.model.response.ResponseError;
import org.openksavi.sponge.remoteapi.model.response.ResponseError.ErrorData;
import org.openksavi.sponge.remoteapi.model.response.SpongeResponse;
import org.openksavi.sponge.remoteapi.util.RemoteApiUtils;

/**
 * A default error response provider.
 */
@SuppressWarnings("rawtypes")
public class DefaultErrorResponseProvider implements ErrorResponseProvider {

    @Override
    public void applyException(RemoteApiService service, SpongeResponse response, Throwable exception) {
        response.setError(new ResponseError());

        response.getError().setCode(RemoteApiConstants.ERROR_CODE_GENERIC);

        // There is a possibility that exceptions thrown in Camel would contain a full request with a password, so it must be hidden
        // here because it could be sent to a client.
        response.getError().setMessage(RemoteApiUtils.obfuscatePassword(exception.getMessage()));

        if (service.getSettings().isIncludeDetailedErrorMessage()) {
            response.getError().setData(new ErrorData(RemoteApiUtils.obfuscatePassword(ExceptionUtils.getStackTrace(exception))));
        }

        if (exception instanceof JsonRpcServerException) {
            int code = ((JsonRpcServerException) exception).getCode();

            response.getError().setCode(code);

            String standardMessage = RemoteApiUtils.getErrorMessage(code);

            if (standardMessage != null) {
                response.getError().setMessage(String.format("%s. %s", standardMessage, response.getError().getMessage()));
            }
        } else {
            // Specific error codes.
            applySpecificErrorCodes(service, response, exception);
        }
    }

    protected void applySpecificErrorCodes(RemoteApiService service, SpongeResponse response, Throwable exception) {
        if (exception instanceof InvalidAuthTokenServerException) {
            response.getError().setCode(RemoteApiConstants.ERROR_CODE_INVALID_AUTH_TOKEN);
        } else if (exception instanceof InvalidKnowledgeBaseVersionServerException) {
            response.getError().setCode(RemoteApiConstants.ERROR_CODE_INVALID_KB_VERSION);
        } else if (exception instanceof InvalidUsernamePasswordServerException) {
            response.getError().setCode(RemoteApiConstants.ERROR_CODE_INVALID_USERNAME_PASSWORD);
        } else if (exception instanceof InactiveActionException) {
            response.getError().setCode(RemoteApiConstants.ERROR_CODE_INACTIVE_ACTION);
        }
    }
}
