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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.model.request.GetActionsRequest.GetActionsParams;

@ApiModel(value = "GetActionsRequest", description = "A get actions request")
public class GetActionsRequest extends BaseRequest<GetActionsParams> {

    public GetActionsRequest(GetActionsParams body) {
        super(RemoteApiConstants.OPERATION_ACTIONS, body);
    }

    public GetActionsRequest() {
        this(new GetActionsParams());
    }

    @Override
    public GetActionsParams createParams() {
        return new GetActionsParams();
    }

    @ApiModel(value = "GetActionsParams", description = "A get actions request params")
    public static class GetActionsParams extends BaseRequestParams {

        private String name;

        private Boolean metadataRequired;

        private Boolean registeredTypes;

        @ApiModelProperty(
                value = "The action name or the regular expression (compatible with https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)",
                required = false)
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @ApiModelProperty(value = "The metadata required flag", required = false)
        public Boolean getMetadataRequired() {
            return metadataRequired;
        }

        public void setMetadataRequired(Boolean metadataRequired) {
            this.metadataRequired = metadataRequired;
        }

        @ApiModelProperty(value = "The flag for requesting registered types used in the actions in the result (defaults to false)",
                required = false)
        public Boolean getRegisteredTypes() {
            return registeredTypes;
        }

        public void setRegisteredTypes(Boolean registeredTypes) {
            this.registeredTypes = registeredTypes;
        }
    }
}
