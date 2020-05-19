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

package org.openksavi.sponge.remoteapi.model.request;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.ProcessorQualifiedVersion;
import org.openksavi.sponge.remoteapi.model.request.ProvideActionArgsRequest.ProvideActionArgsRequestBody;
import org.openksavi.sponge.type.DataType;

@ApiModel(value = "ProvideActionArgsRequest", description = "A provide action arguments request")
@SuppressWarnings("rawtypes")
public class ProvideActionArgsRequest extends BodySpongeRequest<ProvideActionArgsRequestBody> {

    public ProvideActionArgsRequest(ProvideActionArgsRequestBody body) {
        super(body);
    }

    public ProvideActionArgsRequest() {
        this(new ProvideActionArgsRequestBody());
    }

    public ProvideActionArgsRequest(String name, List<String> provide, List<String> submit, Map<String, Object> current,
            Map<String, DataType> dynamicTypes, Map<String, Map<String, Object>> argFeatures, ProcessorQualifiedVersion qualifiedVersion,
            Boolean initial) {
        this(new ProvideActionArgsRequestBody(name, provide, submit, current, dynamicTypes, argFeatures, qualifiedVersion, initial));
    }

    public ProvideActionArgsRequest(String name, List<String> provide, List<String> submit, Map<String, Object> current,
            Map<String, DataType> dynamicTypes, Map<String, Map<String, Object>> argFeatures) {
        this(new ProvideActionArgsRequestBody(name, provide, submit, current, dynamicTypes, argFeatures, null, null));
    }

    @Override
    public ProvideActionArgsRequestBody createBody() {
        return new ProvideActionArgsRequestBody();
    }

    @ApiModel(value = "ProvideActionArgsRequestBody", description = "A provide action arguments request body")
    public static class ProvideActionArgsRequestBody implements RequestBody, ActionExecutionInfo {

        private String name;

        private List<String> provide;

        private List<String> submit;

        private Map<String, Object> current;

        private Map<String, DataType> dynamicTypes;

        private Map<String, Map<String, Object>> argFeatures;

        private ProcessorQualifiedVersion qualifiedVersion;

        private Boolean initial;

        public ProvideActionArgsRequestBody(String name, List<String> provide, List<String> submit, Map<String, Object> current,
                Map<String, DataType> dynamicTypes, Map<String, Map<String, Object>> argFeatures,
                ProcessorQualifiedVersion qualifiedVersion, Boolean initial) {
            this.name = name;
            this.provide = provide;
            this.submit = submit;
            this.current = current;
            this.dynamicTypes = dynamicTypes;
            this.argFeatures = argFeatures;
            this.qualifiedVersion = qualifiedVersion;
            this.initial = initial;
        }

        public ProvideActionArgsRequestBody(String name, List<String> provide, List<String> submit, Map<String, Object> current,
                Map<String, DataType> dynamicTypes, Map<String, Map<String, Object>> features) {
            this(name, provide, submit, current, dynamicTypes, features, null, null);
        }

        public ProvideActionArgsRequestBody() {
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
        public List<String> getProvide() {
            return provide;
        }

        public void setProvide(List<String> provide) {
            this.provide = provide;
        }

        @ApiModelProperty(value = "The names of action arguments to submit", required = false)
        public List<String> getSubmit() {
            return submit;
        }

        public void setSubmit(List<String> submit) {
            this.submit = submit;
        }

        @ApiModelProperty(value = "The current values of action arguments in a client code", required = false)
        public Map<String, Object> getCurrent() {
            return current;
        }

        public void setCurrent(Map<String, Object> current) {
            this.current = current;
        }

        @ApiModelProperty(value = "The types of dynamic values for provide and current", required = false)
        public Map<String, DataType> getDynamicTypes() {
            return dynamicTypes;
        }

        public void setDynamicTypes(Map<String, DataType> dynamicTypes) {
            this.dynamicTypes = dynamicTypes;
        }

        @ApiModelProperty(value = "The features for arguments", required = false)
        public Map<String, Map<String, Object>> getArgFeatures() {
            return argFeatures;
        }

        public void setArgFeatures(Map<String, Map<String, Object>> argFeatures) {
            this.argFeatures = argFeatures;
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

        @ApiModelProperty(value = "The flag indicating if this is the initial provide action arguments request", required = false)
        public Boolean getInitial() {
            return initial;
        }

        public void setInitial(Boolean initial) {
            this.initial = initial;
        }
    }
}
