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

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.CategoryMeta;
import org.openksavi.sponge.ProcessorQualifiedVersion;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.util.SpongeApiUtils;

@ApiModel(value = "ActionMeta", description = "An action metadata")
@SuppressWarnings("rawtypes")
public class RestActionMeta {

    private String name;

    private String label;

    private String description;

    private RestKnowledgeBaseMeta knowledgeBase;

    private CategoryMeta category;

    private Map<String, Object> features;

    private List<DataType> args;

    private DataType result;

    private ProcessorQualifiedVersion qualifiedVersion;

    public RestActionMeta() {
    }

    public RestActionMeta(String name, String label, String description, RestKnowledgeBaseMeta knowledgeBase, CategoryMeta category,
            Map<String, Object> features, List<DataType> args, DataType result, ProcessorQualifiedVersion qualifiedVersion) {
        this.name = name;
        this.label = label;
        this.description = description;
        this.knowledgeBase = knowledgeBase;
        this.category = category;
        this.features = features;
        this.args = args;
        this.result = result;
        this.qualifiedVersion = qualifiedVersion;
    }

    @ApiModelProperty(value = "The action name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(value = "The action label", required = false)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    @ApiModelProperty(value = "The action category metadata", required = false)
    public CategoryMeta getCategory() {
        return category;
    }

    public void setCategory(CategoryMeta category) {
        this.category = category;
    }

    @ApiModelProperty(value = "The action features", required = true)
    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }

    @ApiModelProperty(value = "The action argument types", required = false)
    public List<DataType> getArgs() {
        return args;
    }

    public void setArgs(List<DataType> args) {
        this.args = args;
    }

    @ApiModelProperty(value = "The action result type", required = false)
    public DataType getResult() {
        return result;
    }

    public void setResult(DataType result) {
        this.result = result;
    }

    @ApiModelProperty(value = "The action qualified version", required = false)
    public ProcessorQualifiedVersion getQualifiedVersion() {
        return qualifiedVersion;
    }

    public void setQualifiedVersion(ProcessorQualifiedVersion qualifiedVersion) {
        this.qualifiedVersion = qualifiedVersion;
    }

    @JsonIgnore
    public DataType getArg(String argName) {
        return SpongeApiUtils.getActionArgType(args, argName);
    }
}
