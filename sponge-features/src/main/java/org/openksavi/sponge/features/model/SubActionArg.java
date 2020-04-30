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
import java.util.LinkedHashMap;
import java.util.Map;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.util.HasFeatures;

/**
 * A sub-action argument substitution.
 */
public class SubActionArg implements HasFeatures, Serializable, Cloneable {

    private static final long serialVersionUID = -7230022622488900038L;

    /** The target attribute name (i.e. the argument name of the sub-action). */
    private String target;

    /** The source attribute (i.e. the argument name of the parent action). */
    private String source;

    /** The argument substitution features. */
    private Map<String, Object> features = new LinkedHashMap<>();

    public SubActionArg(String target, String source) {
        this.target = target;
        this.source = source;
    }

    public SubActionArg() {
    }

    public SubActionArg withTarget(String target) {
        setTarget(target);
        return this;
    }

    public SubActionArg withSource(String source) {
        setSource(source);
        return this;
    }

    public SubActionArg withFeatures(Map<String, Object> features) {
        this.features.putAll(features);
        return this;
    }

    public SubActionArg withFeature(String name, Object value) {
        features.put(name, value);
        return this;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
    public SubActionArg clone() {
        try {
            SubActionArg cloned = (SubActionArg) super.clone();
            cloned.features = features != null ? new LinkedHashMap<>(features) : null;

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new SpongeException(e);
        }
    }
}
