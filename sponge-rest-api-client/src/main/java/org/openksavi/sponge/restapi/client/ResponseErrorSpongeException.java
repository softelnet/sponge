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

package org.openksavi.sponge.restapi.client;

/**
 * Response error exception.
 */
public class ResponseErrorSpongeException extends ClientSpongeException {

    private static final long serialVersionUID = 3962360804569372072L;

    private String errorCode;

    private String detailedErrorMessage;

    /**
     * Creates a new exception.
     *
     * @param message exception message.
     */
    public ResponseErrorSpongeException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     *
     * @param throwable source throwable.
     */
    public ResponseErrorSpongeException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Creates a new exception.
     *
     * @param message exception message.
     * @param throwable source throwable.
     */
    public ResponseErrorSpongeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDetailedErrorMessage() {
        return detailedErrorMessage;
    }

    public void setDetailedErrorMessage(String detailedErrorMessage) {
        this.detailedErrorMessage = detailedErrorMessage;
    }
}
