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

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.ProcessConfiguration.RedirectType;
import org.openksavi.sponge.engine.SpongeEngine;

/**
 * This class defines a set of process utility methods.
 */
public abstract class ProcessUtils {

    public static final String TAG_PROCESS_EXECUTABLE = "executable";

    public static final String TAG_PROCESS_ARGUMENT = "argument";

    public static final String TAG_PROCESS_WORKING_DIR = "workingDir";

    public static final String TAG_PROCESS_ENV = "env";

    public static final String TAG_PROCESS_WAIT_SECONDS = "waitSeconds";

    public static final String TAG_PROCESS_REDIRECT_TYPE = "redirectType";

    public static final String TAG_PROCESS_CHARSET = "charset";

    public static final String TAG_PROCESS_WAIT_FOR_OUTPUT_LINE_REGEXP = "waitForOutputLineRegexp";

    public static final String TAG_PROCESS_WAIT_FOR_ERROR_OUTPUT_LINE_REGEXP = "waitForErrorOutputLineRegexp";

    public static final String TAG_PROCESS_WAIT_FOR_OUTPUT_LINE_TIMEOUT = "waitForOutputLineTimeout";

    public static final String ATTR_PROCESS_ENV_NAME = "name";

    public static ProcessConfiguration.Builder createProcessConfigurationBuilder(Configuration configuration) {
        Map<String, String> env = new LinkedHashMap<>();
        Arrays.stream(configuration.getConfigurationsAt(TAG_PROCESS_ENV)).forEach(c -> {
            env.put(Validate.notNull(c.getAttribute(ATTR_PROCESS_ENV_NAME, null), "The environment variable must have a name"),
                    c.getValue());
        });

        ProcessConfiguration.Builder builder = ProcessConfiguration.builder(configuration.getString(TAG_PROCESS_EXECUTABLE, null))
                .arguments(Arrays.stream(configuration.getConfigurationsAt(TAG_PROCESS_ARGUMENT)).map(Configuration::getValue)
                        .collect(Collectors.toList()))
                .workingDir(configuration.getString(TAG_PROCESS_WORKING_DIR, null)).env(env)
                .waitSeconds(configuration.getLong(TAG_PROCESS_WAIT_SECONDS, null));

        String redirectTypeString = configuration.getString(TAG_PROCESS_REDIRECT_TYPE, null);
        if (redirectTypeString != null) {
            builder.redirectType(RedirectType.valueOf(redirectTypeString));
        }

        String charsetString = configuration.getString(TAG_PROCESS_CHARSET, null);
        if (charsetString != null) {
            builder.charset(Charset.forName(charsetString));
        }

        builder.waitForOutputLineRegexp(configuration.getString(TAG_PROCESS_WAIT_FOR_OUTPUT_LINE_REGEXP, null))
                .waitForErrorOutputLineRegexp(configuration.getString(TAG_PROCESS_WAIT_FOR_ERROR_OUTPUT_LINE_REGEXP, null))
                .waitForOutputLineTimeout(configuration.getLong(TAG_PROCESS_WAIT_FOR_OUTPUT_LINE_TIMEOUT, null));

        return builder;
    }

    public static ProcessInstance startProcess(SpongeEngine engine, ProcessConfiguration processConfiguration) {
        return new ProcessInstanceRuntime(engine, processConfiguration).start();
    }

    protected ProcessUtils() {
        //
    }
}
