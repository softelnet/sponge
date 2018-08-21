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

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SocketUtils;

import py4j.GatewayServer;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.ProcessConfiguration;
import org.openksavi.sponge.core.util.ProcessInstance;
import org.openksavi.sponge.core.util.ProcessUtils;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.core.util.SslConfiguration;
import org.openksavi.sponge.java.JPlugin;

/**
 * Base, abstract Sponge plugin that provides integration with CPython using Py4J.
 */
public abstract class BasePy4JPlugin<T> extends JPlugin {

    private static final Logger logger = LoggerFactory.getLogger(BasePy4JPlugin.class);

    public static final String DEFAULT_NAME = "py4j";

    public static final String TAG_FACADE_INTERFACE = "facadeInterface";

    public static final String TAG_JAVA_PORT = "javaPort";

    public static final String TAG_PYTHON_PORT = "pythonPort";

    public static final String TAG_SECURITY = "security";

    public static final String TAG_PYTHON_SCRIPT = "pythonScript";

    public static final String TAG_PYTHON_SCRIPT_BEFORE_STARTUP = "pythonScriptBeforeStartup";

    public static final String TAG_GENERATE_AUTH_TOKEN = "generateAuthToken";

    public static final String TAG_AUTH_TOKEN = "authToken";

    public static final String TAG_RANDOM_PORTS = "randomPorts";

    public static final String DEFAULT_PYTHON_EXECUTABLE = "python";

    public static final String ENV_PY4J_JAVA_PORT = "PY4J_JAVA_PORT";

    public static final String ENV_PY4J_PYTHON_PORT = "PY4J_PYTHON_PORT";

    public static final String ENV_PY4J_AUTH_TOKEN = "PY4J_AUTH_TOKEN";

    private String facadeInterfaceName;

    private T facade;

    private int javaPort = GatewayServer.DEFAULT_PORT;

    private int pythonPort = GatewayServer.DEFAULT_PYTHON_PORT;

    private SslConfiguration security;

    private ProcessConfiguration pythonScriptConfiguration;

    /**
     * If {@code true}, the Python script will be started before this plugin startup (the default value), otherwise it will be started after
     * this plugin startup.
     */
    private boolean pythonScriptBeforeStartup = true;

    /** If {@code true}, the plugin will generate the Py4J auth token (for both sides). The default value is {@code false}. */
    private boolean generateAuthToken = false;

    /** The manual or generated Py4J auth token (for both sides). */
    private String authToken;

    /** If {@code true}, the plugin will use random ports (for both sides). The default value is {@code false}. */
    private boolean randomPorts = false;

    private ProcessInstance scriptProcess;

    public BasePy4JPlugin() {
        setName(DEFAULT_NAME);
    }

    public BasePy4JPlugin(String name) {
        super(name);
    }

    @Override
    public void onStartup() {
        if (generateAuthToken) {
            authToken = SpongeUtils.getRandomUuidString();
        }

        if (randomPorts) {
            javaPort = SocketUtils.findAvailableTcpPort(GatewayServer.DEFAULT_PORT);
            pythonPort = SocketUtils.findAvailableTcpPort(GatewayServer.DEFAULT_PYTHON_PORT);
        }

        logger.info("Using port {} for Java, {} for Python. The auth token is {}.", javaPort, pythonPort, authToken);
    }

    protected void executePythonScript() {
        if (pythonScriptConfiguration != null) {
            ProcessConfiguration finalConfiguration = pythonScriptConfiguration.clone();
            finalConfiguration.getEnv().put(ENV_PY4J_JAVA_PORT, String.valueOf(javaPort));
            finalConfiguration.getEnv().put(ENV_PY4J_PYTHON_PORT, String.valueOf(pythonPort));

            if (authToken != null) {
                finalConfiguration.getEnv().put(ENV_PY4J_AUTH_TOKEN, authToken);
            }

            scriptProcess = SpongeUtils.startProcess(getEngine(), finalConfiguration);
            if (scriptProcess.getOutput() != null) {
                logger.info("Python script output: {}", scriptProcess.getOutput());
            }
        }
    }

    public void killPythonScript() {
        if (scriptProcess != null) {
            scriptProcess.getProcess().destroy();
        }
    }

    public ProcessInstance getScriptProcess() {
        return scriptProcess;
    }

    @Override
    public void onConfigure(Configuration configuration) {
        facadeInterfaceName = configuration.getString(TAG_FACADE_INTERFACE, facadeInterfaceName);
        javaPort = configuration.getInteger(TAG_JAVA_PORT, javaPort);
        pythonPort = configuration.getInteger(TAG_PYTHON_PORT, pythonPort);

        if (configuration.hasChildConfiguration(TAG_SECURITY)) {
            security = SpongeUtils.createSslConfiguration(configuration.getChildConfiguration(TAG_SECURITY));
        }

        if (configuration.hasChildConfiguration(TAG_PYTHON_SCRIPT)) {
            pythonScriptConfiguration =
                    ProcessUtils.createProcessConfigurationBuilder(configuration.getChildConfiguration(TAG_PYTHON_SCRIPT))
                            .name("Python script").build();
            if (pythonScriptConfiguration.getExecutable() == null) {
                pythonScriptConfiguration.setExecutable(DEFAULT_PYTHON_EXECUTABLE);
            }
        }

        pythonScriptBeforeStartup = configuration.getBoolean(TAG_PYTHON_SCRIPT_BEFORE_STARTUP, pythonScriptBeforeStartup);

        generateAuthToken = configuration.getBoolean(TAG_GENERATE_AUTH_TOKEN, generateAuthToken);
        authToken = configuration.getString(TAG_AUTH_TOKEN, authToken);

        randomPorts = configuration.getBoolean(TAG_RANDOM_PORTS, randomPorts);
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

    public ProcessConfiguration getPythonScriptConfiguration() {
        return pythonScriptConfiguration;
    }

    public void setPythonScriptConfiguration(ProcessConfiguration pythonScriptConfiguration) {
        this.pythonScriptConfiguration = pythonScriptConfiguration;
    }

    public boolean isPythonScriptBeforeStartup() {
        return pythonScriptBeforeStartup;
    }

    public void setPythonScriptBeforeStartup(boolean pythonScriptBeforeStartup) {
        this.pythonScriptBeforeStartup = pythonScriptBeforeStartup;
    }

    public int getJavaPort() {
        return javaPort;
    }

    public void setJavaPort(int javaPort) {
        this.javaPort = javaPort;
    }

    public int getPythonPort() {
        return pythonPort;
    }

    public void setPythonPort(int pythonPort) {
        this.pythonPort = pythonPort;
    }

    public SslConfiguration getSecurity() {
        return security;
    }

    public void setSecurity(SslConfiguration security) {
        this.security = security;
    }

    protected SSLContext createSslContext() {
        return SpongeUtils.createSslContext(security);
    }

    public boolean isGenerateAuthToken() {
        return generateAuthToken;
    }

    public void setGenerateAuthToken(boolean generateAuthToken) {
        this.generateAuthToken = generateAuthToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean isRandomPorts() {
        return randomPorts;
    }

    public void setRandomPorts(boolean randomPorts) {
        this.randomPorts = randomPorts;
    }
}
