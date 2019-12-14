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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.restapi.model.response.GetVersionResponse.GetVersionResponseBody;

@ApiModel(value = "GetVersionResponse", description = "A get version response")
public class GetVersionResponse extends BodySpongeResponse<GetVersionResponseBody> {

    public GetVersionResponse(GetVersionResponseBody body) {
        super(body);
    }

    public GetVersionResponse() {
        this(new GetVersionResponseBody());
    }

    public GetVersionResponse(String version) {
        this(new GetVersionResponseBody(version));
    }

    @Override
    public GetVersionResponseBody createBody() {
        return new GetVersionResponseBody();
    }

    @ApiModel(value = "GetVersionResponseBody", description = "A get version response body")
    public static class GetVersionResponseBody implements ResponseBody {

        private String version;

        public GetVersionResponseBody() {
        }

        public GetVersionResponseBody(String version) {
            this.version = version;
        }

        @ApiModelProperty(value = "The Sponge version", required = true)
        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
