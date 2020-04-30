/*
 * Copyright 2016-2020 The Sponge authors.
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

package org.openksavi.sponge.features.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.util.Descriptive;
import org.openksavi.sponge.util.HasFeatures;

/**
 * A sub-action specification.
 */
public class SubAction implements HasFeatures, Descriptive, Serializable, Cloneable {

    private static final long serialVersionUID = -9111976205752523512L;

    /** The sub-action name. */
    private String name;

    /** The sub-action label. */
    private String label;

    /** The sub-action description. */
    private String description;

    /** The sub-action argument substitutions. */
    private List<SubActionArg> args = new ArrayList<>();

    /** The sub-action result substitution. */
    private SubActionResult result;

    /** The sub-action features. */
    private Map<String, Object> features = new LinkedHashMap<>();

    public SubAction(String name) {
        this.name = name;
    }

    public SubAction() {
    }

    public SubAction withName(String name) {
        setName(name);
        return this;
    }

    public SubAction withLabel(String label) {
        setLabel(label);
        return this;
    }

    public SubAction withDescription(String description) {
        setDescription(description);
        return this;
    }

    public SubAction withArgs(List<SubActionArg> args) {
        args.addAll(args);
        return this;
    }

    public SubAction withArg(SubActionArg arg) {
        args.add(arg);
        return this;
    }

    public SubAction withArg(String target, String source) {
        return withArg(new SubActionArg(target, source));
    }

    public SubAction withResult(SubActionResult result) {
        setResult(result);
        return this;
    }

    public SubAction withResult(String target) {
        return withResult(new SubActionResult(target));
    }

    public SubAction withFeatures(Map<String, Object> features) {
        this.features.putAll(features);
        return this;
    }

    public SubAction withFeature(String name, Object value) {
        features.put(name, value);
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public List<SubActionArg> getArgs() {
        return args;
    }

    public void setArgs(List<SubActionArg> args) {
        this.args = args;
    }

    public SubActionResult getResult() {
        return result;
    }

    public void setResult(SubActionResult result) {
        this.result = result;
    }

    @Override
    public Map<String, Object> getFeatures() {
        return features;
    }

    @Override
    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }

    @Override
    public SubAction clone() {
        try {
            SubAction cloned = (SubAction) super.clone();
            cloned.args = args != null ? args.stream().map(arg -> arg.clone()).collect(Collectors.toList()) : null;
            cloned.result = result != null ? result.clone() : null;
            cloned.features = features != null ? new LinkedHashMap<>(features) : null;

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new SpongeException(e);
        }
    }
}
