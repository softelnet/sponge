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

package org.openksavi.sponge.restapi.model;

import java.util.LinkedHashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.util.Descriptive;
import org.openksavi.sponge.util.HasFeatures;

@ApiModel(value = "CategoryMeta", description = "A category metadata")
public class RestCategoryMeta implements Descriptive, HasFeatures {

    /** The category name. */
    private String name;

    /** The category label. */
    private String label;

    /** The category description. */
    private String description;

    /** The category features. */
    private Map<String, Object> features = new LinkedHashMap<>();

    private Integer sequenceNumber;

    public RestCategoryMeta() {
    }

    public RestCategoryMeta(String name, String label, String description, Map<String, Object> features, Integer sequenceNumber) {
        this.name = name;
        this.label = label;
        this.description = description;
        this.features = features;
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    @ApiModelProperty(value = "The category name", required = true)
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @ApiModelProperty(value = "The category label", required = false)
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    @ApiModelProperty(value = "The category description", required = false)
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    @ApiModelProperty(value = "The category features", required = true)
    public Map<String, Object> getFeatures() {
        return features;
    }

    @Override
    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }

    @ApiModelProperty(value = "The category sequence number", required = false)
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
