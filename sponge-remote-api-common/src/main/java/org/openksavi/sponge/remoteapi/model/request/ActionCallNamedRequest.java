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

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.ProcessorQualifiedVersion;
import org.openksavi.sponge.remoteapi.model.request.ActionCallNamedRequest.ActionCallNamedRequestBody;

@ApiModel(value = "ActionCallNamedRequest", description = "An action call with named arguments request")
public class ActionCallNamedRequest extends BodySpongeRequest<ActionCallNamedRequestBody> {

    public ActionCallNamedRequest(ActionCallNamedRequestBody body) {
        super(body);
    }

    public ActionCallNamedRequest() {
        this(new ActionCallNamedRequestBody());
    }

    public ActionCallNamedRequest(String name, Map<String, ?> args, ProcessorQualifiedVersion qualifiedVersion) {
        this(new ActionCallNamedRequestBody(name, args, qualifiedVersion));
    }

    public ActionCallNamedRequest(String name, Map<String, ?> args) {
        this(name, args, null);
    }

    @Override
    public ActionCallNamedRequestBody createBody() {
        return new ActionCallNamedRequestBody();
    }

    @ApiModel(value = "ActionCallNamedRequestBody", description = "An action call with named arguments request body")
    public static class ActionCallNamedRequestBody implements RequestBody, ActionExecutionInfo {

        private String name;

        private Map<String, ?> args;

        private ProcessorQualifiedVersion qualifiedVersion;

        public ActionCallNamedRequestBody(String name, Map<String, ?> args, ProcessorQualifiedVersion qualifiedVersion) {
            this.name = name;
            this.args = args;
            this.qualifiedVersion = qualifiedVersion;
        }

        public ActionCallNamedRequestBody() {
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

        @ApiModelProperty(value = "The named action arguments", required = false)
        public Map<String, ?> getArgs() {
            return args;
        }

        public void setArgs(Map<String, ?> args) {
            this.args = args;
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
}
