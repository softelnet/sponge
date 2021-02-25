/*
 * Copyright 2016-2021 The Sponge authors.
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

package org.openksavi.sponge.springboot.remoteservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.remoteapi.client.DefaultSpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClientConfiguration;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.RemoteApiSettings;

@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@SpringBootApplication
public class SpongeRemoteServiceSpringBootTest {

    @Autowired
    private RemoteApiServerPlugin plugin;

    @Autowired
    private GrpcApiServerPlugin grpcPlugin;

    @Autowired
    private TestRestTemplate template;

    @Test
    public void getVersionAdmin() {
        try (SpongeClient client = createRemoteClientAdmin()) {
            assertNotNull(client.getVersion());
        }
    }

    protected SpongeClient createRemoteClient() {
        return new DefaultSpongeClient(SpongeClientConfiguration.builder().url(String.format("%s/sponge", template.getRootUri())).build());
    }

    protected SpongeClient createRemoteClientAdmin() {
        return new DefaultSpongeClient(SpongeClientConfiguration.builder().url(String.format("%s/sponge", template.getRootUri()))
                .username("admin").password("password").build());
    }

    @Test
    public void remoteServiceProperties() {
        RemoteApiSettings settings = plugin.getSettings();

        assertEquals("1", settings.getVersion());
        assertEquals("Remote name", settings.getName());
        assertEquals("Remote description", settings.getDescription());
        assertEquals("Remote license", settings.getLicense());
        assertEquals(true, settings.isPrettyPrint());
        assertEquals(true, settings.isPublishReload());
        assertEquals(true, settings.isAllowAnonymous());
        assertEquals("ROLE_ADMIN2", settings.getAdminRole());
        assertEquals("ROLE_ANONYMOUS2", settings.getAnonymousRole());
        assertEquals(true, settings.isIncludeDetailedErrorMessage());
        assertEquals(Duration.ofMillis(22000), settings.getAuthTokenExpirationDuration());
        assertEquals(true, settings.isIncludeResponseTimes());
        assertEquals(false, settings.isRegisterServiceDiscovery());
        assertEquals(true, settings.isIgnoreUnknownArgs());

        assertEquals("value1", settings.getOpenApiProperties().get("property1"));
        assertEquals("http://localhost/test", settings.getServiceDiscoveryInfo().getUrl());

        assertEquals(9191, grpcPlugin.getPort());
        assertEquals(true, grpcPlugin.isAutoStart());
    }

    @Test
    public void hasAction() {
        try (SpongeClient client = createRemoteClient()) {
            assertEquals("TO UPPER", client.call(String.class, "UpperCase", Arrays.asList("to upper")));
        }
    }
}
