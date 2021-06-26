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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;

import org.openksavi.sponge.engine.WrappedException;

public abstract class SpongeExceptionUtils {

    @SuppressWarnings("rawtypes")
    private static final Map<Class<? extends Throwable>,
            ErrorLocationTextInExceptionMessageFinder> errorLocationTextInExceptionMessageFinders = new HashMap<>();
    static {
        errorLocationTextInExceptionMessageFinders.put(ScriptException.class,
                new ScriptExceptionErrorLocationTextInExceptionMessageFinder());
        errorLocationTextInExceptionMessageFinders.put(WrappedException.class,
                new WrappedExceptionErrorLocationTextInExceptionMessageFinder());
    }

    private SpongeExceptionUtils() {
    }

    public static boolean containsException(Throwable exception, final Class<? extends Throwable> type) {
        return ExceptionUtils.indexOfType(exception, type) > -1;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getException(Throwable exception, final Class<? extends Throwable> type) {
        return (T) ExceptionUtils.getThrowableList(exception).get(ExceptionUtils.indexOfType(exception, type));
    }

    public static String getErrorMessageWithoutErrorLocation(Throwable exception) {
        String message = exception.getMessage();
        if (StringUtils.isEmpty(message)) {
            return message;
        }

        for (Class<? extends Throwable> exceptionClass : errorLocationTextInExceptionMessageFinders.keySet()) {
            if (containsException(exception, exceptionClass)) {
                message = getExceptionMessageWithoutErrorLocation(message, getException(exception, exceptionClass));
            }
        }

        return message;
    }

    @SuppressWarnings("unchecked")
    public static String getExceptionMessageWithoutErrorLocation(String message, Throwable exception) {
        if (StringUtils.isEmpty(message)) {
            return message;
        }

        Optional<String> errorLocation =
                errorLocationTextInExceptionMessageFinders.get(exception.getClass()).getErrorLocationText(exception);
        if (!errorLocation.isPresent()) {
            return message;
        }

        int errorLocationIndex = message.indexOf(errorLocation.get());
        Validate.isTrue(errorLocationIndex > -1, "Error location message in %s is incorrect", exception.getClass());

        return message.substring(0, errorLocationIndex) + message.substring(errorLocationIndex + errorLocation.get().length());
    }
}
