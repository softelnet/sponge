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

@ApiModel(value = "ActionCallResponse", description = "Action call response")
public class RestActionCallResponse extends BaseRestResponse {

    private Object result;

    public RestActionCallResponse() {
        //
    }

    public RestActionCallResponse(Object result) {
        this.result = result;
    }

    @ApiModelProperty(value = "The action result", required = true)
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
