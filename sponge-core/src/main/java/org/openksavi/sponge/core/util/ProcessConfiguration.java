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
import java.util.List;

public class ProcessConfiguration {

    private String name = "Process";

    private String executable;

    private List<String> arguments;

    private String workingDir;

    private long waitSeconds = 0;

    private RedirectType redirectType = RedirectType.INHERIT;

    private Charset charset;

    public static enum RedirectType {
        LOGGER, INHERIT, STRING, NONE
    }

    public ProcessConfiguration name(String name) {
        this.name = name;
        return this;
    }

    public ProcessConfiguration executable(String executable) {
        this.executable = executable;
        return this;
    }

    public ProcessConfiguration arguments(String... arguments) {
        this.arguments = Arrays.asList(arguments);
        return this;
    }

    public ProcessConfiguration arguments(List<String> arguments) {
        this.arguments = new ArrayList<>(arguments);
        return this;
    }

    public ProcessConfiguration workingDir(String workingDir) {
        this.workingDir = workingDir;
        return this;
    }

    public ProcessConfiguration waitSeconds(long waitSeconds) {
        this.waitSeconds = waitSeconds;
        return this;
    }

    public ProcessConfiguration redirectType(RedirectType redirectType) {
        this.redirectType = redirectType;
        return this;
    }

    public ProcessConfiguration charset(Charset charset) {
        this.charset = charset;
        return this;
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

    public long getWaitSeconds() {
        return waitSeconds;
    }

    public void setWaitSeconds(long waitSeconds) {
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
}
