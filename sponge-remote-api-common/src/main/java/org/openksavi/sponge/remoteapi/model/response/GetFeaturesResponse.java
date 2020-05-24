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

package org.openksavi.sponge.remoteapi.model.response;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.remoteapi.model.response.GetFeaturesResponse.GetFeaturesResult;

@ApiModel(value = "GetFeaturesResponse", description = "A get features response")
public class GetFeaturesResponse extends BaseResponse<GetFeaturesResult> {

    public GetFeaturesResponse(GetFeaturesResult result) {
        super(result);
    }

    public GetFeaturesResponse() {
        this(null);
    }

    @ApiModel(value = "GetFeaturesResult", description = "A get features response result")
    public static class GetFeaturesResult extends BaseResponseResult<Map<String, Object>> {

        public GetFeaturesResult() {
        }

        public GetFeaturesResult(Map<String, Object> features) {
            super(features);
        }

        @Override
        @ApiModelProperty(value = "The API features", required = true)
        public Map<String, Object> getValue() {
            return super.getValue();
        }
    }
}
