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

package org.openksavi.sponge.remoteapi.model.response;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.remoteapi.model.RemoteActionMeta;
import org.openksavi.sponge.remoteapi.model.response.GetActionsResponse.GetActionsResult;
import org.openksavi.sponge.type.DataType;

@ApiModel(value = "GetActionsResponse", description = "A get actions response")
public class GetActionsResponse extends BaseResponse<GetActionsResult> {

    public GetActionsResponse(GetActionsResult body) {
        super(body);
    }

    public GetActionsResponse() {
        this(null);
    }

    @ApiModel(value = "GetActionsResult", description = "A get actions response result")
    public static class GetActionsResult extends BaseResponseResult<GetActionsValue> {

        public GetActionsResult() {
        }

        public GetActionsResult(GetActionsValue value) {
            super(value);
        }

        @Override
        @ApiModelProperty(value = "The actions metadata", required = true)
        public GetActionsValue getValue() {
            return super.getValue();
        }
    }

    public static class GetActionsValue {

        private List<RemoteActionMeta> actions;

        private Map<String, DataType<?>> types;

        public GetActionsValue(List<RemoteActionMeta> actions, Map<String, DataType<?>> types) {
            this.actions = actions;
            this.types = types;
        }

        public GetActionsValue() {
        }

        @ApiModelProperty(value = "The available actions", required = true)
        public List<RemoteActionMeta> getActions() {
            return actions;
        }

        public void setActions(List<RemoteActionMeta> actions) {
            this.actions = actions;
        }

        @ApiModelProperty(value = "The registered types used in the actions", required = false)
        public Map<String, DataType<?>> getTypes() {
            return types;
        }

        public void setTypes(Map<String, DataType<?>> types) {
            this.types = types;
        }
    }
}
