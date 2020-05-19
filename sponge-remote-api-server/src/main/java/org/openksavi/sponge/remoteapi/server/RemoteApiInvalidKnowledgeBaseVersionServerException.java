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

public class RemoteApiInvalidKnowledgeBaseVersionServerException extends ApplicationServerSpongeException {

    private static final long serialVersionUID = 5429837374793138193L;

    /**
     * Creates a new exception.
     *
     * @param message exception message.
     */
    public RemoteApiInvalidKnowledgeBaseVersionServerException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     *
     * @param throwable source throwable.
     */
    public RemoteApiInvalidKnowledgeBaseVersionServerException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Creates a new exception.
     *
     * @param message exception message.
     * @param throwable source throwable.
     */
    public RemoteApiInvalidKnowledgeBaseVersionServerException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
