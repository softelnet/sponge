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

package org.openksavi.sponge.py4j;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;

public class Py4JTest {

    private static final Logger logger = LoggerFactory.getLogger(Py4JTest.class);

    protected Pair<Process, String> startCPython(SpongeEngine engine, String script, boolean readOutput) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(engine.getConfigurationManager().getProperty("pythonExecutable"), script);
        Process process = null;

        try {
            process = pb.start();
            logger.debug("{}", pb.environment());
        } catch (Exception e) {
            logger.error(getClass().getSimpleName() + " tests require Python 2.7! Python must have Py4J installed, e.g. pip install py4j.");
            throw e;
        }

        String outputText = null;
        if (readOutput) {
            try (BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                outputText = output.lines().collect(Collectors.joining("\n"));
                logger.info("Python process output:\n{}", outputText);

                String errorsString = errors.lines().collect(Collectors.joining("\n"));
                if (!errorsString.isEmpty()) {
                    throw new SpongeException("Python script error: " + errorsString);
                }
            }
        }

        return new ImmutablePair<>(process, outputText);
    }

    @Test
    public void testPy4JJavaServer() throws Exception {
        String rootDir = "examples/py4j/java_server";
        SpongeEngine engine = DefaultSpongeEngine.builder().config(rootDir + "/py4j_java_server_sponge_hello_world.xml").build();
        engine.startup();

        try {
            Pair<Process, String> scriptResult = startCPython(engine, rootDir + "/py4j_java_server_python_hello_world.py", true);
            Process process = scriptResult.getLeft();
            String outputText = scriptResult.getRight();

            process.waitFor(60, TimeUnit.SECONDS);

            await().atMost(60, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "eventCounter").intValue() > 0);

            assertEquals(String.format("Connected to %s\nTriggers count: %d, first: %s", engine.getDescription(),
                    engine.getTriggers().size(), engine.getTriggers().get(0).getName()), outputText);
            assertEquals(1, engine.getOperations().getVariable(Number.class, "eventCounter").intValue());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testPy4JPythonServer() throws Exception {
        String rootDir = "examples/py4j/python_server";
        SpongeEngine engine = DefaultSpongeEngine.builder().config(rootDir + "/py4j_python_server_sponge_hello_world.xml").build();
        engine.startup();
        Process process = null;

        try {
            Pair<Process, String> scriptResult = startCPython(engine, rootDir + "/py4j_python_server_python_hello_world.py", false);
            process = scriptResult.getLeft();
            // Wait for the Python process to start.
            TimeUnit.SECONDS.sleep(2);

            assertEquals("TEST", engine.getOperations().call("PythonUpperCase", "test"));
            assertFalse(engine.isError());
        } finally {
            if (process != null) {
                process.destroy();
            }
            engine.shutdown();
        }
    }

    @Test
    public void testPy4JJavaServerTls() throws Exception {
        String rootDir = "examples/py4j/java_server_tls";
        SpongeEngine engine = DefaultSpongeEngine.builder().config(rootDir + "/py4j_java_server_tls_sponge_hello_world.xml").build();
        engine.startup();

        try {
            Pair<Process, String> scriptResult = startCPython(engine, rootDir + "/py4j_java_server_tls_python_hello_world.py", true);
            Process process = scriptResult.getLeft();
            String outputText = scriptResult.getRight();

            process.waitFor(60, TimeUnit.SECONDS);

            await().atMost(60, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "eventCounter").intValue() > 0);

            assertEquals(String.format("Connected to %s\nTriggers count: %d, first: %s", engine.getDescription(),
                    engine.getTriggers().size(), engine.getTriggers().get(0).getName()), outputText);
            assertEquals(1, engine.getOperations().getVariable(Number.class, "eventCounter").intValue());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }
}
