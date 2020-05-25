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

package org.openksavi.sponge.remoteapi.model.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.model.request.SendEventRequest.SendEventParams;

@ApiModel(value = "SendEventRequest", description = "A send event request")
public class SendEventRequest extends TypedParamsRequest<SendEventParams> {

    public SendEventRequest(SendEventParams params) {
        super(RemoteApiConstants.METHOD_SEND, params);
    }

    public SendEventRequest() {
        this(new SendEventParams());
    }

    @Override
    public SendEventParams createParams() {
        return new SendEventParams();
    }

    @ApiModel(value = "SendEventParams", description = "A send event request params")
    public static class SendEventParams extends BaseRequestParams {

        private String name;

        @JsonInclude(Include.NON_NULL)
        private Map<String, Object> attributes;

        @JsonInclude(Include.NON_NULL)
        private String label;

        @JsonInclude(Include.NON_NULL)
        private String description;

        @JsonInclude(Include.NON_NULL)
        private Map<String, Object> features;

        public SendEventParams(String name, Map<String, Object> attributes, String label, String description,
                Map<String, Object> features) {
            this.name = name;
            this.attributes = attributes;
            this.label = label;
            this.description = description;
            this.features = features;
        }

        public SendEventParams() {
        }

        @ApiModelProperty(value = "The event name", required = true)
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @ApiModelProperty(value = "The event attributes", required = false)
        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        @ApiModelProperty(value = "The event label", required = false)
        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @ApiModelProperty(value = "The event description", required = false)
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @ApiModelProperty(value = "The event features", required = false)
        public Map<String, Object> getFeatures() {
            return features;
        }

        public void setFeatures(Map<String, Object> features) {
            this.features = features;
        }
    }
}
