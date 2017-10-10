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

package org.openksavi.sponge.engine.interactive;

import org.openksavi.sponge.engine.ExceptionHandler;

/**
 * Interactive mode loop.
 */
public interface InteractiveMode {

    /**
     * The interactive mode loop.
     */
    void loop();

    /**
     * Returns {@code true} if this interactive mode is running the loop.
     *
     * @return {@code true} if this interactive mode is running the loop.
     */
    boolean isRunning();

    /**
     * Returns the console.
     *
     * @return the console.
     */
    InteractiveModeConsole getConsole();

    /**
     * Sets the exception handler.
     *
     * @param exceptionHandler the exception handler.
     */
    void setExceptionHandler(ExceptionHandler exceptionHandler);

    /**
     * Returns the exception handler.
     *
     * @return the exception handler.
     */
    ExceptionHandler getExceptionHandler();
}
