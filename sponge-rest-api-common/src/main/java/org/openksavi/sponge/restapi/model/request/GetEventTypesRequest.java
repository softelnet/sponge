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

package org.openksavi.sponge.restapi.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "GetEventTypesRequest", description = "A get event types request")
public class GetEventTypesRequest extends SpongeRequest {

    private String name;

    public GetEventTypesRequest() {
    }

    public GetEventTypesRequest(String name) {
        this.name = name;
    }

    @ApiModelProperty(
            value = "The event name or the regular expression (compatible with https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html)",
            required = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
