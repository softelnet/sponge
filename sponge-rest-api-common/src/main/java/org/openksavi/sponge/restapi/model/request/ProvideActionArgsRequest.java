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

package org.openksavi.sponge.restapi.model.request;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.ProcessorQualifiedVersion;

@ApiModel(value = "ProvideActionArgsRequest", description = "A provide action arguments request")
public class ProvideActionArgsRequest extends SpongeRequest implements ActionExecutionRequest {

    private String name;

    private List<String> argNames;

    private Map<String, Object> current;

    private ProcessorQualifiedVersion qualifiedVersion;

    public ProvideActionArgsRequest() {
    }

    public ProvideActionArgsRequest(String name, List<String> argNames, Map<String, Object> current,
            ProcessorQualifiedVersion qualifiedVersion) {
        this.name = name;
        this.argNames = argNames;
        this.current = current;
        this.qualifiedVersion = qualifiedVersion;
    }

    public ProvideActionArgsRequest(String name, List<String> argNames, Map<String, Object> current) {
        this(name, argNames, current, null);
    }

    @Override
    @ApiModelProperty(value = "The action name", required = true)
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(value = "The names of action arguments to provide", required = false)
    public List<String> getArgNames() {
        return argNames;
    }

    public void setArgNames(List<String> argNames) {
        this.argNames = argNames;
    }

    @ApiModelProperty(value = "The current values of action arguments in a client code", required = false)
    public Map<String, Object> getCurrent() {
        return current;
    }

    public void setCurrent(Map<String, Object> current) {
        this.current = current;
    }

    @Override
    @ApiModelProperty(value = "The action expected qualified version", required = false)
    public ProcessorQualifiedVersion getQualifiedVersion() {
        return qualifiedVersion;
    }

    @Override
    public void setQualifiedVersion(ProcessorQualifiedVersion qualifiedVersion) {
        this.qualifiedVersion = qualifiedVersion;
    }
}
