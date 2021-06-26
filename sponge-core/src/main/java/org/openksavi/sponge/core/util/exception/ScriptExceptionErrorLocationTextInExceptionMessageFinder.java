/*
 * Copyright 2016-2021 The Sponge authors.
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

package org.openksavi.sponge.core.util.exception;

import java.util.Optional;

import javax.script.ScriptException;

public class ScriptExceptionErrorLocationTextInExceptionMessageFinder
        implements ErrorLocationTextInExceptionMessageFinder<ScriptException> {

    @Override
    public Optional<String> getErrorLocationText(ScriptException exception) {
        if (exception.getFileName() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(" in " + exception.getFileName());
            if (exception.getLineNumber() != -1) {
                sb.append(" at line number " + exception.getLineNumber());
            }

            if (exception.getColumnNumber() != -1) {
                sb.append(" at column number " + exception.getColumnNumber());
            }

            return Optional.of(sb.toString());
        }

        return Optional.empty();
    }
}
