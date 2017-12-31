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

package org.openksavi.sponge.core.engine.interactive;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.interactive.InteractiveModeConsole;

/**
 * Simple interactive mode console.
 */
public class SimpleInteractiveModeConsole implements InteractiveModeConsole {

    private Console console;

    private BufferedReader reader;

    public SimpleInteractiveModeConsole(boolean open) {
        if (open) {
            open();
        }
    }

    public SimpleInteractiveModeConsole() {
        this(false);
    }

    @Override
    public void open() {
        console = System.console();
        reader = console != null ? new BufferedReader(console.reader()) : new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public boolean isOpen() {
        return reader != null;
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            reader = null;
        }
    }

    @Override
    public void print(String text) {
        if (console != null) {
            console.printf(text);
        } else {
            System.out.print(text);
        }
    }

    @Override
    public String readLine() throws IOException {
        print(InteractiveModeConstants.PROMPT);

        return reader.readLine();
    }
}
