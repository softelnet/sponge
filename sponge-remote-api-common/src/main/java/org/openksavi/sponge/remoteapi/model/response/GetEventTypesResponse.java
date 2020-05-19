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

package org.openksavi.sponge.remoteapi.model.response;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.remoteapi.model.response.GetEventTypesResponse.GetEventTypesResponseBody;
import org.openksavi.sponge.type.RecordType;

@ApiModel(value = "GetEventTypesResponse", description = "A get event types response")
public class GetEventTypesResponse extends BodySpongeResponse<GetEventTypesResponseBody> {

    public GetEventTypesResponse(GetEventTypesResponseBody body) {
        super(body);
    }

    public GetEventTypesResponse() {
        this(new GetEventTypesResponseBody());
    }

    public GetEventTypesResponse(Map<String, RecordType> eventTypes) {
        this(new GetEventTypesResponseBody(eventTypes));
    }

    @Override
    public GetEventTypesResponseBody createBody() {
        return new GetEventTypesResponseBody();
    }

    @ApiModel(value = "GetEventTypesResponseBody", description = "A get event types response body")
    public static class GetEventTypesResponseBody implements ResponseBody {

        private Map<String, RecordType> eventTypes;

        public GetEventTypesResponseBody() {
        }

        public GetEventTypesResponseBody(Map<String, RecordType> eventTypes) {
            this.eventTypes = eventTypes;
        }

        @ApiModelProperty(value = "The available event types", required = true)
        public Map<String, RecordType> getEventTypes() {
            return eventTypes;
        }

        public void setEventTypes(Map<String, RecordType> eventTypes) {
            this.eventTypes = eventTypes;
        }
    }
}
