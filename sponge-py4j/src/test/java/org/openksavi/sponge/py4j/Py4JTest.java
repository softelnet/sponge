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

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.util.ProcessInstance;
import org.openksavi.sponge.engine.SpongeEngine;

@net.jcip.annotations.NotThreadSafe
public class Py4JTest {

    @Test
    public void testPy4JJavaServer() throws Exception {
        String rootDir = "examples/py4j/java_server";
        SpongeEngine engine = DefaultSpongeEngine.builder().config(rootDir + "/py4j_java_server_sponge_hello_world.xml").build();
        engine.startup();

        try {
            ProcessInstance processInstance = engine.getPluginManager().getPlugin(BasePy4JPlugin.class).getScriptProcess();

            await().atMost(60, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "eventCounter").intValue() > 0);

            await().atMost(60, TimeUnit.SECONDS)
                    .until(() -> String.format("Connected to %s\nTriggers count: %d, first: %s", engine.getInfo(),
                            engine.getTriggers().size(), engine.getTriggers().get(0).getName()).equals(processInstance.getOutput()));
            assertEquals(1, engine.getOperations().getVariable(Number.class, "eventCounter").intValue());
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();
        }
    }

    @Test
    public void testPy4JJavaServerTls() throws Exception {
        String rootDir = "examples/py4j/java_server_tls";
        SpongeEngine engine = DefaultSpongeEngine.builder().config(rootDir + "/py4j_java_server_tls_sponge_hello_world.xml").build();
        engine.startup();

        try {
            ProcessInstance processInstance = engine.getPluginManager().getPlugin(BasePy4JPlugin.class).getScriptProcess();

            await().atMost(60, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(Number.class, "eventCounter").intValue() > 0);

            assertEquals(String.format("Connected to %s\nTriggers count: %d, first: %s", engine.getInfo(), engine.getTriggers().size(),
                    engine.getTriggers().get(0).getName()), processInstance.getOutput());
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
            assertEquals("TEST", engine.getOperations().call("PythonUpperCase", "test"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();

            if (process != null) {
                process.destroy();
            }
        }
    }

    @Test
    public void testPy4JPythonServerConfiguration() throws Exception {
        String rootDir = "examples/py4j/python_server_configuration";
        SpongeEngine engine = DefaultSpongeEngine.builder().config(rootDir + "/py4j_python_server_sponge_hello_world.xml").build();
        engine.startup();
        Process process = null;

        try {
            assertEquals("TEST", engine.getOperations().call("PythonUpperCase", "test"));
            assertFalse(engine.isError());
        } finally {
            engine.shutdown();

            if (process != null) {
                process.destroy();
            }
        }
    }

}
