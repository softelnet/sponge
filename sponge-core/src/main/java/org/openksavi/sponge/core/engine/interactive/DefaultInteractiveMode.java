package org.openksavi.sponge.core.engine.interactive;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.GenericExceptionContext;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.ExceptionHandler;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.engine.interactive.InteractiveMode;
import org.openksavi.sponge.engine.interactive.InteractiveModeConsole;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseInterpreter;
import org.openksavi.sponge.kb.ScriptKnowledgeBaseInterpreter;

/**
 * Default interactive mode.
 */
public class DefaultInteractiveMode implements InteractiveMode {

    protected SpongeEngine engine;

    protected String kbName;

    private ExceptionHandler exceptionHandler;

    private Supplier<InteractiveModeConsole> consoleSupplier;

    private InteractiveModeConsole activeConsole;

    private AtomicBoolean running = new AtomicBoolean(false);

    public DefaultInteractiveMode(SpongeEngine engine, String kbName, Supplier<InteractiveModeConsole> consoleSupplier) {
        this.engine = engine;
        this.kbName = kbName;
        this.consoleSupplier = consoleSupplier;
    }

    @Override
    public void loop() {
        running.set(true);

        try (InteractiveModeConsole console = consoleSupplier.get()) {
            activeConsole = console;

            if (!console.isOpen()) {
                console.open();
            }

            ScriptKnowledgeBaseInterpreter scriptInterpreter = getScriptKnowledgeBaseInterpreter();

            console.print(getWelcomeMessage());

            while (true) {
                try {
                    if (!iteration(console, scriptInterpreter)) {
                        break;
                    }
                } catch (Throwable e) {
                    handleException("interactive", e);
                }
            }
        } finally {
            running.set(false);
        }
    }

    protected boolean iteration(InteractiveModeConsole reader, ScriptKnowledgeBaseInterpreter scriptInterpreter) throws IOException {
        StringBuffer commandBuffer = new StringBuffer();
        while (true) {
            if (!engine.isRunning()) {
                return false;
            }

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

            if (StringUtils.equalsAny(command.trim(), InteractiveModeConstants.EXIT_COMMAND, InteractiveModeConstants.QUIT_COMMAND)) {
                return false;
            }

            boolean isMultiLineStatement = command.endsWith(InteractiveModeConstants.LINE_BREAK);
            if (isMultiLineStatement) {
                command = StringUtils.removeEnd(command, InteractiveModeConstants.LINE_BREAK);
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

    protected void handleException(String sourceName, Throwable e) {
        if (exceptionHandler != null) {
            exceptionHandler.handleException(e,
                    new GenericExceptionContext(engine, ObjectUtils.defaultIfNull(SpongeUtils.getSourceName(e), sourceName)));
        } else {
            e.printStackTrace();
        }
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    protected ScriptKnowledgeBaseInterpreter getScriptKnowledgeBaseInterpreter() {
        KnowledgeBase knowledgeBase = kbName != null ? engine.getKnowledgeBaseManager().getKnowledgeBase(kbName)
                : engine.getKnowledgeBaseManager().getMainKnowledgeBase();
        KnowledgeBaseInterpreter interpreter = knowledgeBase.getInterpreter();
        if (!(interpreter instanceof ScriptKnowledgeBaseInterpreter)) {
            throw new SpongeException("Knowledge base '" + kbName + "' is not script-based.");
        }

        return (ScriptKnowledgeBaseInterpreter) interpreter;
    }

    protected String getWelcomeMessage() {
        return "Running " + engine.getDescription() + " in an interactive mode. Type \"" + InteractiveModeConstants.EXIT_COMMAND + "\", \""
                + InteractiveModeConstants.QUIT_COMMAND + "\" or press CTRL-D to exit.\n";
    }

    @Override
    public InteractiveModeConsole getConsole() {
        return activeConsole;
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }
}
