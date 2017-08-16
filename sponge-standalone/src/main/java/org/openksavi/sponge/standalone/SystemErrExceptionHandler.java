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

package org.openksavi.sponge.standalone;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import org.openksavi.sponge.engine.ExceptionContext;
import org.openksavi.sponge.engine.ExceptionHandler;

/**
 * System.err exception handler.
 */
public class SystemErrExceptionHandler implements ExceptionHandler {

    public SystemErrExceptionHandler() {
        //
    }

    @Override
    public void handleException(Throwable exception, ExceptionContext context) {
        String message = "Error" + ((context.getSourceName() != null) ? (" in " + context.getSourceName()) : "") + ": ";

        System.err.println(message);
        Throwable cause = ObjectUtils.defaultIfNull(context.getEngine().getKnowledgeBaseManager().unwrapKnowledgeBaseException(exception),
                ExceptionUtils.getRootCause(exception));
        if (cause != null) {
            cause.printStackTrace();
        } else {
            exception.printStackTrace();
        }
    }
}
