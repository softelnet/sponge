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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "GetActionsRequest", description = "Get actions request")
public class RestGetActionsRequest extends BaseRestRequest {

    private Boolean metadataRequired;

    public RestGetActionsRequest() {
        //
    }

    public RestGetActionsRequest(String username, String password) {
        super(username, password);
    }

    @ApiModelProperty(value = "Metadata required", required = false)
    public Boolean getMetadataRequired() {
        return metadataRequired;
    }

    public void setMetadataRequired(Boolean metadataRequired) {
        this.metadataRequired = metadataRequired;
    }
}
