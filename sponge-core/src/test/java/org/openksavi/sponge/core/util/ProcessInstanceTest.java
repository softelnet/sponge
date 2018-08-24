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

package org.openksavi.sponge.core.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.util.ProcessConfiguration.RedirectType;
import org.openksavi.sponge.engine.SpongeEngine;

public class ProcessInstanceTest {

    @Test
    public void testProcessEcho() {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance processInstance = SpongeUtils.startProcess(engine,
                ProcessConfiguration.builder("echo").arguments("TEST").redirectType(RedirectType.STRING).build());
        assertEquals("TEST", processInstance.getOutput());
    }

    @Test
    public void testProcessEnv() {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        ProcessInstance processInstance = SpongeUtils.startProcess(engine, ProcessConfiguration.builder("printenv")
                .arguments("TEST_VARIABLE").env("TEST_VARIABLE", "TEST").redirectType(RedirectType.STRING).build());
        assertEquals("TEST", processInstance.getOutput());
    }

    @Test(expected = SpongeException.class)
    public void testProcessWaitForErrorOutput() {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        try {
            SpongeUtils.startProcess(engine, ProcessConfiguration.builder("echo").arguments("ERROR").redirectType(RedirectType.LOGGER)
                    .waitForErrorOutputLineRegexp(".*ERROR.*").build());
        } catch (SpongeException e) {
            assertEquals("Error in the subprocess: ERROR", e.getMessage());
            throw e;
        }
    }

    @Test
    public void testProcessWaitForNonexistingOutputEarlyExit() {
        SpongeEngine engine = DefaultSpongeEngine.builder().build();

        SpongeUtils.startProcess(engine, ProcessConfiguration.builder("echo").arguments("OK").redirectType(RedirectType.LOGGER)
                .waitForOutputLineRegexp(".*NONEXISTING.*").build());
    }
}
