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
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.util.process.InputRedirect;
import org.openksavi.sponge.util.process.OutputRedirect;
import org.openksavi.sponge.util.process.ProcessConfiguration;
import org.openksavi.sponge.util.process.ProcessInstance;

@net.jcip.annotations.NotThreadSafe
public class ProcessInstanceTest {

    @Test
    public void testProcessEcho() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance process = engine.getOperations()
                .runProcess(ProcessConfiguration.builder("echo").arguments("TEST").outputRedirect(OutputRedirect.STRING).build());
        assertEquals("TEST", process.getOutputString());
    }

    @Test
    public void testProcessEnv() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance process = engine.getOperations().runProcess(ProcessConfiguration.builder("printenv").arguments("TEST_VARIABLE")
                .env("TEST_VARIABLE", "TEST").outputRedirect(OutputRedirect.STRING).build());
        assertEquals("TEST", process.getOutputString());
    }

    @Test
    public void testProcessWaitForOutput() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        engine.getOperations().runProcess(ProcessConfiguration.builder("echo").arguments("MSG").outputRedirect(OutputRedirect.LOGGER)
                .waitForPositiveLineRegexp(".*MSG.*").waitForNegativeLineRegexp(".*ERROR.*").build());
    }

    @Test
    public void testInfiniteProcessWaitForOutputPython() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance process = engine.getOperations()
                .runProcess(ProcessConfiguration.builder("python").arguments("src/test/resources/test_infinite_process_wait_for_output.py")
                        .outputRedirect(OutputRedirect.LOGGER).waitForPositiveLineRegexp(".*STARTED.*")
                        .waitForNegativeLineRegexp(".*ERROR.*").build());

        process.destroy();
    }

    @Test
    public void testInfiniteProcessWaitForOutputBash() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance process = engine.getOperations()
                .runProcess(ProcessConfiguration.builder("bash").arguments("-c").arguments("echo STARTED; sleep 600")
                        .outputRedirect(OutputRedirect.LOGGER).waitForPositiveLineRegexp(".*STARTED.*")
                        .waitForNegativeLineRegexp(".*ERROR.*").build());

        process.destroy();
    }

    @Test(expected = SpongeException.class)
    public void testProcessWaitForErrorOutput() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        try {
            engine.getOperations().runProcess(ProcessConfiguration.builder("echo").arguments("ERROR").outputRedirect(OutputRedirect.LOGGER)
                    .waitForNegativeLineRegexp(".*ERROR.*").build());
        } catch (SpongeException e) {
            assertEquals("Error in the subprocess: ERROR", e.getMessage());
            throw e;
        }
    }

    @Test
    public void testProcessWaitForNonexistingOutputEarlyExit() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        engine.getOperations().runProcess(ProcessConfiguration.builder("echo").arguments("OK").outputRedirect(OutputRedirect.LOGGER)
                .waitForPositiveLineRegexp(".*NONEXISTING.*").build());
    }

    @Test
    public void testProcessRedirectToBinary() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance process = engine.getOperations()
                .runProcess(ProcessConfiguration.builder("echo").arguments("-n", "MSG").outputRedirect(OutputRedirect.BINARY).build());
        assertArrayEquals(new byte[] { 'M', 'S', 'G' }, process.getOutputBinary());
    }

    @Test
    public void testProcessInputBinaryOutputString() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        byte[] data = new byte[] { '1', '2', '3' };

        ProcessInstance process = engine.getOperations().runProcess(ProcessConfiguration.builder("base64")
                .inputRedirect(InputRedirect.BINARY).inputBinary(data).outputRedirect(OutputRedirect.STRING).build());

        assertEquals(Base64.getEncoder().encodeToString(data), process.getOutputString());
    }

    @Test
    public void testProcessInputStringOutputBinary() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        byte[] data = new byte[] { '1', '2', '3' };
        String stringData = Base64.getEncoder().encodeToString(data);

        ProcessInstance process = engine.getOperations().runProcess(ProcessConfiguration.builder("base64").arguments("--decode")
                .inputRedirect(InputRedirect.STRING).inputString(stringData).outputRedirect(OutputRedirect.BINARY).build());

        assertArrayEquals(data, process.getOutputBinary());
    }

    @Test
    public void testProcessInputStreamOutputString() throws Exception {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        byte[] data = new byte[] { '1', '2', '3' };

        ProcessInstance process = engine.getOperations().runProcess(ProcessConfiguration.builder("base64")
                .inputRedirect(InputRedirect.STREAM).inputBinary(data).outputRedirect(OutputRedirect.STRING).build());

        // Feed the subprocess standard input.
        IOUtils.write(data, process.getInput());
        process.getInput().close();
        process.waitForReady();

        assertEquals(Base64.getEncoder().encodeToString(data), process.getOutputString());
    }

    @Test
    public void testProcessInputFileOutputString() throws InterruptedException {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance process = engine.getOperations().runProcess(ProcessConfiguration.builder("base64").inputRedirect(InputRedirect.FILE)
                .inputFile("src/test/resources/process_instance_test_input.txt").outputRedirect(OutputRedirect.STRING).build());
        assertEquals("MTIz", process.getOutputString());
    }

    @Test
    public void testProcessInputStringOutputFile() throws Exception {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        String outputFileName = "target/testProcessInputStringOutputFile_output.txt";
        engine.getOperations().runProcess(ProcessConfiguration.builder("base64").arguments("--decode").inputRedirect(InputRedirect.STRING)
                .inputString("MTIz").outputRedirect(OutputRedirect.FILE).outputFile(outputFileName).build());

        File resultFile = new File(outputFileName);
        try {
            assertArrayEquals(new byte[] { '1', '2', '3' }, FileUtils.readFileToByteArray(resultFile));
        } finally {
            FileUtils.deleteQuietly(resultFile);
        }
    }

    @Test
    public void testProcessInputFileOutputFile() throws Exception {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        String outputFileName = "target/testProcessInputFileOutputFile_output.txt";
        engine.getOperations()
                .runProcess(ProcessConfiguration.builder("cat").arguments("-").inputRedirect(InputRedirect.FILE)
                        .inputFile("src/test/resources/process_instance_test_input.txt").outputRedirect(OutputRedirect.FILE)
                        .outputFile(outputFileName).build());

        File resultFile = new File(outputFileName);
        try {
            assertEquals("123", FileUtils.readFileToString(resultFile, "UTF-8"));
        } finally {
            FileUtils.deleteQuietly(resultFile);
        }
    }
}
