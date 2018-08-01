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
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.TimeUnit;

import org.jline.terminal.TerminalBuilder;
import org.junit.Test;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.standalone.StandaloneSpongeEngine;
import org.openksavi.sponge.standalone.interactive.JLineInteractiveModeConsole;

public class InteractiveModeStandaloneTest {

    private StandaloneSpongeEngine engine;

    private PipedOutputStream outIn;

    @Test
    public void testInteractive() throws Exception {
        PipedInputStream in = new PipedInputStream();

        try (PipedOutputStream pipedOutputStream = new PipedOutputStream(in)) {
            outIn = pipedOutputStream;
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            engine = StandaloneSpongeEngine.builder().commandLineArgs("-k", "examples/standalone/interactive.py", "-i")
                    .interactiveModeConsoleSupplier(() -> {
                        JLineInteractiveModeConsole console = new JLineInteractiveModeConsole();
                        console.setTerminalBuilder(TerminalBuilder.builder().streams(in, out));

                        return console;
                    }).build();

            SpongeUtils.executeConcurrentlyOnce(engine, () -> {
                engine.startup();
                engine.getInteractiveMode().loop();
            });

            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> engine != null && engine.isRunning() && engine.getInteractiveMode().isRunning());

            // Print the message.
            write("print 'Starting interactive mode tests.'");

            // Send the alarm.
            write("sponge.event(\"alarm\").send()");
            await().atMost(10, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(Number.class, "alarms").intValue() >= 1);

            // Create trigger and send event.
            writeMulti("class T(Trigger):\\");
            writeMulti("    def onConfigure(self):\\");
            writeMulti("        self.event = \"notification\"\\");
            writeMulti("    def onRun(self, event):\\");
            writeMulti("        sponge.getVariable(\"notifications\").incrementAndGet()\\");
            writeMulti("        print \"Received the notification!\"");
            write("");
            write("sponge.enable(T)");
            write("sponge.event(\"notification\").send()");
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "notifications").intValue() >= 1);

            assertFalse(engine.isError());
        } finally {
            if (engine != null) {
                engine.shutdown();
            }
        }
    }

    private void write(String command) throws IOException {
        outIn.write((command + "\r\n").getBytes());
    }

    private void writeMulti(String command) throws IOException {
        outIn.write((command + "\n").getBytes());
    }
}
