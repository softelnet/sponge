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

package org.openksavi.sponge.remoteapi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.util.Descriptive;

@ApiModel(value = "KnowledgeBaseMeta", description = "A knowledge base metadata")
public class RemoteKnowledgeBaseMeta implements Descriptive {

    private String name;

    private String label;

    private String description;

    private Integer version;

    private Integer sequenceNumber;

    public RemoteKnowledgeBaseMeta() {
    }

    public RemoteKnowledgeBaseMeta(String name, String label, String description, Integer version, Integer sequenceNumber) {
        this.name = name;
        this.label = label;
        this.description = description;
        this.version = version;
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    @ApiModelProperty(value = "The knowledge base name", required = true)
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    @ApiModelProperty(value = "The knowledge base label", required = false)
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    @ApiModelProperty(value = "The knowledge base description", required = false)
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @ApiModelProperty(value = "The knowledge base version", required = false)
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @ApiModelProperty(value = "The knowledge base sequence number", required = false)
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
