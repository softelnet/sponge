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

package org.openksavi.sponge.standalone.test;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.engine.ConfigurationConstants;
import org.openksavi.sponge.groovy.GroovyConstants;
import org.openksavi.sponge.jruby.RubyConstants;
import org.openksavi.sponge.jython.PythonConstants;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.nashorn.JavaScriptConstants;
import org.openksavi.sponge.standalone.StandaloneEngineMain;

@net.jcip.annotations.NotThreadSafe
public class StandaloneErrorReportingTest {

    @BeforeAll
    public static void beforeClass() {
        System.setProperty(ConfigurationConstants.PROP_HOME, ".");
        StandaloneTestUtils.reloadLogback("../sponge-distribution/src/release/logback.xml");
    }

    @AfterAll
    public static void afterClass() {
        System.clearProperty(ConfigurationConstants.PROP_HOME);
        StandaloneTestUtils.reloadLogback("../sponge-test/src/main/resources/logback.xml");
    }

    @Test
    public void testErrorReportingFunctionGroovy() {
        testErrorReporting("error_reporting_function", GroovyConstants.TYPE);
    }

    @Test
    public void testErrorReportingFunctionJavaScript() {
        testErrorReporting("error_reporting_function", JavaScriptConstants.TYPE);
    }

    @Test
    public void testErrorReportingFunctionPython() {
        testErrorReporting("error_reporting_function", PythonConstants.TYPE);
    }

    @Test
    public void testErrorReportingFunctionRuby() {
        testErrorReporting("error_reporting_function", RubyConstants.TYPE);
    }

    @Test
    public void testErrorReportingTriggerGroovy() {
        testErrorReporting("error_reporting_trigger", GroovyConstants.TYPE);
    }

    @Test
    public void testErrorReportingTriggerJavaScript() {
        testErrorReporting("error_reporting_trigger", JavaScriptConstants.TYPE);
    }

    @Test
    public void testErrorReportingTriggerPython() {
        testErrorReporting("error_reporting_trigger", PythonConstants.TYPE);
    }

    @Test
    public void testErrorReportingTriggerRuby() {
        testErrorReporting("error_reporting_trigger", RubyConstants.TYPE);
    }

    protected void testErrorReporting(String name, KnowledgeBaseType type) {
        StandaloneEngineMain engineMain = null;
        try {
            List<String> args = new ArrayList<>();
            args.add("-k");
            args.add("examples/standalone/" + name + "/" + name + "." + type.getFileExtensions().get(0));
            engineMain = StandaloneTestUtils.startupStandaloneEngineMain(args.toArray(new String[args.size()]));

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!engineMain.getEngine().isError()) {
                fail("Expected exception");
            }
        } catch (SpongeException e) {
            // Ignore expected exception.
        } finally {
            StandaloneTestUtils.shutdownStandaloneEngineMain(engineMain);
        }
    }
}
