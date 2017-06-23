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

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.ExceptionHandler;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseInterpreter;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;

/**
 * Interactive mode loop.
 */
public class InteractiveMode {

    private static final String LINE_BREAK = "\\";

    private static final String EXIT_COMMAND = "exit";

    private static final String QUIT_COMMAND = "quit";

    private static final String PROMPT = "> ";

    private Engine engine;

    private String kbName;

    private ExceptionHandler exceptionHandler;

    public InteractiveMode(Engine engine, String kbName) {
        this.engine = engine;
        this.kbName = kbName;
    }

    public void loop() {
        Console console = System.console();

        try (BufferedReader reader =
                console != null ? new BufferedReader(console.reader()) : new BufferedReader(new InputStreamReader(System.in))) {

            KnowledgeBase knowledgeBase = kbName != null ? engine.getKnowledgeBaseManager().getKnowledgeBase(kbName)
                    : engine.getKnowledgeBaseManager().getMainKnowledgeBase();
            KnowledgeBaseInterpreter interpreter = knowledgeBase.getInterpreter();
            if (!(interpreter instanceof ScriptKnowledgeBaseInterpreter)) {
                throw new SpongeException("Knowledge base '" + kbName + "' is not script-based.");
            }

            ScriptKnowledgeBaseInterpreter scriptInterpreter = (ScriptKnowledgeBaseInterpreter) interpreter;

            print(console, "Running " + engine.getDescription() + " in an interactive mode. Type \"" + EXIT_COMMAND + "\", \"" +
                    QUIT_COMMAND + "\" or press CTRL-D to exit.\n");

            while (true) {
                try {
                    if (!iteration(console, reader, scriptInterpreter)) {
                        break;
                    }
                } catch (Throwable e) {
                    handleException("interactive", e);
                }
            }
        } catch (IOException e) {
            throw new SpongeException(e);
        }
    }

    protected void handleException(String sourceName, Throwable e) {
        if (exceptionHandler != null) {
            exceptionHandler.handleException(e,
                    new GenericExceptionContext(engine, ObjectUtils.defaultIfNull(Utils.getSourceName(e), sourceName)));
        } else {
            e.printStackTrace();
        }
    }

    protected void print(Console console, String text) {
        if (console != null) {
            console.printf(text);
        } else {
            System.out.print(text);
        }
    }

    private boolean iteration(Console console, BufferedReader reader, ScriptKnowledgeBaseInterpreter scriptInterpreter)
            throws IOException {
        StringBuffer commandBuffer = new StringBuffer();
        while (true) {
            if (!engine.isRunning()) {
                return false;
            }

            print(console, PROMPT);

            String command = null;
            try {
                command = reader.readLine();
            } catch (IOException e) {
                handleException("readLine", e);
                return false;
            }

            if (command == null) {
                return false;
            }

            command = StringUtils.stripEnd(command, null);

            if (StringUtils.equalsAny(command.trim(), EXIT_COMMAND, QUIT_COMMAND)) {
                return false;
            }

            boolean isMultiLineStatement = command.endsWith(LINE_BREAK);
            if (isMultiLineStatement) {
                command = StringUtils.removeEnd(command, LINE_BREAK);
            }

            commandBuffer.append(command);

            if (isMultiLineStatement) {
                commandBuffer.append(System.lineSeparator());
            } else {
                break;
            }
        }

        if (engine.isRunning()) {
            scriptInterpreter.eval(commandBuffer.toString());
            return true;
        } else {
            return false;
        }
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}
