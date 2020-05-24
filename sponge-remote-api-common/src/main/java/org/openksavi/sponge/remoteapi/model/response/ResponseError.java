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

package org.openksavi.sponge.remoteapi.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A response error.
 */
@ApiModel(value = "Error", description = "An error object")
public class ResponseError {

    private Integer code;

    private String message;

    private ErrorData data;

    public ResponseError(Integer code, String message, ErrorData data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseError(Integer code, String message) {
        this(code, message, null);
    }

    public ResponseError() {
    }

    @ApiModelProperty(value = "The error code", required = true)
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @ApiModelProperty(value = "The error message", required = true)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @ApiModelProperty(value = "The error data", required = false)
    public ErrorData getData() {
        return data;
    }

    public void setData(ErrorData data) {
        this.data = data;
    }

    @ApiModel(value = "ErrorData", description = "An error data")
    public static class ErrorData {

        private String detailedErrorMessage;

        public ErrorData(String detailedErrorMessage) {
            this.detailedErrorMessage = detailedErrorMessage;
        }

        public ErrorData() {
        }

        @ApiModelProperty(value = "The detailed error message", required = false)
        public String getDetailedErrorMessage() {
            return detailedErrorMessage;
        }

        public void setDetailedErrorMessage(String detailedErrorMessage) {
            this.detailedErrorMessage = detailedErrorMessage;
        }
    }
}