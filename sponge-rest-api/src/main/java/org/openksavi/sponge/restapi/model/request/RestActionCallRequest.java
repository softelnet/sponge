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

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ActionCallRequest", description = "Represents a Sponge action call")
public class RestActionCallRequest extends BaseRestRequest {

    private String name;

    private List<Object> args;

    public RestActionCallRequest() {
        //
    }

    public RestActionCallRequest(String username, String password, String name, List<Object> args) {
        this.name = name;
        this.args = args;
    }

    public RestActionCallRequest(String name, List<Object> args) {
        this(null, null, name, args);
    }

    @ApiModelProperty(value = "The action name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(value = "The action arguments", required = false)
    public List<Object> getArgs() {
        return args;
    }

    public void setArgs(List<Object> args) {
        this.args = args;
    }
}
