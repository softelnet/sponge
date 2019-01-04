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

import org.openksavi.sponge.type.DataType;

@ApiModel(value = "ActionArgMeta", description = "An action argument metadata")
@SuppressWarnings("rawtypes")
public class RestActionArgMeta {

    private String name;

    private DataType type;

    private String displayName;

    private String description;

    private boolean optional;

    private boolean provided;

    private List<String> depends;

    private boolean readOnly;

    private boolean overwrite;

    public RestActionArgMeta() {
        //
    }

    public RestActionArgMeta(String name, DataType type, String displayName, String description, boolean optional, boolean provided,
            List<String> depends, boolean readOnly, boolean overwrite) {
        this.name = name;
        this.type = type;
        this.displayName = displayName;
        this.description = description;
        this.optional = optional;
        this.provided = provided;
        this.depends = depends;
        this.readOnly = readOnly;
        this.overwrite = overwrite;
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

    @ApiModelProperty(value = "The action argument display name", required = false)
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    @ApiModelProperty(value = "Provided argument", required = false)
    public boolean isProvided() {
        return provided;
    }

    public void setProvided(boolean provided) {
        this.provided = provided;
    }

    @ApiModelProperty(value = "Argument depends on others arguments", required = false)
    public List<String> getDepends() {
        return depends;
    }

    public void setDepends(List<String> depends) {
        this.depends = depends;
    }

    @ApiModelProperty(value = "Read only argument", required = false)
    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @ApiModelProperty(value = "The overwrite flag", required = false)
    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }
}
