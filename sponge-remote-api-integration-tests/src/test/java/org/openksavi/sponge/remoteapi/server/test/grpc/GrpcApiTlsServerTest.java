/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.server.test.grpc;

import java.io.File;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;

import okhttp3.OkHttpClient;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.SslConfiguration;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.grpcapi.client.DefaultSpongeGrpcClient;
import org.openksavi.sponge.grpcapi.client.SpongeGrpcClient;
import org.openksavi.sponge.grpcapi.client.SpongeGrpcClientConfiguration;
import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.remoteapi.client.BaseSpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClientConfiguration;
import org.openksavi.sponge.remoteapi.client.okhttp.OkHttpSpongeClient;
import org.openksavi.sponge.remoteapi.client.util.SpongeClientUtils;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { GrpcApiTlsServerTest.TlsTestConfig.class })
@DirtiesContext
public class GrpcApiTlsServerTest extends GrpcApiServerBaseTest {

    @Configuration
    public static class TlsTestConfig extends PortTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRemoteApiPlugin(), spongeGrpcApiPlugin())
                    .config("examples/remote-api-server/remote_api.xml").build();
        }

        @Bean
        public RemoteApiServerPlugin spongeRemoteApiPlugin() {
            RemoteApiServerPlugin plugin = new RemoteApiServerPlugin();
            plugin.getSettings().setPort(spongeRemoteApiPort());
            plugin.getSettings().setPrettyPrint(true);

            SslConfiguration sslConfiguration = new SslConfiguration();
            sslConfiguration.setKeyStore("security/remote_api_selfsigned.jks");
            sslConfiguration.setKeyStorePassword("sponge");
            sslConfiguration.setKeyPassword("sponge");
            plugin.getSettings().setSslConfiguration(sslConfiguration);

            return plugin;
        }

        @Bean
        public GrpcApiServerPlugin spongeGrpcApiPlugin() {
            return new GrpcApiServerPlugin();
        }
    }

    @Override
    protected BaseSpongeClient createSpongeClient(boolean useEventTypeCache) {
        return new OkHttpSpongeClient(
                SpongeClientConfiguration.builder().url(String.format("https://localhost:%d", port)).useEventTypeCache(useEventTypeCache)
                        .build(),
                // Insecure connection only for tests.
                new OkHttpClient.Builder()
                        .sslSocketFactory(SpongeClientUtils.createTrustAllSslContext().getSocketFactory(),
                                SpongeClientUtils.createTrustAllTrustManager())
                        .hostnameVerifier((String hostname, SSLSession session) -> true).build());
    }

    @Override
    protected SpongeGrpcClient createGrpcClient(SpongeGrpcClientConfiguration configuration) {
        // Use the self signed certificate in the PEM format. Created from remote_api_selfsigned.jks by the command:
        // keytool -exportcert -rfc -file remote_api_selfsigned.pem -keystore remote_api_selfsigned.jks -alias remote_api -keypass sponge
        // -storepass sponge
        return new DefaultSpongeGrpcClient(createSpongeClient(true), configuration, (builder) -> {
            try {
                builder.sslContext(
                        GrpcSslContexts.forClient().trustManager(new File("src/test/resources/security/remote_api_selfsigned.pem")).build())
                        .overrideAuthority("sponge");
            } catch (SSLException e) {
                throw new SpongeException("SSL error", e);
            }
        });
    }
}
