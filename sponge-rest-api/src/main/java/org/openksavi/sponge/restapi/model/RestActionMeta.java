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

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ActionMeta", description = "Represents a Sponge action metadata")
public class RestActionMeta {

    private String name;

    private String displayName;

    private RestKnowledgeBase knowledgeBase;

    private List<RestActionArgMeta> argsMeta;

    private RestActionResultMeta resultMeta;

    public RestActionMeta() {
        //
    }

    public RestActionMeta(String name, String displayName, RestKnowledgeBase knowledgeBase, List<RestActionArgMeta> argsMeta,
            RestActionResultMeta resultMeta) {
        this.name = name;
        this.displayName = displayName;
        this.knowledgeBase = knowledgeBase;
        this.argsMeta = argsMeta;
        this.resultMeta = resultMeta;
    }

    @ApiModelProperty(value = "The action name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(value = "The action display name", required = false)
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @ApiModelProperty(value = "The action knowledge base", required = true)
    public RestKnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public void setKnowledgeBase(RestKnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    @ApiModelProperty(value = "The action arguments metadata", required = false)
    public List<RestActionArgMeta> getArgsMeta() {
        return argsMeta;
    }

    public void setArgsMetadata(List<RestActionArgMeta> argsMeta) {
        this.argsMeta = argsMeta;
    }

    @ApiModelProperty(value = "The action result metadata", required = false)
    public RestActionResultMeta getResultMeta() {
        return resultMeta;
    }

    public void setResultMeta(RestActionResultMeta resultMeta) {
        this.resultMeta = resultMeta;
    }
}
