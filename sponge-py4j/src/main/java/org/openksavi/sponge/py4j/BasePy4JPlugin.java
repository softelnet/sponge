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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import py4j.GatewayServer;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.java.JavaPlugin;

/**
 * Base, abstract Sponge plugin that provides integration with CPython using Py4J.
 */
public abstract class BasePy4JPlugin<T> extends JavaPlugin {

    public static final String DEFAULT_NAME = "py4j";

    public static final String TAG_FACADE_INTERFACE = "facadeInterface";

    public static final String TAG_JAVA_PORT = "javaPort";

    public static final String TAG_PYTHON_PORT = "pythonPort";

    public static final String TAG_SECURITY = "security";

    public static final String TAG_SECURITY_PASSWORD = "password";

    public static final String TAG_SECURITY_KEYSTORE = "keystore";

    public static final String TAG_SECURITY_ALGORITHM = "algorithm";

    public static final String DEFAULT_SECURITY_ALGORITHM = "SunX509";

    private String facadeInterfaceName;

    private T facade;

    private int javaPort = GatewayServer.DEFAULT_PORT;

    private int pythonPort = GatewayServer.DEFAULT_PYTHON_PORT;

    private SecurityConfiguration security;

    public BasePy4JPlugin() {
        setName(DEFAULT_NAME);
    }

    public T getFacade() {
        return facade;
    }

    public void setFacade(T facade) {
        this.facade = facade;
    }

    public String getFacadeInterfaceName() {
        return facadeInterfaceName;
    }

    public void setFacadeInterfaceName(String facadeInterfaceName) {
        this.facadeInterfaceName = facadeInterfaceName;
    }

    @Override
    public void onConfigure(Configuration configuration) {
        facadeInterfaceName = configuration.getString(TAG_FACADE_INTERFACE, facadeInterfaceName);
        javaPort = configuration.getInteger(TAG_JAVA_PORT, javaPort);
        pythonPort = configuration.getInteger(TAG_PYTHON_PORT, pythonPort);

        if (configuration.hasChildConfiguration(TAG_SECURITY)) {
            Configuration securityConfiguration = configuration.getChildConfiguration(TAG_SECURITY);

            SecurityConfiguration newSecurity = new SecurityConfiguration();
            newSecurity.setPassword(Utils.getRequiredConfigurationString(securityConfiguration, TAG_SECURITY_PASSWORD));
            newSecurity.setKeystore(Utils.getRequiredConfigurationString(securityConfiguration, TAG_SECURITY_KEYSTORE));
            newSecurity.setAlgorithm(securityConfiguration.getString(TAG_SECURITY_ALGORITHM, DEFAULT_SECURITY_ALGORITHM));

            security = newSecurity;
        }
    }

    public Integer getJavaPort() {
        return javaPort;
    }

    public void setJavaPort(Integer javaPort) {
        this.javaPort = javaPort;
    }

    public Integer getPythonPort() {
        return pythonPort;
    }

    public void setPythonPort(Integer pythonPort) {
        this.pythonPort = pythonPort;
    }

    public SecurityConfiguration getSecurity() {
        return security;
    }

    public void setSecurity(SecurityConfiguration security) {
        this.security = security;
    }

    protected SSLContext createSslContext() {
        InputStream fis = null;

        try {
            char[] password = security.getPassword().toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");

            URL keystoreUrl = Utils.getUrlFromClasspath(security.getKeystore());
            if (keystoreUrl == null) {
                throw new SpongeException("Expected a '" + security.getKeystore() + "' keystore file on the classpath");
            }
            fis = keystoreUrl.openStream();

            ks.load(fis, password);

            // Setup the key manager factory.
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(security.getAlgorithm());
            kmf.init(ks, password);

            // Setup the trust manager factory.
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(security.getAlgorithm());
            tmf.init(ks);

            SSLContext sslContext = SSLContext.getInstance("TLS");

            // Setup the HTTPS context and parameters.
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            return sslContext;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException
                | KeyManagementException e) {
            throw Utils.wrapException("createSslContext", e);
        } finally {
            Utils.close(fis);
        }
    }
}
