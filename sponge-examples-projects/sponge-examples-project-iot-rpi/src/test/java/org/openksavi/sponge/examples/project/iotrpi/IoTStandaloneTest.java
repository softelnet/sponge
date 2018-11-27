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

package org.openksavi.sponge.examples.project.iotrpi;

import org.junit.Test;

import org.openksavi.sponge.core.engine.ConfigurationConstants;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.standalone.StandaloneEngineMain;

public class IoTStandaloneTest {

    protected SpongeRestClient createRestClient() {
        return new DefaultSpongeRestClient(SpongeRestClientConfiguration.builder()
                .url(String.format("http://localhost:%d/%s", RestApiConstants.DEFAULT_PORT, RestApiConstants.DEFAULT_PATH))
                .username("admin").password("password").build());
    }

    @Test
    public void testIoTStandalone() {
        StandaloneEngineMain engineMain = new StandaloneEngineMain(true);

        System.setProperty(ConfigurationConstants.PROP_HOME, ".");
        System.setProperty("password.file", "sponge/password.txt");

        try {
            engineMain.startup("-c", "sponge/sponge_iot.xml");
            SpongeEngine engine = engineMain.getEngine();

            try (SpongeRestClient client = createRestClient()) {
                // client.call("SendSms", "000000000", "Test message");
                // client.call("TakePicture");
                // client.call("SendNotificationEmail", "Test", "TEST");
            }

            // await().atMost(60, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable("message") != null
            // && engine.getOperations().getVariable(Boolean.class, "sent"));
            //
            // assertTrue(engine.getOperations().getVariable(String.class, "message").contains("Send me to Camel"));
        } finally {
            engineMain.shutdown();
        }
    }
}
