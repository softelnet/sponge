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

package org.openksavi.sponge.jython;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.python.core.PyString;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.jython.core.JythonKnowledgeBaseInterpreter;
import org.openksavi.sponge.test.util.TestUtils;

public class JythonKnowledgeBaseInterpreterTest {

    @Test
    void shouldJythonThrowException_whenStringIsNotAscii() {
        assertThrows(IllegalArgumentException.class, () -> new PyString("‑"));
    }

    @Test
    void shouldThrowException_whenFilenameIsNotAscii() {
        SpongeEngine engine = DefaultSpongeEngine.builder().knowledgeBaseString(TestUtils.DEFAULT_KB, PythonConstants.TYPE, "").build();
        engine.startup();

        try {
            JythonKnowledgeBaseInterpreter interpreter = (JythonKnowledgeBaseInterpreter) engine.getKnowledgeBaseManager()
                    .getKnowledgeBase(TestUtils.DEFAULT_KB).getInterpreter();

            SpongeException exception =
                    assertThrows(SpongeException.class, () -> interpreter.eval(new StringReader(""), "filename_with_unicode‑here"));
            assertEquals("Jython filename should contain only ASCII characters: filename_with_unicode‑here", exception.getMessage());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    void shouldNotThrowException_whenFilenameIsAscii() {
        SpongeEngine engine = DefaultSpongeEngine.builder().knowledgeBaseString(TestUtils.DEFAULT_KB, PythonConstants.TYPE, "").build();
        engine.startup();

        try {
            String filename = "filename_with_ascii";
            JythonKnowledgeBaseInterpreter interpreter = (JythonKnowledgeBaseInterpreter) engine.getKnowledgeBaseManager()
                    .getKnowledgeBase(TestUtils.DEFAULT_KB).getInterpreter();

            Throwable exception =
                    assertThrows(Throwable.class, () -> interpreter.eval(new StringReader("raise Exception('Here')"), filename));

            assertEquals("Exception: Here in filename_with_ascii at line number 1 at filename_with_ascii", exception.getMessage());
        } finally {
            engine.shutdown();
        }
    }
}
