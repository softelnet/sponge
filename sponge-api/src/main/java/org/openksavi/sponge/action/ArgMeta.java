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

package org.openksavi.sponge.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.type.AnyType;
import org.openksavi.sponge.type.DataType;

/**
 * An action argument metadata.
 */
@SuppressWarnings("rawtypes")
public class ArgMeta implements Cloneable {

    /** The argument name. */
    private String name;

    /** The argument data type. */
    private DataType type;

    /** The argument label. */
    private String label;

    /** The argument description. */
    private String description;

    /** The flag specifying if this argument is optional. Defaults to {@code false}. */
    private boolean optional = false;

    /** The provided argument specification. Defaults to {@code null}. */
    private ArgProvidedMeta provided;

    /** The features. */
    private Map<String, Object> features = new LinkedHashMap<>();

    /** The sub-arguments metadata. Defaults to an empty list. */
    private List<ArgMeta> subArgs = new ArrayList<>();

    public ArgMeta(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    public ArgMeta(String name) {
        this(name, new AnyType());
    }

    public ArgMeta withLabel(String label) {
        this.label = label;
        return this;
    }

    public ArgMeta withDescription(String description) {
        this.description = description;
        return this;
    }

    public ArgMeta withOptional() {
        optional = true;
        return this;
    }

    public ArgMeta withProvided(ArgProvidedMeta provided) {
        this.provided = provided;
        return this;
    }

    public ArgMeta withFeatures(Map<String, Object> features) {
        getFeatures().putAll(features);
        return this;
    }

    public ArgMeta withFeature(String name, Object value) {
        getFeatures().put(name, value);
        return this;
    }

    public ArgMeta withSubArgs(List<ArgMeta> subArgs) {
        this.subArgs.addAll(subArgs);
        return this;
    }

    public ArgMeta withSubArg(ArgMeta subArg) {
        return withSubArgs(Arrays.asList(subArg));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public ArgProvidedMeta getProvided() {
        return provided;
    }

    public void setProvided(ArgProvidedMeta provided) {
        this.provided = provided;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = new LinkedHashMap<>(features);
    }

    public List<ArgMeta> getSubArgs() {
        return subArgs;
    }

    public void setSubArgs(List<ArgMeta> subArgs) {
        this.subArgs = new ArrayList<>(subArgs);
    }

    @Override
    public ArgMeta clone() {
        try {
            return (ArgMeta) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new SpongeException(e);
        }
    }
}
