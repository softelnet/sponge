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

package org.openksavi.sponge.remoteapi.server.test.remote.openapi;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.RemoteApiSettings;
import org.openksavi.sponge.remoteapi.server.security.NoSecuritySecurityProvider;
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
@ExtendWith(SpringExtension.class)
@DirtiesContext
public abstract class BaseOpenApiTest {

    @Inject
    @Named(PortTestConfig.PORT_BEAN_NAME)
    protected Integer port;

    protected abstract static class BaseTestConfig extends PortTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRemoteApiPlugin()).build();
        }

        @Bean
        public RemoteApiServerPlugin spongeRemoteApiPlugin() {
            RemoteApiServerPlugin plugin = new RemoteApiServerPlugin();

            plugin.getSettings().setPort(spongeRemoteApiPort());
            plugin.getSettings().setAllowAnonymous(true);

            plugin.setSecurityProvider(new NoSecuritySecurityProvider());

            setup(plugin.getSettings());

            return plugin;
        }

        protected abstract void setup(RemoteApiSettings settings);
    }

    protected String getOpenApiDocs() throws IOException {
        Response okHttpResponse = new OkHttpClient.Builder().build()
                .newCall(new Request.Builder().url(String.format("http://localhost:%d/doc", port))
                        .headers(new Headers.Builder().add("Content-Type", RemoteApiConstants.CONTENT_TYPE_JSON).build()).get().build())
                .execute();
        return IOUtils.toString(okHttpResponse.body().charStream());
    }
}
