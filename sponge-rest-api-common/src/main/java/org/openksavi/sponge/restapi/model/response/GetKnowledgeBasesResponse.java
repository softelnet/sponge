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

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.restapi.model.RestKnowledgeBaseMeta;
import org.openksavi.sponge.restapi.model.response.GetKnowledgeBasesResponse.GetKnowledgeBasesResponseBody;

@ApiModel(value = "GetKnowledgeBasesResponse", description = "A get knowledge bases response")
public class GetKnowledgeBasesResponse extends BodySpongeResponse<GetKnowledgeBasesResponseBody> {

    public GetKnowledgeBasesResponse(GetKnowledgeBasesResponseBody body) {
        super(body);
    }

    public GetKnowledgeBasesResponse() {
        this(new GetKnowledgeBasesResponseBody());
    }

    public GetKnowledgeBasesResponse(List<RestKnowledgeBaseMeta> knowledgeBases) {
        this(new GetKnowledgeBasesResponseBody(knowledgeBases));
    }

    @Override
    public GetKnowledgeBasesResponseBody createBody() {
        return new GetKnowledgeBasesResponseBody();
    }

    @ApiModel(value = "GetKnowledgeBasesResponseBody", description = "A get knowledge bases response body")
    public static class GetKnowledgeBasesResponseBody implements ResponseBody {

        private List<RestKnowledgeBaseMeta> knowledgeBases;

        public GetKnowledgeBasesResponseBody() {
        }

        public GetKnowledgeBasesResponseBody(List<RestKnowledgeBaseMeta> knowledgeBases) {
            this.knowledgeBases = knowledgeBases;
        }

        @ApiModelProperty(value = "The available knowledge bases", required = true)
        public List<RestKnowledgeBaseMeta> getKnowledgeBases() {
            return knowledgeBases;
        }

        public void setKnowledgeBases(List<RestKnowledgeBaseMeta> knowledgeBases) {
            this.knowledgeBases = knowledgeBases;
        }
    }
}
