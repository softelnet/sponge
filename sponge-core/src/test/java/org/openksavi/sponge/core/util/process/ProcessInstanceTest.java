/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.core.util.process;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.util.process.ProcessConfiguration;
import org.openksavi.sponge.util.process.ProcessInstance;

@net.jcip.annotations.NotThreadSafe
public class ProcessInstanceTest {

    @Test
    public void testProcessEcho() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance process =
                engine.getOperations().process(ProcessConfiguration.builder("echo").arguments("TEST").outputAsString()).run();
        assertEquals("TEST", process.getOutputString());
    }

    @Test
    public void testProcessEnv() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance process = engine.getOperations()
                .process(ProcessConfiguration.builder("printenv").arguments("TEST_VARIABLE").env("TEST_VARIABLE", "TEST").outputAsString())
                .run();
        assertEquals("TEST", process.getOutputString());
    }

    @Test
    public void testProcessWaitForOutput() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        engine.getOperations().process(ProcessConfiguration.builder("echo").arguments("MSG").outputAsConsumer()
                .waitForPositiveLineRegexp(".*MSG.*").waitForNegativeLineRegexp(".*ERROR.*")).run();
    }

    @Test
    public void testInfiniteProcessWaitForOutputPython() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance process = engine.getOperations()
                .process(ProcessConfiguration.builder("python").arguments("src/test/resources/test_infinite_process_wait_for_output.py")
                        .outputAsConsumer().waitForPositiveLineRegexp(".*STARTED.*").waitForNegativeLineRegexp(".*ERROR.*"))
                .run();

        process.destroy();
    }

    @Test
    public void testInfiniteProcessWaitForOutputBash() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance process =
                engine.getOperations().process(ProcessConfiguration.builder("bash").arguments("-c").arguments("echo STARTED; sleep 600")
                        .outputAsConsumer().waitForPositiveLineRegexp(".*STARTED.*").waitForNegativeLineRegexp(".*ERROR.*")).run();

        process.destroy();
    }

    @Test(expected = SpongeException.class)
    public void testProcessWaitForErrorOutput() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        try {
            engine.getOperations().process(
                    ProcessConfiguration.builder("echo").arguments("ERROR").outputAsConsumer().waitForNegativeLineRegexp(".*ERROR.*"))
                    .run();
        } catch (SpongeException e) {
            assertEquals("Error in the subprocess: ERROR", e.getMessage());
            throw e;
        }
    }

    @Test
    public void testProcessWaitForNonexistingOutputEarlyExit() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        engine.getOperations().process(
                ProcessConfiguration.builder("echo").arguments("OK").outputAsConsumer().waitForPositiveLineRegexp(".*NONEXISTING.*")).run();
    }

    @Test
    public void testProcessRedirectToBinary() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance process =
                engine.getOperations().process(ProcessConfiguration.builder("echo").arguments("-n", "MSG").outputAsBinary()).run();
        assertArrayEquals(new byte[] { 'M', 'S', 'G' }, process.getOutputBinary());
    }

    @Test
    public void testProcessInputBinaryOutputString() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        byte[] data = new byte[] { '1', '2', '3' };

        ProcessInstance process =
                engine.getOperations().process(ProcessConfiguration.builder("base64").inputAsBinary(data).outputAsString()).run();

        assertEquals(Base64.getEncoder().encodeToString(data), process.getOutputString());
    }

    @Test
    public void testProcessInputStringOutputBinary() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        byte[] data = new byte[] { '1', '2', '3' };
        String stringData = Base64.getEncoder().encodeToString(data);

        ProcessInstance process = engine.getOperations()
                .process(ProcessConfiguration.builder("base64").arguments("--decode").inputAsString(stringData).outputAsBinary()).run();

        assertArrayEquals(data, process.getOutputBinary());
    }

    @Test
    public void testProcessInputStreamOutputString() throws Exception {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        byte[] data = new byte[] { '1', '2', '3' };

        ProcessInstance process =
                engine.getOperations().process(ProcessConfiguration.builder("base64").inputAsStream().outputAsString()).run();

        // Feed the subprocess standard input.
        IOUtils.write(data, process.getInput());
        process.getInput().close();
        process.waitForReady();

        assertEquals(Base64.getEncoder().encodeToString(data), process.getOutputString());
    }

    @Test
    public void testProcessInputFileOutputString() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance process = engine.getOperations().process(
                ProcessConfiguration.builder("base64").inputAsFile("src/test/resources/process_instance_test_input.txt").outputAsString())
                .run();
        assertEquals("MTIz", process.getOutputString());
    }

    @Test
    public void testProcessInputStringOutputFile() throws Exception {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        String outputFilename = "target/testProcessInputStringOutputFile_output.txt";
        engine.getOperations()
                .process(ProcessConfiguration.builder("base64").arguments("--decode").inputAsString("MTIz").outputAsFile(outputFilename))
                .run();

        File resultFile = new File(outputFilename);
        try {
            assertArrayEquals(new byte[] { '1', '2', '3' }, FileUtils.readFileToByteArray(resultFile));
        } finally {
            FileUtils.deleteQuietly(resultFile);
        }
    }

    @Test
    public void testProcessInputFileOutputFile() throws Exception {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        String outputFilename = "target/testProcessInputFileOutputFile_output.txt";
        engine.getOperations().process(ProcessConfiguration.builder("cat").arguments("-")
                .inputAsFile("src/test/resources/process_instance_test_input.txt").outputAsFile(outputFilename)).run();

        File resultFile = new File(outputFilename);
        try {
            assertEquals("123", FileUtils.readFileToString(resultFile, StandardCharsets.UTF_8.name()));
        } finally {
            FileUtils.deleteQuietly(resultFile);
        }
    }
}
