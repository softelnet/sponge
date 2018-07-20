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

package org.openksavi.sponge.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.ProcessConfiguration.RedirectType;
import org.openksavi.sponge.engine.SpongeEngine;

/**
 * This class defines a set of process utility methods.
 */
public abstract class ProcessUtils {

    private static final Logger logger = LoggerFactory.getLogger(ProcessUtils.class);

    public static final String TAG_PROCESS_EXECUTABLE = "executable";

    public static final String TAG_PROCESS_ARGUMENT = "argument";

    public static final String TAG_PROCESS_WORKING_DIR = "workingDir";

    public static final String TAG_PROCESS_WAIT_SECONDS = "waitSeconds";

    public static final String TAG_PROCESS_REDIRECT_TYPE = "redirectType";

    public static final String TAG_PROCESS_CHARSET = "charset";

    public static ProcessConfiguration createProcessConfiguration(Configuration configuration) {
        ProcessConfiguration processConfiguration = new ProcessConfiguration();

        processConfiguration.executable(configuration.getString(TAG_PROCESS_EXECUTABLE, processConfiguration.getExecutable()));
        processConfiguration.arguments(Arrays.stream(configuration.getConfigurationsAt(TAG_PROCESS_ARGUMENT)).map(Configuration::getValue)
                .collect(Collectors.toList()));
        processConfiguration.workingDir(configuration.getString(TAG_PROCESS_WORKING_DIR, processConfiguration.getWorkingDir()));
        processConfiguration.waitSeconds(configuration.getLong(TAG_PROCESS_WAIT_SECONDS, processConfiguration.getWaitSeconds()));

        String redirectTypeString = configuration.getString(TAG_PROCESS_REDIRECT_TYPE, null);
        processConfiguration.redirectType(
                redirectTypeString != null ? RedirectType.valueOf(redirectTypeString) : processConfiguration.getRedirectType());

        String charsetString = configuration.getString(TAG_PROCESS_CHARSET, null);
        processConfiguration.charset(charsetString != null ? Charset.forName(charsetString) : processConfiguration.getCharset());

        return processConfiguration;
    }

    public static ProcessInstance startProcess(SpongeEngine engine, ProcessConfiguration processConfiguration) {
        List<String> commands = new ArrayList<>();
        commands.add(processConfiguration.getExecutable());
        commands.addAll(processConfiguration.getArguments());

        ProcessBuilder builder = new ProcessBuilder(commands);
        if (processConfiguration.getRedirectType() == RedirectType.INHERIT) {
            builder.inheritIO();
        }

        if (processConfiguration.getWorkingDir() != null) {
            builder.directory(new File(processConfiguration.getWorkingDir()));
        }

        Process process = null;
        try {
            process = builder.start();

            if (processConfiguration.getRedirectType() == RedirectType.LOGGER) {
                SpongeUtils.executeConcurrentlyOnce(engine, new InputStreamLineConsumerRunnable(process.getInputStream(), logger::info));
                SpongeUtils.executeConcurrentlyOnce(engine, new InputStreamLineConsumerRunnable(process.getErrorStream(), logger::warn));
            }

            logger.debug("{}", builder.environment());
        } catch (IOException e) {
            throw SpongeUtils.wrapException(processConfiguration.getName(), e);
        }

        String outputString = null;
        if (processConfiguration.getRedirectType() == RedirectType.STRING) {
            Charset finalCharset = processConfiguration.getCharset() != null ? processConfiguration.getCharset() : Charset.defaultCharset();
            try (BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream(), finalCharset));
                    BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream(), finalCharset))) {
                outputString = output.lines().collect(Collectors.joining("\n"));
                logger.info("{} output:\n{}", processConfiguration.getName(), outputString);

                String errorsString = errors.lines().collect(Collectors.joining("\n"));
                if (!errorsString.isEmpty()) {
                    throw new SpongeException(processConfiguration.getName() + " error: " + errorsString);
                }
            } catch (IOException e) {
                throw SpongeUtils.wrapException(processConfiguration.getName(), e);
            }
        }

        return new ProcessInstance(process, processConfiguration, outputString);
    }

    protected ProcessUtils() {
        //
    }
}
