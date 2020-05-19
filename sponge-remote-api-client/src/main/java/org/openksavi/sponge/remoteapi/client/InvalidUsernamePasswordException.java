/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.client;

public class InvalidUsernamePasswordException extends ErrorResponseException {

    private static final long serialVersionUID = -851651702111467494L;

    /**
     * Creates a new exception.
     *
     * @param message exception message.
     */
    public InvalidUsernamePasswordException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     *
     * @param throwable source throwable.
     */
    public InvalidUsernamePasswordException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Creates a new exception.
     *
     * @param message exception message.
     * @param throwable source throwable.
     */
    public InvalidUsernamePasswordException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
