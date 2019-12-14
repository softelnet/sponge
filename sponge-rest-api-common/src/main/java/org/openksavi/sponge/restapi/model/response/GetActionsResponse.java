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

package org.openksavi.sponge.restapi.model.response;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.response.GetActionsResponse.GetActionsResponseBody;
import org.openksavi.sponge.type.DataType;

@ApiModel(value = "GetActionsResponse", description = "A get actions response")
public class GetActionsResponse extends BodySpongeResponse<GetActionsResponseBody> {

    public GetActionsResponse(GetActionsResponseBody body) {
        super(body);
    }

    public GetActionsResponse() {
        this(new GetActionsResponseBody());
    }

    public GetActionsResponse(List<RestActionMeta> actions, Map<String, DataType<?>> types) {
        this(new GetActionsResponseBody(actions, types));
    }

    @Override
    public GetActionsResponseBody createBody() {
        return new GetActionsResponseBody();
    }

    @ApiModel(value = "GetActionsResponseBody", description = "A get actions response body")
    public static class GetActionsResponseBody implements ResponseBody {

        private List<RestActionMeta> actions;

        private Map<String, DataType<?>> types;

        public GetActionsResponseBody(List<RestActionMeta> actions, Map<String, DataType<?>> types) {
            this.actions = actions;
            this.types = types;
        }

        public GetActionsResponseBody() {
        }

        @ApiModelProperty(value = "The available actions", required = true)
        public List<RestActionMeta> getActions() {
            return actions;
        }

        public void setActions(List<RestActionMeta> actions) {
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
