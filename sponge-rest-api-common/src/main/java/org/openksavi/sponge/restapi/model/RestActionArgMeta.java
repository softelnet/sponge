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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.action.ArgProvidedMeta;
import org.openksavi.sponge.type.DataType;

@ApiModel(value = "ActionArgMeta", description = "An action argument metadata")
@SuppressWarnings("rawtypes")
public class RestActionArgMeta {

    private String name;

    private DataType type;

    private String label;

    private String description;

    private boolean optional;

    private ArgProvidedMeta provided;

    public RestActionArgMeta() {
    }

    public RestActionArgMeta(String name, DataType type, String label, String description, boolean optional, ArgProvidedMeta provided) {
        this.name = name;
        this.type = type;
        this.label = label;
        this.description = description;
        this.optional = optional;
        this.provided = provided;
    }

    @ApiModelProperty(value = "The action argument name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(value = "The action argument type", required = true)
    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    @ApiModelProperty(value = "The action argument label", required = false)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @ApiModelProperty(value = "The action argument description", required = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ApiModelProperty(value = "Optional argument", required = false)
    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    @ApiModelProperty(value = "The provided argument metadata", required = false)
    public ArgProvidedMeta getProvided() {
        return provided;
    }

    public void setProvided(ArgProvidedMeta provided) {
        this.provided = provided;
    }
}
