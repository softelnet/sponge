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

package org.openksavi.sponge.restapi.model;

import org.apache.commons.lang3.exception.ExceptionUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ActionCallResult", description = "Represents a Sponge action call result")
public class RestCallResult extends BaseRestResponse {

    private String actionName;

    private Object result;

    public RestCallResult() {
        //
    }

    public RestCallResult(String actionName, Object result) {
        this.actionName = actionName;
        this.result = result;
    }

    @ApiModelProperty(value = "The action name", required = true)
    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    @ApiModelProperty(value = "The action result", required = true)
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public static RestCallResult fromException(String actionName, Exception e) {
        RestCallResult result = new RestCallResult();
        result.setActionName(actionName);
        result.setErrorMessage(ExceptionUtils.getMessage(e));
        result.setDetailedErrorMessage(ExceptionUtils.getStackTrace(e));

        return result;
    }
}
