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

package org.openksavi.sponge.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.script.ScriptException;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.core.util.exception.ScriptExceptionErrorLocationTextInExceptionMessageFinder;
import org.openksavi.sponge.core.util.exception.SpongeExceptionUtils;
import org.openksavi.sponge.core.util.exception.WrappedExceptionErrorLocationTextInExceptionMessageFinder;
import org.openksavi.sponge.engine.WrappedException;

public class SpongeExceptionUtilsTest {

    @Test
    public void testWrappedExceptionErrorLocationTextInExceptionMessageFinder() {
        WrappedException exception = new WrappedException("SOURCE", new Exception());

        assertEquals(" at SOURCE",
                new WrappedExceptionErrorLocationTextInExceptionMessageFinder().getErrorLocationText(exception).get());
    }

    @Test
    public void testScriptExceptionErrorLocationTextInExceptionMessageFinder() {
        ScriptException exception = new ScriptException("MESSAGE", "FILE_NAME", 10, 20);

        assertEquals(exception.getMessage().substring("MESSAGE".length()),
                new ScriptExceptionErrorLocationTextInExceptionMessageFinder().getErrorLocationText(exception).get());
    }

    @Test
    public void testGetErrorMessageWithoutErrorLocationForScriptExceptionAndWrappedException() {
        ScriptException scriptException = new ScriptException("MESSAGE", "FILE_NAME", 10, 20);
        WrappedException exception = new WrappedException("SOURCE", scriptException);

        assertEquals("MESSAGE", SpongeExceptionUtils.getErrorMessageWithoutErrorLocation(exception));
    }

    @Test
    public void testGetErrorMessageWithoutErrorLocationForScriptException() {
        ScriptException exception = new ScriptException("MESSAGE", "FILE_NAME", 10, 20);

        assertEquals("MESSAGE", SpongeExceptionUtils.getErrorMessageWithoutErrorLocation(exception));
    }

    @Test
    public void testGetErrorMessageWithoutErrorLocationForWrappedException() {
        WrappedException exception = new WrappedException("SOURCE", new Exception("MESSAGE"));

        assertEquals("MESSAGE", SpongeExceptionUtils.getErrorMessageWithoutErrorLocation(exception));
    }

    @Test
    public void testGetErrorMessageWithoutErrorLocationForOtherException() {
        Exception exception = new Exception("MESSAGE");

        assertEquals("MESSAGE", SpongeExceptionUtils.getErrorMessageWithoutErrorLocation(exception));
    }

    @Test
    public void testGetErrorMessageWithoutErrorLocationForOtherExceptionWithNullMessage() {
        Exception exception = new Exception();

        assertNull(SpongeExceptionUtils.getErrorMessageWithoutErrorLocation(exception));
    }
}
