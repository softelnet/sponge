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

package org.openksavi.sponge.standalone.test;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.standalone.StandaloneEngineBuilder;
import org.openksavi.sponge.standalone.StandaloneSpongeEngine;
import org.openksavi.sponge.standalone.interactive.JLineInteractiveModeConsole;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
public class InteractiveModeStandaloneTest {

    static interface InteractiveTest {

        void run(StandaloneSpongeEngine engine, OutputStream out) throws Exception;
    }

    protected void testInteractive(StandaloneEngineBuilder builder, InteractiveTest test) throws Exception {
        AtomicReference<StandaloneSpongeEngine> engineRef = new AtomicReference<>();

        PipedInputStream in = new PipedInputStream();

        try (PipedOutputStream pipedOutputStream = new PipedOutputStream(in)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            StandaloneSpongeEngine engine = builder.interactiveModeConsoleSupplier(() -> {
                JLineInteractiveModeConsole console = new JLineInteractiveModeConsole();
                console.setTerminalBuilder(TerminalBuilder.builder().streams(in, out));

                return console;
            }).build();

            engineRef.set(engine);

            SpongeUtils.executeConcurrentlyOnce(engine, () -> {
                engine.startup();
                engine.getInteractiveMode().loop();
            }, "test");

            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> engine != null && engine.isRunning() && engine.getInteractiveMode().isRunning());

            test.run(engine, pipedOutputStream);

            assertFalse(engine.isError());
        } finally {
            if (engineRef.get() != null) {
                engineRef.get().shutdown();
            }
        }
    }

    @Test
    public void testInteractive() throws Exception {
        testInteractive(StandaloneSpongeEngine.builder().commandLineArgs("-k", "examples/standalone/interactive.py", "-i"),
                (engine, out) -> {
                    // Send the alarm.
                    write(out, "sponge.event(\"alarm\").send()");
                    await().atMost(10, TimeUnit.SECONDS)
                            .until(() -> engine.getOperations().getVariable(Number.class, "alarms").intValue() >= 1);

                    // Create trigger and send event.
                    writeMulti(out, "class T(Trigger):\\");
                    writeMulti(out, "    def onConfigure(self):\\");
                    writeMulti(out, "        self.withEvent(\"notification\")\\");
                    writeMulti(out, "    def onRun(self, event):\\");
                    writeMulti(out, "        sponge.getVariable(\"notifications\").incrementAndGet()\\");
                    writeMulti(out, "        print \"Received the notification!\"");
                    write(out, "");
                    write(out, "sponge.enable(T)");
                    write(out, "sponge.event(\"notification\").send()");

                    await().atMost(10, TimeUnit.SECONDS)
                            .until(() -> engine.getOperations().getVariable(Number.class, "notifications").intValue() >= 1);
                });
    }

    @Test
    public void testInteractiveLangGroovy() throws Exception {
        testInteractive(StandaloneSpongeEngine.builder().commandLineArgs("-i", "-l", "groovy"), (engine, out) -> {
            write(out, "sponge.setVariable(\"lang\", sponge.kb.type.language)");
            write(out, "text = \"Example\"");
            write(out, "sponge.setVariable(\"text\", text)");

            await().atMost(5, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().hasVariable("lang") && engine.getOperations().hasVariable("text"));

            assertEquals("groovy", engine.getOperations().getVariable("lang"));
            assertEquals("Example", engine.getOperations().getVariable("text"));
        });
    }

    @Test
    public void testInteractiveLangPython() throws Exception {
        testInteractive(StandaloneSpongeEngine.builder().commandLineArgs("-i", "-l", "python"), (engine, out) -> {
            write(out, "sponge.setVariable(\"lang\", sponge.kb.type.language)");
            write(out, "text = \"Example\"");
            write(out, "sponge.setVariable(\"text\", text)");

            await().atMost(5, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().hasVariable("lang") && engine.getOperations().hasVariable("text"));

            assertEquals("python", engine.getOperations().getVariable("lang"));
            assertEquals("Example", engine.getOperations().getVariable("text"));
        });
    }

    @Test
    public void testInteractiveLangRuby() throws Exception {
        testInteractive(StandaloneSpongeEngine.builder().commandLineArgs("-i", "-l", "ruby"), (engine, out) -> {
            write(out, "$sponge.setVariable(\"lang\", $sponge.kb.type.language)");
            write(out, "text = \"Example\"");
            write(out, "$sponge.setVariable(\"text\", text)");

            await().atMost(5, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().hasVariable("lang") && engine.getOperations().hasVariable("text"));

            assertEquals("ruby", engine.getOperations().getVariable("lang"));
            assertEquals("Example", engine.getOperations().getVariable("text"));
        });
    }

    @Test
    public void testInteractiveLangJavascript() throws Exception {
        testInteractive(StandaloneSpongeEngine.builder().commandLineArgs("-i", "-l", "javascript"), (engine, out) -> {
            write(out, "sponge.setVariable(\"lang\", sponge.kb.type.language)");
            write(out, "text = \"Example\"");
            write(out, "sponge.setVariable(\"text\", text)");

            await().atMost(5, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().hasVariable("lang") && engine.getOperations().hasVariable("text"));

            assertEquals("javascript", engine.getOperations().getVariable("lang"));
            assertEquals("Example", engine.getOperations().getVariable("text"));
        });
    }

    private static void write(OutputStream out, String command) throws IOException {
        out.write((command + "\r\n").getBytes());
    }

    private static void writeMulti(OutputStream out, String command) throws IOException {
        out.write((command + "\n").getBytes());
    }
}
