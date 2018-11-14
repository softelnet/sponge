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
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ActionMeta", description = "An action metadata")
public class RestActionMeta {

    private String name;

    private String displayName;

    private String description;

    private RestKnowledgeBaseMeta knowledgeBase;

    private Map<String, Object> features;

    private List<RestActionArgMeta> argsMeta;

    private RestActionResultMeta resultMeta;

    public RestActionMeta() {
        //
    }

    public RestActionMeta(String name, String displayName, String description, RestKnowledgeBaseMeta knowledgeBase,
            Map<String, Object> features, List<RestActionArgMeta> argsMeta, RestActionResultMeta resultMeta) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.knowledgeBase = knowledgeBase;
        this.features = features;
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

    @ApiModelProperty(value = "The action description", required = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ApiModelProperty(value = "The action knowledge base metadata", required = true)
    public RestKnowledgeBaseMeta getKnowledgeBase() {
        return knowledgeBase;
    }

    public void setKnowledgeBase(RestKnowledgeBaseMeta knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    @ApiModelProperty(value = "The action features", required = true)
    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }

    @ApiModelProperty(value = "The action arguments metadata", required = false)
    public List<RestActionArgMeta> getArgsMeta() {
        return argsMeta;
    }

    public void setArgsMeta(List<RestActionArgMeta> argsMeta) {
        this.argsMeta = argsMeta;
    }

    @ApiModelProperty(value = "The action result metadata", required = false)
    public RestActionResultMeta getResultMeta() {
        return resultMeta;
    }

    public void setResultMeta(RestActionResultMeta resultMeta) {
        this.resultMeta = resultMeta;
    }

    public String getLabel() {
        return String.format("%s: %s", knowledgeBase != null ? knowledgeBase.getLabel() : null, displayName != null ? displayName : name);

    }
}
