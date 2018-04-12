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

package org.openksavi.sponge.standalone.interactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jline.reader.Completer;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import org.openksavi.sponge.core.VersionInfo;
import org.openksavi.sponge.core.engine.interactive.InteractiveModeConstants;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.interactive.InteractiveModeConsole;

/**
 * An interactive mode console that uses JLine.
 */
public class JLineInteractiveModeConsole implements InteractiveModeConsole {

    private Parser parser;

    private TerminalBuilder terminalBuilder;

    private Terminal terminal;

    private LineReader reader;

    private Completer completer;

    private AtomicBoolean isOpen = new AtomicBoolean(false);

    public JLineInteractiveModeConsole(boolean open) {
        if (open) {
            open();
        }
    }

    public JLineInteractiveModeConsole() {
        this(false);
    }

    public void setTerminalBuilder(TerminalBuilder terminalBuilder) {
        this.terminalBuilder = terminalBuilder;
    }

    protected Parser createParser() {
        DefaultParser result = new DefaultParser();
        result.setEofOnEscapedNewLine(false);
        result.setEscapeChars(null);

        return result;
    }

    @Override
    public void open() {
        parser = createParser();

        try {
            terminal = (terminalBuilder != null ? terminalBuilder : TerminalBuilder.builder()).build();
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        }

        LineReaderBuilder lineReaderBuilder = LineReaderBuilder.builder().appName(VersionInfo.PRODUCT).terminal(terminal).parser(parser);

        if (completer != null) {
            lineReaderBuilder.completer(completer);
        }

        reader = lineReaderBuilder.build();

        isOpen.set(true);
    }

    @Override
    public boolean isOpen() {
        return isOpen.get();
    }

    @Override
    public void close() {
        try {
            terminal.close();
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            isOpen.set(false);
        }
    }

    @Override
    public void print(String text) {
        if (terminal != null) {
            terminal.writer().print(text);
        } else {
            System.out.print(text);
        }
    }

    @Override
    public String readLine() throws IOException {
        try {
            // Ignore the line that will be read, in order to process multiline commands in the client code.
            reader.readLine(InteractiveModeConstants.PROMPT);

            ParsedLine parsedLine = reader.getParsedLine();

            // Return the raw line.
            return parsedLine != null ? parsedLine.line() : null;
        } catch (UserInterruptException | EndOfFileException e) {
            return null;
        }
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public Parser getParser() {
        return parser;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public LineReader getReader() {
        return reader;
    }

    public void setReader(LineReader reader) {
        this.reader = reader;
    }

    public TerminalBuilder getTerminalBuilder() {
        return terminalBuilder;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }

    public Completer getCompleter() {
        return completer;
    }

    public void setCompleter(Completer completer) {
        this.completer = completer;
    }
}
