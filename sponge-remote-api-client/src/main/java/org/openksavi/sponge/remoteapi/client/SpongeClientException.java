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

package org.openksavi.sponge.remoteapi.client;

import org.openksavi.sponge.SpongeException;

/**
 * Sponge client exception.
 */
public class SpongeClientException extends SpongeException {

    private static final long serialVersionUID = 2544877823390090783L;

    /**
     * Creates a new exception.
     *
     * @param message exception message.
     */
    public SpongeClientException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     *
     * @param throwable source throwable.
     */
    public SpongeClientException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Creates a new exception.
     *
     * @param message exception message.
     * @param throwable source throwable.
     */
    public SpongeClientException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
