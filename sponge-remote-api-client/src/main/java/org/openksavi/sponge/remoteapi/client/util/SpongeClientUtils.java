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

package org.openksavi.sponge.remoteapi.client.util;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Cache;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.client.SpongeClientException;
import org.openksavi.sponge.remoteapi.util.RemoteApiUtils;

/**
 * A set of Remote API client utility methods.
 */
public abstract class SpongeClientUtils {

    private SpongeClientUtils() {
        //
    }

    public static SpongeException wrapException(Throwable e) {
        return e instanceof SpongeException ? (SpongeException) e : new SpongeClientException(e);
    }

    /**
     * Creates a new trust manager that trusts all. WARNING: Use only in tests.
     *
     * @return a new trust manager.
     */
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

    /**
     * Creates a new SSL context that trusts all. WARNING: Use only in tests.
     *
     * @return a new trust manager.
     */
    public static SSLContext createTrustAllSslContext() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] { createTrustAllTrustManager() }, new SecureRandom());

            return sslContext;
        } catch (Exception e) {
            throw SpongeClientUtils.wrapException(e);
        }
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

        Cache cache = okHttpClient.cache();
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                throw new SpongeException(e);
            }
        }
    }

    public static String fetchOpenApiJson(String baseRemoteApiUrl) {
        OkHttpClient client = new OkHttpClient.Builder().build();

        try {
            Response okHttpResponse = client
                    .newCall(new Request.Builder().url(baseRemoteApiUrl + "/" + RemoteApiConstants.ENDPOINT_DOC)
                            .headers(new Headers.Builder().add("Content-Type", RemoteApiConstants.CONTENT_TYPE_JSON).build()).get().build())
                    .execute();

            Validate.isTrue(okHttpResponse.code() == 200, "HTTP response is %s", okHttpResponse.code());
            ObjectMapper mapper = RemoteApiUtils.createObjectMapper();
            String json = Validate.notNull(okHttpResponse.body(), "The reponse body is empty").string();

            Map<?, ?> jsonMap = mapper.readValue(json, Map.class);

            Validate.isTrue(jsonMap.containsKey("swagger"), "The response doesn't seem to be an OpenAPI JSON specification");

            return json;
        } catch (IOException e) {
            throw new SpongeException(e);
        } finally {
            SpongeClientUtils.closeOkHttpClient(client);
        }
    }
}
