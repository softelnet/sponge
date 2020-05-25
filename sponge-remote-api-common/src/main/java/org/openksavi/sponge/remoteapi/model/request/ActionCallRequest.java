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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.ProcessorQualifiedVersion;
import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.model.request.ActionCallRequest.ActionCallParams;

@ApiModel(value = "ActionCallRequest", description = "An action call request")
public class ActionCallRequest extends TypedParamsRequest<ActionCallParams> {

    public ActionCallRequest(ActionCallParams params) {
        super(RemoteApiConstants.METHOD_CALL, params);
    }

    public ActionCallRequest() {
        this(new ActionCallParams());
    }

    @Override
    public ActionCallParams createParams() {
        return new ActionCallParams();
    }

    @ApiModel(value = "ActionCallParams", description = "Action call request params")
    public static class ActionCallParams extends BaseRequestParams implements ActionExecutionInfo {

        private String name;

        @JsonInclude(Include.NON_NULL)
        private Object args;

        @JsonInclude(Include.NON_NULL)
        private ProcessorQualifiedVersion qualifiedVersion;

        public ActionCallParams(String name, Object args, ProcessorQualifiedVersion qualifiedVersion) {
            this.name = name;
            this.args = args;
            this.qualifiedVersion = qualifiedVersion;
        }

        public ActionCallParams() {
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
        public Object getArgs() {
            return args;
        }

        public void setArgs(Object args) {
            Validate.isTrue(args == null || args instanceof List || args instanceof Map,
                    "Action args should be an instance of a List or a Map");

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
