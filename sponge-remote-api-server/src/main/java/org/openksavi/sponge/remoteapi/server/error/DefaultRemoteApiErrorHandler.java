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

package org.openksavi.sponge.remoteapi.server.error;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.action.InactiveActionException;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.core.util.exception.SpongeExceptionUtils;
import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.model.response.ErrorResponse;
import org.openksavi.sponge.remoteapi.model.response.ResponseError;
import org.openksavi.sponge.remoteapi.server.InvalidAuthTokenServerException;
import org.openksavi.sponge.remoteapi.server.InvalidKnowledgeBaseVersionServerException;
import org.openksavi.sponge.remoteapi.server.InvalidUsernamePasswordServerException;
import org.openksavi.sponge.remoteapi.server.JsonRpcServerException;
import org.openksavi.sponge.remoteapi.server.RemoteApiSettings;
import org.openksavi.sponge.remoteapi.util.RemoteApiUtils;

/**
 * A default error handler.
 */
public class DefaultRemoteApiErrorHandler extends BaseRemoteApiErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRemoteApiErrorHandler.class);

    public DefaultRemoteApiErrorHandler(RemoteApiSettings settings, Throwable exception) {
        super(settings, exception);
    }

    @Override
    public void logError() {
        logger.error("Remote API error", getException());
    }

    @Override
    public ErrorResponse createErrorResponse() {
        ResponseError error = new ResponseError();
        ErrorResponse errorResponse = new ErrorResponse(error);

        error.setCode(RemoteApiConstants.ERROR_CODE_GENERIC);
        error.setMessage(createErrorMessage());

        if (getSettings().isIncludeDetailedErrorMessage()) {
            Map<String, Object> errorData = SpongeUtils.immutableMapOf(RemoteApiConstants.ERROR_DATA_FIELD_DETAILED_ERROR_MESSAGE,
                    RemoteApiUtils.obfuscatePassword(ExceptionUtils.getStackTrace(getException())));
            error.setData(errorData);
        }

        if (getException() instanceof JsonRpcServerException) {
            int code = ((JsonRpcServerException) getException()).getCode();
            error.setCode(code);

            String standardMessage = RemoteApiUtils.getErrorMessage(code);
            if (standardMessage != null) {
                error.setMessage(String.format("%s. %s", standardMessage, error.getMessage()));
            }
        } else {
            // Specific error codes.
            applySpecificErrorCodes(error);
        }

        return errorResponse;
    }

    protected void applySpecificErrorCodes(ResponseError error) {
        if (getException() instanceof InvalidAuthTokenServerException) {
            error.setCode(RemoteApiConstants.ERROR_CODE_INVALID_AUTH_TOKEN);
        } else if (getException() instanceof InvalidKnowledgeBaseVersionServerException) {
            error.setCode(RemoteApiConstants.ERROR_CODE_INVALID_KB_VERSION);
        } else if (getException() instanceof InvalidUsernamePasswordServerException) {
            error.setCode(RemoteApiConstants.ERROR_CODE_INVALID_USERNAME_PASSWORD);
        } else if (getException() instanceof InactiveActionException) {
            error.setCode(RemoteApiConstants.ERROR_CODE_INACTIVE_ACTION);
        }
    }

    protected String createErrorMessage() {
        String message = getSettings().isIncludeErrorLocationInErrorMessage() ? getException().getMessage()
                : SpongeExceptionUtils.getErrorMessageWithoutErrorLocation(getException());

        // There is a possibility that exceptions thrown in Camel would contain a full request with a password, so it must be hidden
        // here because it could be sent to a client.
        return RemoteApiUtils.obfuscatePassword(message);
    }
}
