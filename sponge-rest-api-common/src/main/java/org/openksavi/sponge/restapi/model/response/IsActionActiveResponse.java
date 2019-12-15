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

package org.openksavi.sponge.restapi.model.response;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.restapi.model.response.IsActionActiveResponse.IsActionActiveResponseBody;

@ApiModel(value = "IsActionActiveResponse", description = "An action active response")
public class IsActionActiveResponse extends BodySpongeResponse<IsActionActiveResponseBody> {

    public IsActionActiveResponse(IsActionActiveResponseBody body) {
        super(body);
    }

    public IsActionActiveResponse() {
        this(new IsActionActiveResponseBody());
    }

    public IsActionActiveResponse(List<Boolean> active) {
        this(new IsActionActiveResponseBody(active));
    }

    @Override
    public IsActionActiveResponseBody createBody() {
        return new IsActionActiveResponseBody();
    }

    @ApiModel(value = "IsActionActiveResponseBody", description = "An action active response body")
    public static class IsActionActiveResponseBody implements ResponseBody {

        private List<Boolean> active = new ArrayList<>();

        public IsActionActiveResponseBody() {
        }

        public IsActionActiveResponseBody(List<Boolean> active) {
            this.active = active;
        }

        @ApiModelProperty(value = "The actions activity statuses", required = true)
        public List<Boolean> getActive() {
            return active;
        }

        public void setActive(List<Boolean> active) {
            this.active = active;
        }
    }
}
