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

    private String facadeInterfaceName;

    private T facade;

    private int javaPort = GatewayServer.DEFAULT_PORT;

    private int pythonPort = GatewayServer.DEFAULT_PYTHON_PORT;

    private SslConfiguration security;

    private ProcessConfiguration pythonScriptConfiguration;

    private boolean pythonScriptBeforeStartup = true;

    private ProcessInstance scriptProcess;

    public BasePy4JPlugin() {
        setName(DEFAULT_NAME);
    }

    public BasePy4JPlugin(String name) {
        super(name);
    }

    protected void executePythonScript() {
        if (pythonScriptConfiguration != null) {
            scriptProcess = ProcessUtils.startProcess(getEngine(), pythonScriptConfiguration);
            if (scriptProcess.tryWaitFor() && scriptProcess.getOutput() != null) {
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
                    ProcessUtils.createProcessConfiguration(configuration.getChildConfiguration(TAG_PYTHON_SCRIPT)).name("Python script");
        }

        pythonScriptBeforeStartup = configuration.getBoolean(TAG_PYTHON_SCRIPT_BEFORE_STARTUP, pythonScriptBeforeStartup);
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

    public SslConfiguration getSecurity() {
        return security;
    }

    public void setSecurity(SslConfiguration security) {
        this.security = security;
    }

    protected SSLContext createSslContext() {
        return SpongeUtils.createSslContext(security);
    }
}
