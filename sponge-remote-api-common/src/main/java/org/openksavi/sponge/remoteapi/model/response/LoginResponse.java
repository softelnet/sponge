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

package org.openksavi.sponge.remoteapi.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.remoteapi.model.response.LoginResponse.LoginResponseBody;

@ApiModel(value = "LoginResponse", description = "A login response")
public class LoginResponse extends BodySpongeResponse<LoginResponseBody> {

    public LoginResponse(LoginResponseBody body) {
        super(body);
    }

    public LoginResponse() {
        this(new LoginResponseBody());
    }

    public LoginResponse(String authToken) {
        this(new LoginResponseBody(authToken));
    }

    @Override
    public LoginResponseBody createBody() {
        return new LoginResponseBody();
    }

    @ApiModel(value = "LoginResponseBody", description = "A login response body")
    public static class LoginResponseBody implements ResponseBody {

        private String authToken;

        public LoginResponseBody() {
        }

        public LoginResponseBody(String authToken) {
            this.authToken = authToken;
        }

        @ApiModelProperty(value = "The authentication token", required = true)
        public String getAuthToken() {
            return authToken;
        }

        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }
    }
}
