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

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.type.DataType;

/**
 * A provide args method parameters.
 */
@SuppressWarnings("rawtypes")
public class ProvideArgsParameters {

    /** The list of argument names to provide. */
    private List<String> provide;

    /** The list of submitted argument names. */
    private List<String> submit;

    /**
     * The map of argument names and their current values passed from a client code. The map is required to contain values of those
     * arguments that the arguments specified in the {@code provide} and {@code submit} depend on and all arguments specified by
     * {@code submit}.
     */
    private Map<String, Object> current;

    /** The types of dynamic values used in {@code current} and {@code provide}. */
    private Map<String, DataType> dynamicTypes;

    /** The features for arguments. */
    private Map<String, Map<String, Object>> features;

    private Boolean initial = false;

    public ProvideArgsParameters(List<String> provide, List<String> submit, Map<String, Object> current, Map<String, DataType> dynamicTypes,
            Map<String, Map<String, Object>> features, Boolean initial) {
        this.provide = provide;
        this.submit = submit;
        this.current = current;
        this.dynamicTypes = dynamicTypes;
        this.features = features;

        if (initial != null) {
            this.initial = initial;
        }
    }

    public ProvideArgsParameters(List<String> provide, List<String> submit, Map<String, Object> current,
            Map<String, DataType> dynamicTypes) {
        this(provide, submit, current, dynamicTypes, null, null);
    }

    public ProvideArgsParameters(List<String> provide, List<String> submit, Map<String, Object> current) {
        this(provide, submit, current, null, null, null);
    }

    public ProvideArgsParameters(List<String> provide) {
        this(provide, null, null, null, null, null);
    }

    public ProvideArgsParameters() {
    }

    public List<String> getProvide() {
        return provide;
    }

    public void setProvide(List<String> provide) {
        this.provide = provide;
    }

    public List<String> getSubmit() {
        return submit;
    }

    public void setSubmit(List<String> submit) {
        this.submit = submit;
    }

    public Map<String, Object> getCurrent() {
        return current;
    }

    public void setCurrent(Map<String, Object> current) {
        this.current = current;
    }

    public Map<String, DataType> getDynamicTypes() {
        return dynamicTypes;
    }

    public void setDynamicTypes(Map<String, DataType> dynamicTypes) {
        this.dynamicTypes = dynamicTypes;
    }

    public Map<String, Map<String, Object>> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Map<String, Object>> features) {
        this.features = features;
    }

    public ProvideArgsParameters withProvide(List<String> provide) {
        setProvide(provide);
        return this;
    }

    public ProvideArgsParameters withSubmit(List<String> submit) {
        setSubmit(submit);
        return this;
    }

    public ProvideArgsParameters withCurrent(Map<String, Object> current) {
        setCurrent(current);
        return this;
    }

    public ProvideArgsParameters withDynamicTypes(Map<String, DataType> dynamicTypes) {
        setDynamicTypes(dynamicTypes);
        return this;
    }

    public ProvideArgsParameters withFeatures(Map<String, Map<String, Object>> features) {
        setFeatures(features);
        return this;
    }

    public Boolean isInitial() {
        return initial;
    }

    public void setInitial(Boolean initial) {
        this.initial = initial;
    }
}
