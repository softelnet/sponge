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

import org.openksavi.sponge.remoteapi.model.request.BaseRequest;
import org.openksavi.sponge.remoteapi.model.request.BaseRequestParams;
import org.openksavi.sponge.remoteapi.server.test.remote.ActionDelegateRemoteApiOperationTest;
import org.openksavi.sponge.remoteapi.server.test.remote.delegate.UpperCaseRequest.UpperCaseParams;

@ApiModel(value = "UpperCaseRequest", description = "An UpperCase request")
public class UpperCaseRequest extends BaseRequest<UpperCaseParams> {

    public UpperCaseRequest(UpperCaseParams params) {
        super(ActionDelegateRemoteApiOperationTest.OPERATION, params);
    }

    public UpperCaseRequest() {
        this(new UpperCaseParams());
    }

    @Override
    public UpperCaseParams createParams() {
        return new UpperCaseParams();
    }

    @ApiModel(value = "UpperCaseParams", description = "An UpperCase request params")
    public static class UpperCaseParams extends BaseRequestParams {

        private String text;

        public UpperCaseParams(String text) {
            this.text = text;
        }

        public UpperCaseParams() {
        }

        @ApiModelProperty(value = "Text to upper case", required = true)
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
