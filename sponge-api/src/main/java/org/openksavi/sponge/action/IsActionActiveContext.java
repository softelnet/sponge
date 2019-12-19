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

package org.openksavi.sponge.action;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.type.DataType;

/**
 * A context for the {@code onIsActive} callback method.
 */
@SuppressWarnings("rawtypes")
public class IsActionActiveContext implements Cloneable {

    /** The context value. Can be {@code null}. */
    private Object value;

    /** The context value type. Can be {@code null}. */
    private DataType type;

    /** The action arguments in the context. Can be {@code null}. */
    private List<Object> args;

    /** The context features. It is guaranteed to be non null in the {@code onIsActive} action callback method. */
    private Map<String, Object> features;

    public IsActionActiveContext(Object value, DataType type, List<Object> args, Map<String, Object> features) {
        this.value = value;
        this.type = type;
        this.args = args;
        this.features = features;
    }

    public IsActionActiveContext(Object value, DataType type, List<Object> args) {
        this(value, type, args, new LinkedHashMap<>());
    }

    public IsActionActiveContext(List<Object> args) {
        this(null, null, args);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public List<Object> getArgs() {
        return args;
    }

    public void setArgs(List<Object> args) {
        this.args = args;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }

    public Object getFeature(String featureName) {
        Validate.isTrue(features != null && features.containsKey(featureName), "There is no feature %s", featureName);

        return features.get(featureName);
    }

    @SuppressWarnings("unchecked")
    public <T> T getFeature(String featureName, T defaultValue) {
        if (features == null || !features.containsKey(featureName)) {
            return defaultValue;
        }

        return (T) features.get(featureName);
    }

    @Override
    public IsActionActiveContext clone() {
        try {
            IsActionActiveContext cloned = (IsActionActiveContext) super.clone();
            cloned.args = args != null ? new ArrayList<>(args) : null;
            cloned.features = features != null ? new LinkedHashMap<>(features) : null;

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new SpongeException(e);
        }
    }
}
