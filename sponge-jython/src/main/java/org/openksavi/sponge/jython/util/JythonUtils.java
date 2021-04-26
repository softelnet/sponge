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

package org.openksavi.sponge.jython.util;

import javax.script.ScriptException;

import org.python.core.PyException;

import org.openksavi.sponge.core.util.SpongeUtils;

public abstract class JythonUtils {

    protected JythonUtils() {
        //
    }

    public static String getSpecificExceptionMessage(Throwable e) {
        // Remove unnecessary leading exception class names added by Jython.
        if (e.getMessage() != null && (e instanceof ScriptException || e instanceof PyException)) {
            return SpongeUtils.removeLeadingExceptionClassNamesFromMessage(e);
        }

        // Jython exception message may be null.
        if (e.getMessage() == null && e instanceof PyException) {
            return e.toString();
        }

        return null;
    }
}
