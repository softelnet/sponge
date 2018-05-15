/*
 * Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.engine;

import org.openksavi.sponge.SpongeException;

/**
 * Exception that wraps other exception.
 */
public class WrappedException extends SpongeException {

    private static final long serialVersionUID = -7842020620067616297L;

    private String sourceName;

    public WrappedException(String sourceName, Throwable throwable) {
        this(sourceName, throwable.getMessage(), throwable);
    }

    public WrappedException(String sourceName, String message, Throwable throwable) {
        super(message, throwable);

        this.sourceName = sourceName;
    }

    public String getSourceName() {
        return sourceName;
    }
}
