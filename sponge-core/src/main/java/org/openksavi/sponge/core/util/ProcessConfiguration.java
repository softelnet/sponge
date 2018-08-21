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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.SpongeException;

/**
 * A process configuration.
 */
public class ProcessConfiguration implements Cloneable {

    private String name = "Process";

    private String executable;

    private List<String> arguments = new ArrayList<>();

    private String workingDir;

    private Map<String, String> env = new LinkedHashMap<>();

    private Long waitSeconds;

    private RedirectType redirectType = RedirectType.INHERIT;

    private Charset charset;

    private String waitForOutputLineRegexp;

    private Long waitForOutputLineTimeout;

    /** The subprocess redirect type. */
    public static enum RedirectType {

        /** Logs the subprocess standard output (as INFO) and error output (as WARN) to the logger. */
        LOGGER,

        /** Sets the source and destination for subprocess standard I/O to be the same as those of the current Java process. */
        INHERIT,

        /**
         * Writes all subprocess standard output and error output to a {@code ProcessInstance.output} string. The thread that started the
         * subprocess will wait for the subprocess to exit.
         */
        STRING,

        /** No redirection will be set. */
        NONE
    }

    public ProcessConfiguration(String executable) {
        this.executable = executable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public void setEnv(Map<String, String> env) {
        this.env = env;
    }

    public Long getWaitSeconds() {
        return waitSeconds;
    }

    public void setWaitSeconds(Long waitSeconds) {
        this.waitSeconds = waitSeconds;
    }

    public RedirectType getRedirectType() {
        return redirectType;
    }

    public void setRedirectType(RedirectType redirectType) {
        this.redirectType = redirectType;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getWaitForOutputLineRegexp() {
        return waitForOutputLineRegexp;
    }

    public void setWaitForOutputLineRegexp(String waitForOutputLineRegexp) {
        this.waitForOutputLineRegexp = waitForOutputLineRegexp;
    }

    public Long getWaitForOutputLineTimeout() {
        return waitForOutputLineTimeout;
    }

    public void setWaitForOutputLineTimeout(Long waitForOutputLineTimeout) {
        this.waitForOutputLineTimeout = waitForOutputLineTimeout;
    }

    @Override
    public ProcessConfiguration clone() {
        try {
            ProcessConfiguration result = (ProcessConfiguration) super.clone();

            result.setArguments(new ArrayList<>(arguments));
            result.setEnv(new LinkedHashMap<>(env));

            return result;
        } catch (CloneNotSupportedException e) {
            throw new SpongeException(e);
        }
    }

    /**
     * A process configuration builder.
     *
     * @param executable the executable file name.
     * @return the builder.
     */
    public static Builder builder(String executable) {
        return new Builder(executable);
    }

    public static class Builder {

        private ProcessConfiguration configuration;

        public Builder(String executable) {
            configuration = new ProcessConfiguration(executable);
        }

        public ProcessConfiguration build() {
            return configuration;
        }

        /**
         * Sets the process display name.
         *
         * @param name the process display name.
         * @return this builder.
         */
        public Builder name(String name) {
            configuration.setName(name);
            return this;
        }

        /**
         * Adds the process arguments.
         *
         * @param arguments the process arguments.
         * @return this builder.
         */
        public Builder arguments(String... arguments) {
            configuration.getArguments().addAll(Arrays.asList(arguments));
            return this;
        }

        /**
         * Add the process arguments.
         *
         * @param arguments the process arguments.
         * @return this builder.
         */
        public Builder arguments(List<String> arguments) {
            configuration.getArguments().addAll(arguments);
            return this;
        }

        /**
         * Sets the process working directory.
         *
         * @param workingDir the process working directory. If {@code null} (the default value) then the current directory will be used.
         * @return this builder.
         */
        public Builder workingDir(String workingDir) {
            configuration.setWorkingDir(workingDir);
            return this;
        }

        /**
         * Adds the environment variable.
         *
         * @param name the environment variable name.
         * @param value the environment variable value.
         * @return this builder.
         */
        public Builder env(String name, String value) {
            configuration.getEnv().put(name, value);
            return this;
        }

        /**
         * Adds the environment variables.
         *
         * @param env the environment variables.
         * @return this builder.
         */
        public Builder env(Map<String, String> env) {
            configuration.getEnv().putAll(env);
            return this;
        }

        /**
         * Sets the maximum number of seconds to wait after the start of the process. The thread that started the process will be blocked
         * until the time elapses or the subprocess exits.
         *
         * @param waitSeconds the maximum number of seconds to wait or {@code null} (the default value) if the thread shouldn't wait.
         * @return this builder.
         */
        public Builder waitSeconds(Long waitSeconds) {
            configuration.setWaitSeconds(waitSeconds);
            return this;
        }

        /**
         * Sets the redirect type. The default value is {@code RedirectType.INHERIT}.
         *
         * @param redirectType the redirect type.
         * @return this builder.
         */
        public Builder redirectType(RedirectType redirectType) {
            configuration.setRedirectType(redirectType);
            return this;
        }

        /**
         * Sets the charset of the subprocess output stream used if the {@code redirectType} is {@code RedirectType.STRING}.
         *
         * @param charset the charset.
         * @return this builder.
         */
        public Builder charset(Charset charset) {
            configuration.setCharset(charset);
            return this;
        }

        /**
         * Sets the Java regular expression of a line from the process output stream. The thread that started the process will wait
         * (blocking) for such line.
         *
         * @param waitForOutputLineRegexp the Java regular expression or {@code null} if the thread shouldn't wait for a specific line.
         * @return this builder.
         */
        public Builder waitForOutputLineRegexp(String waitForOutputLineRegexp) {
            configuration.setWaitForOutputLineRegexp(waitForOutputLineRegexp);
            return this;
        }

        /**
         * Sets the timeout for waiting for a specific line from the process output stream (in seconds). If the timeout is exceeded, the
         * exception will be thrown.
         *
         * @param waitForOutputLineTimeout the timeout for waiting for a specific line or {@code null} if the thread could wait
         *        indefinitely.
         * @return this builder.
         */
        public Builder waitForOutputLineTimeout(Long waitForOutputLineTimeout) {
            configuration.setWaitForOutputLineTimeout(waitForOutputLineTimeout);
            return this;
        }
    }
}
