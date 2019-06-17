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

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;

import okhttp3.OkHttpClient;

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.SslConfiguration;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.BaseSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.client.okhttp.OkHttpSpongeRestClient;
import org.openksavi.sponge.restapi.client.util.RestClientUtils;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@net.jcip.annotations.NotThreadSafe
@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { GrpcApiTlsServerTest.TlsTestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class GrpcApiTlsServerTest extends GrpcApiServerBaseTest {

    @Configuration
    public static class TlsTestConfig extends PortTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin(), spongeGrpcApiPlugin())
                    .config("examples/remote-api-server/remote_api.xml").build();
        }

        @Bean
        public RestApiServerPlugin spongeRestApiPlugin() {
            RestApiServerPlugin plugin = new RestApiServerPlugin();
            plugin.getSettings().setPort(spongeRestApiPort());
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
    protected BaseSpongeRestClient createRestClient(boolean useEventTypeCache) {
        return new OkHttpSpongeRestClient(
                SpongeRestClientConfiguration.builder().url(String.format("https://localhost:%d/%s", port, RestApiConstants.DEFAULT_PATH))
                        .useEventTypeCache(useEventTypeCache).build(),
                // Insecure connection only for tests.
                new OkHttpClient.Builder()
                        .sslSocketFactory(RestClientUtils.createTrustAllSslContext().getSocketFactory(),
                                RestClientUtils.createTrustAllTrustManager())
                        .hostnameVerifier((String hostname, SSLSession session) -> true).build());
    }

    @Override
    protected ManagedChannel createManagedChannel() {
        // Use the self signed certificate in the PEM format. Created from remote_api_selfsigned.jks by the command:
        // keytool -exportcert -rfc -file remote_api_selfsigned.pem -keystore remote_api_selfsigned.jks -alias rest_api -keypass sponge
        // -storepass sponge
        try {
            return NettyChannelBuilder
                    .forTarget("dns:///localhost:" + getGrpcPort()).sslContext(GrpcSslContexts.forClient()
                            .trustManager(new File("src/test/resources/security/remote_api_selfsigned.pem")).build())
                    .overrideAuthority("sponge").build();
        } catch (SSLException e) {
            throw new SpongeException("SSL error", e);
        }
    }
}
