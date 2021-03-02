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

package org.openksavi.sponge.java;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.core.action.BaseAction;
import org.openksavi.sponge.type.DataType;

/**
 * Java-specific implementation of the action.
 */
@SuppressWarnings("rawtypes")
public abstract class JAction extends BaseAction {

    @Override
    public final JAction withName(String name) {
        return (JAction) super.withName(name);
    }

    @Override
    public final JAction withLabel(String label) {
        return (JAction) super.withLabel(label);
    }

    @Override
    public final JAction withDescription(String description) {
        return (JAction) super.withDescription(description);
    }

    @Override
    public final JAction withVersion(Integer version) {
        return (JAction) super.withVersion(version);
    }

    @Override
    public final JAction withFeatures(Map<String, Object> features) {
        return (JAction) super.withFeatures(features);
    }

    @Override
    public final JAction withFeature(String name, Object value) {
        return (JAction) super.withFeature(name, value);
    }

    @Override
    public final JAction withCategory(String category) {
        return (JAction) super.withCategory(category);
    }

    @Override
    public final JAction withArgs(List<DataType> args) {
        return (JAction) super.withArgs(args);
    }

    public final JAction withArgs(DataType... args) {
        return withArgs(Arrays.asList(args));
    }

    @Override
    public final JAction withArg(DataType arg) {
        return (JAction) super.withArg(arg);
    }

    @Override
    public final JAction withNoArgs() {
        return (JAction) super.withNoArgs();
    }

    @Override
    public final JAction withResult(DataType result) {
        return (JAction) super.withResult(result);
    }

    @Override
    public final JAction withNoResult() {
        return (JAction) super.withNoResult();
    }

    @Override
    public final JAction withCallable(boolean callable) {
        return (JAction) super.withCallable(callable);
    }

    @Override
    public final JAction withNonCallable() {
        return (JAction) super.withNonCallable();
    }

    @Override
    public final JAction withActivatable() {
        return (JAction) super.withActivatable();
    }
}
