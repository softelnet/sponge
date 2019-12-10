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

package org.openksavi.sponge.core.engine;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.ExceptionContext;
import org.openksavi.sponge.engine.ExceptionHandler;

/**
 * System.err exception handler.
 */
public class SystemErrExceptionHandler implements ExceptionHandler {

    private boolean includeStackTrace;

    private boolean simplified;

    public SystemErrExceptionHandler(boolean includeStackTrace, boolean simplified) {
        this.includeStackTrace = includeStackTrace;
        this.simplified = simplified;
    }

    public SystemErrExceptionHandler() {
        this(true, false);
    }

    public boolean isIncludeStackTrace() {
        return includeStackTrace;
    }

    public void setIncludeStackTrace(boolean includeStackTrace) {
        this.includeStackTrace = includeStackTrace;
    }

    public boolean isSimplified() {
        return simplified;
    }

    public void setSimplified(boolean simplified) {
        this.simplified = simplified;
    }

    @Override
    public void handleException(Throwable exception, ExceptionContext context) {
        String message = exception.getLocalizedMessage();

        if (message == null) {
            message = exception.toString();
        }

        System.err.println(simplified ? "ERROR: " + message : SpongeUtils.createErrorMessage(exception, context, includeStackTrace));
    }
}
