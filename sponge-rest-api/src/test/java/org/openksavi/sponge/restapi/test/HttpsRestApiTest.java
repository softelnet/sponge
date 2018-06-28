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

package org.openksavi.sponge.restapi.test;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import org.openksavi.sponge.camel.SpongeCamelConfiguration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.core.util.SslConfiguration;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;

import okhttp3.OkHttpClient;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { HttpsRestApiTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class HttpsRestApiTest extends BaseRestApiTestTemplate {

    @Configuration
    public static class TestConfig extends SpongeCamelConfiguration {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin())
                    .knowledgeBase("kb", "examples/rest-api/rest_api.py").build();
        }

        @Bean
        public RestApiPlugin spongeRestApiPlugin() {
            RestApiPlugin plugin = new RestApiPlugin();

            plugin.getSettings().setPort(PORT);

            SslConfiguration sslConfiguration = new SslConfiguration();
            sslConfiguration.setKeyStore("keystore/rest_api_selfsigned.jks");
            sslConfiguration.setKeyStorePassword("sponge");
            sslConfiguration.setKeyPassword("sponge");
            plugin.getSettings().setSslConfiguration(sslConfiguration);

            return plugin;
        }
    }

    @Override
    protected String getProtocol() {
        return "https";
    }

    @Override
    protected RestTemplate createRestTemplate() {
        try {
            final X509TrustManager trustAllCerts = new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] { trustAllCerts }, new java.security.SecureRandom());

            return setupRestTemplate(new RestTemplate(new OkHttp3ClientHttpRequestFactory(
                    new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory(), trustAllCerts)
                            .hostnameVerifier((String hostname, SSLSession session) -> true).build())));
        } catch (Exception e) {
            throw SpongeUtils.wrapException(e);
        }
    }
}
