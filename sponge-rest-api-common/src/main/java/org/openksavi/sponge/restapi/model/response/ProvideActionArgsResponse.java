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

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.type.provided.ProvidedValue;

@ApiModel(value = "ProvideActionArgsResponse", description = "A provide action arguments response")
public class ProvideActionArgsResponse extends SpongeResponse {

    private Map<String, ProvidedValue<?>> provided;

    public ProvideActionArgsResponse() {
    }

    public ProvideActionArgsResponse(Map<String, ProvidedValue<?>> provided) {
        this.provided = provided;
    }

    @ApiModelProperty(value = "The provided action arguments", required = true)
    public Map<String, ProvidedValue<?>> getProvided() {
        return provided;
    }

    public void setProvided(Map<String, ProvidedValue<?>> provided) {
        this.provided = provided;
    }
}
