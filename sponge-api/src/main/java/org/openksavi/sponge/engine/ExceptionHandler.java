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

package org.openksavi.sponge.engine;

/**
 * Exception handler that is used to handle exceptions that are thrown in other threads than the thread that started the engine. For example
 * handles exceptions that are thrown in event processors.
 */
public interface ExceptionHandler {

    /**
     * Handles an exception.
     *
     * @param exception an exception.
     * @param context an exception context.
     */
    void handleException(Throwable exception, ExceptionContext context);
}
