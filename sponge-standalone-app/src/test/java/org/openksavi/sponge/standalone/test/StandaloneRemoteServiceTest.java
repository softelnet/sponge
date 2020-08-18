/*
 * Copyright 2016-2020 The Sponge authors.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.remoteapi.client.DefaultSpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClientConfiguration;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerConstants;
import org.openksavi.sponge.standalone.StandaloneEngineMain;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
public class StandaloneRemoteServiceTest {

    private static final int PORT = 11836;

    @Test
    public void testRemoteService() {
        StandaloneEngineMain engineMain = null;
        try {
            System.setProperty("sponge.serviceDir", "examples/standalone/remote_service");
            System.setProperty(RemoteApiServerConstants.PROP_PORT, String.valueOf(PORT));

            engineMain = StandaloneTestUtils.startupStandaloneEngineMain("-c", "services/remote/remote.xml");
            SpongeEngine engine = engineMain.getEngine();

            String arg1 = "User";

            try (SpongeClient client =
                    new DefaultSpongeClient(SpongeClientConfiguration.builder().url(String.format("http://localhost:%d", PORT)).build())) {
                Object result = client.call("HelloWorld", Arrays.asList(arg1));

                assertEquals(String.format("Hello World! Hello %s!", arg1), result);

                client.call("SetupGitKnowledgeBase", Arrays.asList("https://github.com/mnpas/sponge_example_git_kb.git"));

                client.call("EngineReload");

                assertEquals(client.call("HelloWorldActionPython", Arrays.asList("User")), "Hello World! Hello User!");
            }

            assertFalse(engine.isError());
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }
}
