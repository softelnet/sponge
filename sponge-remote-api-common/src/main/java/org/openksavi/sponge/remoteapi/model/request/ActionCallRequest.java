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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.ProcessorQualifiedVersion;
import org.openksavi.sponge.remoteapi.model.request.ActionCallRequest.ActionCallRequestBody;

@ApiModel(value = "ActionCallRequest", description = "An action call request")
public class ActionCallRequest extends BodySpongeRequest<ActionCallRequestBody> {

    public ActionCallRequest(ActionCallRequestBody body) {
        super(body);
    }

    public ActionCallRequest() {
        this(new ActionCallRequestBody());
    }

    public ActionCallRequest(String name, List<Object> args, ProcessorQualifiedVersion qualifiedVersion) {
        this(new ActionCallRequestBody(name, args, qualifiedVersion));
    }

    public ActionCallRequest(String name, List<Object> args) {
        this(name, args, null);
    }

    @Override
    public ActionCallRequestBody createBody() {
        return new ActionCallRequestBody();
    }

    @ApiModel(value = "ActionCallRequestBody", description = "An action call request body")
    public static class ActionCallRequestBody implements RequestBody, ActionExecutionInfo {

        private String name;

        private List<Object> args;

        private ProcessorQualifiedVersion qualifiedVersion;

        public ActionCallRequestBody(String name, List<Object> args, ProcessorQualifiedVersion qualifiedVersion) {
            this.name = name;
            this.args = args;
            this.qualifiedVersion = qualifiedVersion;
        }

        public ActionCallRequestBody() {
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

        @ApiModelProperty(value = "The action arguments", required = false)
        public List<Object> getArgs() {
            return args;
        }

        public void setArgs(List<Object> args) {
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
