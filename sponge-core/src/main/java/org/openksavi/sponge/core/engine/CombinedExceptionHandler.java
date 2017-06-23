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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openksavi.sponge.engine.ExceptionContext;
import org.openksavi.sponge.engine.ExceptionHandler;

/**
 * Combined exception handler.
 */
public class CombinedExceptionHandler implements ExceptionHandler {

    private List<ExceptionHandler> handlers = new ArrayList<>();

    public CombinedExceptionHandler(ExceptionHandler... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));
    }

    @Override
    public void handleException(Throwable exception, ExceptionContext context) {
        handlers.forEach(handler -> handler.handleException(exception, context));
    }
}
