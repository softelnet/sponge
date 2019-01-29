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

package org.openksavi.sponge.core.util.process;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.util.process.ErrorRedirect;
import org.openksavi.sponge.util.process.InputRedirect;
import org.openksavi.sponge.util.process.OutputRedirect;
import org.openksavi.sponge.util.process.ProcessConfiguration;
import org.openksavi.sponge.util.process.ProcessConfigurationBuilder;

/**
 * This class defines a set of process utility methods.
 */
public abstract class ProcessUtils {

    public static final String TAG_PROCESS_EXECUTABLE = "executable";

    public static final String TAG_PROCESS_ARGUMENT = "argument";

    public static final String TAG_PROCESS_WORKING_DIR = "workingDir";

    public static final String TAG_PROCESS_ENV = "env";

    public static final String TAG_PROCESS_WAIT_SECONDS = "waitSeconds";

    public static final String TAG_PROCESS_INPUT_REDIRECT = "inputRedirect";

    public static final String TAG_PROCESS_OUTPUT_REDIRECT = "outputRedirect";

    public static final String TAG_PROCESS_ERROR_REDIRECT = "errorRedirect";

    public static final String TAG_PROCESS_CHARSET = "charset";

    public static final String TAG_PROCESS_WAIT_FOR_POSITIVE_LINE_REGEXP = "waitForPositiveLineRegexp";

    public static final String TAG_PROCESS_WAIT_FOR_NEGATIVE_LINE_REGEXP = "waitForNegativeLineRegexp";

    public static final String TAG_PROCESS_WAIT_FOR_LINE_TIMEOUT = "waitForLineTimeout";

    public static final String TAG_PROCESS_INPUT_STRING = "inputString";

    public static final String TAG_PROCESS_INPUT_BINARY = "inputBinary";

    public static final String ATTR_PROCESS_ENV_NAME = "name";

    private ProcessUtils() {
        //
    }

    public static ProcessConfigurationBuilder createProcessConfigurationBuilder(Configuration configuration) {
        Map<String, String> env = new LinkedHashMap<>();
        configuration.getConfigurationsAt(TAG_PROCESS_ENV).forEach(c -> {
            env.put(Validate.notNull(c.getAttribute(ATTR_PROCESS_ENV_NAME, null), "The environment variable must have a name"),
                    c.getValue());
        });

        ProcessConfigurationBuilder builder = ProcessConfiguration.builder(configuration.getString(TAG_PROCESS_EXECUTABLE, null))
                .arguments(configuration.getConfigurationsAt(TAG_PROCESS_ARGUMENT).stream().map(Configuration::getValue)
                        .collect(Collectors.toList()))
                .workingDir(configuration.getString(TAG_PROCESS_WORKING_DIR, null)).env(env)
                .waitSeconds(configuration.getLong(TAG_PROCESS_WAIT_SECONDS, null));

        String inputRedirectString = configuration.getString(TAG_PROCESS_INPUT_REDIRECT, null);
        if (inputRedirectString != null) {
            builder.inputRedirect(InputRedirect.valueOf(inputRedirectString));
        }
        String outputRedirectString = configuration.getString(TAG_PROCESS_OUTPUT_REDIRECT, null);
        if (outputRedirectString != null) {
            builder.outputRedirect(OutputRedirect.valueOf(outputRedirectString));
        }
        String errorRedirectString = configuration.getString(TAG_PROCESS_ERROR_REDIRECT, null);
        if (errorRedirectString != null) {
            builder.errorRedirect(ErrorRedirect.valueOf(errorRedirectString));
        }

        String charsetString = configuration.getString(TAG_PROCESS_CHARSET, null);
        if (charsetString != null) {
            builder.charset(Charset.forName(charsetString));
        }

        String inputStringString = configuration.getString(TAG_PROCESS_INPUT_STRING, null);
        if (inputStringString != null) {
            builder.inputAsString(inputStringString);
        }
        String inputBinaryString = configuration.getString(TAG_PROCESS_INPUT_BINARY, null);
        if (inputBinaryString != null) {
            builder.inputAsBinary(Base64.getDecoder().decode(inputBinaryString));
        }

        builder.waitForPositiveLineRegexp(configuration.getString(TAG_PROCESS_WAIT_FOR_POSITIVE_LINE_REGEXP, null))
                .waitForNegativeLineRegexp(configuration.getString(TAG_PROCESS_WAIT_FOR_NEGATIVE_LINE_REGEXP, null))
                .waitForLineTimeout(configuration.getLong(TAG_PROCESS_WAIT_FOR_LINE_TIMEOUT, null));

        return builder;
    }

    public static boolean isOutputRedirectSavingInstantly(ProcessConfiguration configuration) {
        return configuration.getOutputRedirect() == OutputRedirect.STRING || configuration.getOutputRedirect() == OutputRedirect.BINARY
                || configuration.getOutputRedirect() == OutputRedirect.FILE;
    }

    public static boolean isErrorRedirectSavingInstantly(ProcessConfiguration configuration) {
        return configuration.getErrorRedirect() == ErrorRedirect.STRING || configuration.getErrorRedirect() == ErrorRedirect.FILE;
    }

    public static boolean isRedirectSavingInstantly(ProcessConfiguration configuration) {
        return isOutputRedirectSavingInstantly(configuration) || isErrorRedirectSavingInstantly(configuration);
    }

    public static void validateProcessConfiguration(ProcessConfiguration configuration) {
        Validate.isTrue(configuration.getOutputLineConsumer() == null || configuration.getOutputRedirect() == OutputRedirect.CONSUMER,
                "If the output line consumer is provided, the output redirect type must be CONSUMER");
        Validate.isTrue(configuration.getErrorLineConsumer() == null || configuration.getErrorRedirect() == ErrorRedirect.CONSUMER,
                "If the error line consumer is provided, the error redirect type must be CONSUMER");

        Validate.isTrue(
                !shouldWaitForSpecificLine(configuration) || configuration.getOutputRedirect() == OutputRedirect.CONSUMER
                        || configuration.getErrorRedirect() == ErrorRedirect.CONSUMER,
                "If the waiting for a specific line is set, the output or error redirect type must be CONSUMER");

        Validate.isTrue(configuration.getInputRedirect() != InputRedirect.STRING || configuration.getInputString() != null,
                "The input string must be set if the input redirect is STRING");

        Validate.isTrue(configuration.getInputRedirect() != InputRedirect.BINARY || configuration.getInputBinary() != null,
                "The input binary must be set if the input redirect is BINARY");

        Validate.isTrue(configuration.getInputRedirect() != InputRedirect.FILE || configuration.getInputFile() != null,
                "The input file must be set if the input redirect is FILE");

        Validate.isTrue(configuration.getOutputRedirect() != OutputRedirect.FILE || configuration.getOutputFile() != null,
                "The output file must be set if the output redirect is FILE");

        Validate.isTrue(configuration.getErrorRedirect() != ErrorRedirect.FILE || configuration.getErrorFile() != null,
                "The error file must be set if the error redirect is FILE");
    }

    public static boolean shouldWaitForReadyInstantly(ProcessConfiguration configuration) {
        return configuration.getInputRedirect() != InputRedirect.STREAM;
    }

    public static boolean shouldWaitForSpecificLine(ProcessConfiguration configuration) {
        return configuration.getWaitForPositiveLineRegexp() != null || configuration.getWaitForNegativeLineRegexp() != null;
    }
}
