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

package org.openksavi.sponge.type.provided;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openksavi.sponge.Experimental;

/**
 * A provided object metadata.
 */
public class ProvidedMeta {

    /** The flag specifying if the value is provided. Defaults to {@code false}. */
    private boolean value = false;

    /** The metadata specifying if the value set is provided. Defaults to {@code null}. */
    private ValueSetMeta valueSet;

    /** The list of names that this provided object depends on. */
    private List<String> dependencies = new ArrayList<>();

    /**
     * The flag specifying if the provided value of this object should overwrite the value set in a client code. Defaults to {@code false}.
     * This flag should be handled by a client code.
     */
    private boolean overwrite = false;

    /** The flag specifying if the list element value set is provided. Applicable only for list types. Defaults to {@code false}. */
    private boolean elementValueSet = false;

    /** The metadata specifying if the value can be submitted (i.e. written). */
    private SubmittableMeta submittable;

    /** The flag specifying if the provided value should be updated lazily in a client code when a dependency changes (experimental). */
    @Experimental
    private boolean lazyUpdate = false;

    /** The flag specifying if the current value in a client code should be passed to a server when its new value is to be provided. */
    private boolean current = false;

    /**
     * The provided read mode: {@code explicit} (a value has to specified to be provided in {@code provideArgs}), {@code optional} (a value
     * may or may not be specified to be provided in {@code provideArgs}) or {@code implicit} (a value shouldn't be specified to be provided
     * in {@code provideArgs}). Defaults to {@code explicit}. For example a value can be provided optionally or implicitly when another
     * value is submitted.
     */
    private ProvidedMode mode = ProvidedMode.EXPLICIT;

    public ProvidedMeta() {
    }

    public ProvidedMeta withValue() {
        value = true;
        return this;
    }

    public ProvidedMeta withValueSet() {
        return withValueSet(new ValueSetMeta());
    }

    public ProvidedMeta withValueSet(ValueSetMeta valueSet) {
        this.valueSet = valueSet;
        return this;
    }

    public ProvidedMeta withDependencies(List<String> dependencies) {
        this.dependencies.addAll(dependencies);
        return this;
    }

    public ProvidedMeta withDependency(String dependency) {
        return withDependencies(Arrays.asList(dependency));
    }

    public ProvidedMeta withOverwrite() {
        overwrite = true;
        return this;
    }

    public ProvidedMeta withElementValueSet() {
        elementValueSet = true;
        return this;
    }

    public ProvidedMeta withSubmittable(SubmittableMeta submittable) {
        this.submittable = submittable;
        return this;
    }

    public ProvidedMeta withSubmittable() {
        return withSubmittable(new SubmittableMeta());
    }

    public ProvidedMeta withLazyUpdate() {
        lazyUpdate = true;
        return this;
    }

    public ProvidedMeta withCurrent() {
        current = true;
        return this;
    }

    public ProvidedMeta withMode(ProvidedMode mode) {
        this.mode = mode;
        return this;
    }

    public ProvidedMeta withExplicitMode() {
        return withMode(ProvidedMode.EXPLICIT);
    }

    public ProvidedMeta withOptionalMode() {
        return withMode(ProvidedMode.OPTIONAL);
    }

    public ProvidedMeta withImplicitMode() {
        return withMode(ProvidedMode.IMPLICIT);
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public ValueSetMeta getValueSet() {
        return valueSet;
    }

    public void setValueSet(ValueSetMeta valueSet) {
        this.valueSet = valueSet;
    }

    public boolean hasValueSet() {
        return valueSet != null;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = new ArrayList<>(dependencies);
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    public boolean isElementValueSet() {
        return elementValueSet;
    }

    public void setElementValueSet(boolean elementValueSet) {
        this.elementValueSet = elementValueSet;
    }

    public SubmittableMeta getSubmittable() {
        return submittable;
    }

    public void setSubmittable(SubmittableMeta submittable) {
        this.submittable = submittable;
    }

    public boolean isLazyUpdate() {
        return lazyUpdate;
    }

    public void setLazyUpdate(boolean lazyUpdate) {
        this.lazyUpdate = lazyUpdate;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public ProvidedMode getMode() {
        return mode;
    }

    public void setMode(ProvidedMode mode) {
        this.mode = mode;
    }
}
