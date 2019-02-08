/*
 * Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.core.action;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.action.ArgProvidedValue;
import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.core.BaseProcessor;

public abstract class BaseAction extends BaseProcessor<ActionAdapter> implements Action {

    @Override
    public void onConfigure() {
        //
    }

    @Override
    public final ActionAdapter createAdapter() {
        return new BaseActionAdapter(new BaseActionDefinition());
    }

    @Override
    public List<ArgMeta<?>> getArgsMeta() {
        return getAdapter().getArgsMeta();
    }

    @Override
    public void setArgsMeta(List<ArgMeta<?>> argsMeta) {
        getAdapter().setArgsMeta(argsMeta);
    }

    @Override
    public ResultMeta<?> getResultMeta() {
        return getAdapter().getResultMeta();
    }

    @Override
    public void setResultMeta(ResultMeta<?> resultMeta) {
        getAdapter().setResultMeta(resultMeta);
    }

    @Override
    public void onProvideArgs(Set<String> names, Map<String, Object> current, Map<String, ArgProvidedValue<?>> provided) {
        // The default implementation doesn't modify the provided map.
    }

    @Override
    public final BaseAction withName(String name) {
        return (BaseAction) super.withName(name);
    }

    @Override
    public final BaseAction withLabel(String label) {
        return (BaseAction) super.withLabel(label);
    }

    @Override
    public final BaseAction withDescription(String description) {
        return (BaseAction) super.withDescription(description);
    }

    @Override
    public final BaseAction withVersion(Integer version) {
        return (BaseAction) super.withVersion(version);
    }

    @Override
    public final BaseAction withFeatures(Map<String, Object> features) {
        return (BaseAction) super.withFeatures(features);
    }

    @Override
    public final BaseAction withFeature(String name, Object value) {
        return (BaseAction) super.withFeature(name, value);
    }

    @Override
    public final BaseAction withCategory(String category) {
        return (BaseAction) super.withCategory(category);
    }

    public final BaseAction withArgsMeta(List<ArgMeta<?>> argsMeta) {
        setArgsMeta(argsMeta);
        return this;
    }

    public final BaseAction withResultMeta(ResultMeta<?> resultMeta) {
        setResultMeta(resultMeta);
        return this;
    }
}
