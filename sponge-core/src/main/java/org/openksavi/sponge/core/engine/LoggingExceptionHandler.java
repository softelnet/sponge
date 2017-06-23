/*
 * Copyright 2016-2017 Softelnet.
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

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

import org.openksavi.sponge.engine.ExceptionContext;
import org.openksavi.sponge.engine.ExceptionHandler;
import org.openksavi.sponge.engine.WrappedException;

/**
 * Logging exception handler.
 */
public class LoggingExceptionHandler implements ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoggingExceptionHandler.class);

    @Override
    public void handleException(Throwable exception, ExceptionContext context) {
        StringBuilder sb = new StringBuilder();

        sb.append("Error" + ((context.getSourceName() != null) ? (" in " + context.getSourceName()) : "") + ":\n");
        sb.append(Throwables.getCausalChain(exception instanceof WrappedException ? exception.getCause() : exception).stream()
                .map(e -> " * " + e.toString()).collect(Collectors.joining("\n")));

        sb.append("\nStack trace:\n" + Throwables.getStackTraceAsString(exception));
        logger.error(sb.toString());
    }
}
