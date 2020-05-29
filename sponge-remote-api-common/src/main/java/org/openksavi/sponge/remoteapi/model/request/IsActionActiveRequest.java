/*
 * Copyright 2016-2019 The Sponge authors.
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.ProcessorQualifiedVersion;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.model.request.IsActionActiveRequest.IsActionActiveParams;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.util.HasFeatures;

@ApiModel(value = "IsActionActiveRequest", description = "An action active request")
public class IsActionActiveRequest extends TypedParamsRequest<IsActionActiveParams> {

    public IsActionActiveRequest(IsActionActiveParams params) {
        super(RemoteApiConstants.METHOD_IS_ACTION_ACTIVE, params);
    }

    public IsActionActiveRequest() {
        this(new IsActionActiveParams());
    }

    @Override
    public IsActionActiveParams createParams() {
        return new IsActionActiveParams();
    }

    @ApiModel(value = "IsActionActiveParams", description = "An action active request params")
    public static class IsActionActiveParams extends BaseRequestParams {

        private List<IsActionActiveEntry> entries;

        public IsActionActiveParams(List<IsActionActiveEntry> entries) {
            this.entries = entries;
        }

        public IsActionActiveParams() {
        }

        @ApiModelProperty(value = "The query entries", required = true)
        public List<IsActionActiveEntry> getEntries() {
            return entries;
        }

        public void setEntries(List<IsActionActiveEntry> entries) {
            this.entries = entries;
        }
    }

    @ApiModel(value = "IsActionActiveEntry", description = "An action active request entry")
    @SuppressWarnings("rawtypes")
    public static class IsActionActiveEntry implements ActionExecutionInfo, HasFeatures, Cloneable {

        private String name;

        private Object contextValue;

        private DataType contextType;

        private List<Object> args;

        private Map<String, Object> features;

        private ProcessorQualifiedVersion qualifiedVersion;

        public IsActionActiveEntry(String name, Object contextValue, DataType contextType, List<Object> args, Map<String, Object> features,
                ProcessorQualifiedVersion qualifiedVersion) {
            this.name = name;
            this.contextValue = contextValue;
            this.contextType = contextType;
            this.args = args;
            this.features = features;
            this.qualifiedVersion = qualifiedVersion;
        }

        public IsActionActiveEntry(String name) {
            this.name = name;
        }

        public IsActionActiveEntry() {
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

        @ApiModelProperty(value = "The context value", required = false)
        public Object getContextValue() {
            return contextValue;
        }

        public void setContextValue(Object contextValue) {
            this.contextValue = contextValue;
        }

        @ApiModelProperty(value = "The context type", required = false)
        public DataType getContextType() {
            return contextType;
        }

        public void setContextType(DataType contextType) {
            this.contextType = contextType;
        }

        @ApiModelProperty(value = "The action arguments in the context", required = false)
        public List<Object> getArgs() {
            return args;
        }

        public void setArgs(List<Object> args) {
            this.args = args;
        }

        @Override
        @ApiModelProperty(value = "The features", required = false)
        public Map<String, Object> getFeatures() {
            return features;
        }

        @Override
        public void setFeatures(Map<String, Object> features) {
            this.features = features;
        }

        @Override
        @ApiModelProperty(value = "The action qualified version", required = false)
        public ProcessorQualifiedVersion getQualifiedVersion() {
            return qualifiedVersion;
        }

        @Override
        public void setQualifiedVersion(ProcessorQualifiedVersion qualifiedVersion) {
            this.qualifiedVersion = qualifiedVersion;
        }

        public IsActionActiveEntry withContextValue(Object contextValue) {
            setContextValue(contextValue);
            return this;
        }

        public IsActionActiveEntry withContextType(DataType contextType) {
            setContextType(contextType);
            return this;
        }

        public IsActionActiveEntry withArgs(List<Object> args) {
            setArgs(args);
            return this;
        }

        public IsActionActiveEntry withFeatures(Map<String, Object> features) {
            setFeatures(features);
            return this;
        }

        public IsActionActiveEntry withQualifiedVersion(ProcessorQualifiedVersion qualifiedVersion) {
            setQualifiedVersion(qualifiedVersion);
            return this;
        }

        @Override
        public IsActionActiveEntry clone() {
            try {
                IsActionActiveEntry cloned = (IsActionActiveEntry) super.clone();

                cloned.args = args != null ? new ArrayList<>(args) : null;
                cloned.features = features != null ? new LinkedHashMap<>(features) : null;

                return cloned;
            } catch (CloneNotSupportedException e) {
                throw new SpongeException(e);
            }
        }
    }
}
