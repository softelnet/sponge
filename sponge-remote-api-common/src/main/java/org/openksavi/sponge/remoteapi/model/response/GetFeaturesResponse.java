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

import java.util.LinkedHashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.remoteapi.model.response.GetFeaturesResponse.GetFeaturesResponseBody;

@ApiModel(value = "GetFeaturesResponse", description = "A get features response")
public class GetFeaturesResponse extends BodySpongeResponse<GetFeaturesResponseBody> {

    public GetFeaturesResponse(GetFeaturesResponseBody body) {
        super(body);
    }

    public GetFeaturesResponse() {
        this(new GetFeaturesResponseBody());
    }

    public GetFeaturesResponse(Map<String, Object> features) {
        this(new GetFeaturesResponseBody(features));
    }

    @Override
    public GetFeaturesResponseBody createBody() {
        return new GetFeaturesResponseBody();
    }

    @ApiModel(value = "GetFeaturesResponseBody", description = "A get features response body")
    public static class GetFeaturesResponseBody implements ResponseBody {

        private Map<String, Object> features = new LinkedHashMap<>();

        public GetFeaturesResponseBody() {
        }

        public GetFeaturesResponseBody(Map<String, Object> features) {
            this.features.putAll(features);
        }

        @ApiModelProperty(value = "The API features", required = true)
        public Map<String, Object> getFeatures() {
            return features;
        }

        public void setFeatures(Map<String, Object> features) {
            this.features = features;
        }
    }
}
