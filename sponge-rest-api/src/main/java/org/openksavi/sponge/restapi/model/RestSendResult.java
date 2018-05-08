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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.apache.commons.lang3.exception.ExceptionUtils;

@ApiModel(value = "SendResult", description = "Represents a Sponge event id")
public class RestSendResult extends BaseRestResponse {

    private String eventId;

    public RestSendResult() {
        //
    }

    public RestSendResult(String eventId) {
        this.eventId = eventId;
    }

    @ApiModelProperty(value = "The event id", required = true)
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public static RestSendResult fromException(Exception e) {
        RestSendResult result = new RestSendResult();
        result.setErrorMessage(ExceptionUtils.getMessage(e));
        result.setDetailedErrorMessage(ExceptionUtils.getStackTrace(e));

        return result;
    }
}
