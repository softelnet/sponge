/*
 * Copyright 2016-2020 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.server.test.remote.delegate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.remoteapi.model.response.BaseResponse;
import org.openksavi.sponge.remoteapi.model.response.BaseResponseResult;
import org.openksavi.sponge.remoteapi.server.test.remote.delegate.UpperCaseResponse.UpperCaseResult;

@ApiModel(value = "UpperCaseResponse", description = "An UpperCase response")
public class UpperCaseResponse extends BaseResponse<UpperCaseResult> {

    public UpperCaseResponse(UpperCaseResult result) {
        super(result);
    }

    public UpperCaseResponse() {
        this(null);
    }

    @ApiModel(value = "UpperCaseResult", description = "An UpperCase response result")
    public static class UpperCaseResult extends BaseResponseResult<String> {

        public UpperCaseResult() {
        }

        public UpperCaseResult(String result) {
            super(result);
        }

        @Override
        @ApiModelProperty(value = "The result", required = true)
        public String getValue() {
            return super.getValue();
        }
    }
}
