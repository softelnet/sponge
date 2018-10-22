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

package org.openksavi.sponge.restapi.client.util;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.restapi.client.ClientSpongeException;

/**
 * A set of REST API client utility methods.
 */
public abstract class RestApiClientUtils {

    private RestApiClientUtils() {
        //
    }

    public static SpongeException wrapException(Throwable e) {
        return e instanceof SpongeException ? (SpongeException) e : new ClientSpongeException(e);
    }

    public static X509TrustManager createTrustAllTrustManager() {
        return new X509TrustManager() {

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
    }

    public static SSLContext createTrustAllSslContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] { createTrustAllTrustManager() }, new java.security.SecureRandom());

            return sslContext;
        } catch (Exception e) {
            throw RestApiClientUtils.wrapException(e);
        }
    }

    public static OkHttpClient createOkHttpClient() {
        SSLContext sslContext = RestApiClientUtils.createTrustAllSslContext();

        return new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory(), RestApiClientUtils.createTrustAllTrustManager())
                .hostnameVerifier((String hostname, SSLSession session) -> true).build();
    }

    /**
     * Closes aggressively a OkHttpClient according to the OkHttpClient documentation.
     *
     * @param okHttpClient the OkHttpClient.
     */
    public static void closeOkHttpClient(OkHttpClient okHttpClient) {
        if (okHttpClient.dispatcher() != null && okHttpClient.dispatcher().executorService() != null) {
            okHttpClient.dispatcher().executorService().shutdown();
        }

        if (okHttpClient.connectionPool() != null) {
            okHttpClient.connectionPool().evictAll();
        }

        if (okHttpClient.cache() != null) {
            try {
                okHttpClient.cache().close();
            } catch (IOException e) {
                throw new SpongeException(e);
            }
        }
    }
}
