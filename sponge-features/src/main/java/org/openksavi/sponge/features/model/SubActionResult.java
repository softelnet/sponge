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
 * A sub-action result substitution.
 */
public class SubActionResult implements HasFeatures, Serializable, Cloneable {

    private static final long serialVersionUID = -3855487686314690347L;

    /** The target attribute for the result (i.e. the argument name of the parent action). */
    private String target;

    /** The result substitution features. */
    private Map<String, Object> features = new LinkedHashMap<>();

    public SubActionResult(String target) {
        this.target = target;
    }

    public SubActionResult() {
    }

    public SubActionResult withTarget(String target) {
        setTarget(target);
        return this;
    }

    public SubActionResult withFeatures(Map<String, Object> features) {
        this.features.putAll(features);
        return this;
    }

    public SubActionResult withFeature(String name, Object value) {
        features.put(name, value);
        return this;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
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
    public SubActionResult clone() {
        try {
            SubActionResult cloned = (SubActionResult) super.clone();
            cloned.features = features != null ? new LinkedHashMap<>(features) : null;

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new SpongeException(e);
        }
    }
}
