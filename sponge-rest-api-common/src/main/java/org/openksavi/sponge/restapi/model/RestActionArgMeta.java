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

import org.openksavi.sponge.type.Type;

@ApiModel(value = "ActionArgMeta", description = "Represents a Sponge action argument metadata")
@SuppressWarnings("rawtypes")
public class RestActionArgMeta {

    private String name;

    private Type type;

    private boolean required;

    private String displayName;

    private String description;

    public RestActionArgMeta() {
        //
    }

    public RestActionArgMeta(String name, Type type, boolean required, String displayName, String description) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.displayName = displayName;
        this.description = description;
    }

    @ApiModelProperty(value = "The action argument name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(value = "The action argument type", required = true)
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @ApiModelProperty(value = "Is the argument required", required = true)
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @ApiModelProperty(value = "The action argument display name", required = true)
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @ApiModelProperty(value = "The action argument description", required = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
