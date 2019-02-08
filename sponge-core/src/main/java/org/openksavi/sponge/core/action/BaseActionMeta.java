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

package org.openksavi.sponge.core.action;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.core.BaseProcessorMeta;
import org.openksavi.sponge.core.util.SpongeUtils;

/**
 * A base action metadata.
 */
public class BaseActionMeta extends BaseProcessorMeta implements ActionMeta {

    private List<ArgMeta<?>> argsMeta;

    private ResultMeta<?> resultMeta;

    @Override
    public List<ArgMeta<?>> getArgsMeta() {
        return argsMeta;
    }

    @Override
    public void setArgsMeta(List<ArgMeta<?>> argsMeta) {
        this.argsMeta = SpongeUtils.createUnmodifiableList(argsMeta);
    }

    @Override
    public ArgMeta<?> getArgMeta(String name) {
        Validate.notNull(argsMeta, "Arguments metadata not defined");
        Optional<ArgMeta<?>> argMetaO = argsMeta.stream().filter(argMeta -> Objects.equals(argMeta.getName(), name)).findFirst();
        Validate.isTrue(argMetaO.isPresent(), "Metadata for argument '%s' not found", name);

        return argMetaO.get();
    }

    @Override
    public ResultMeta<?> getResultMeta() {
        return resultMeta;
    }

    @Override
    public void setResultMeta(ResultMeta<?> resultMeta) {
        this.resultMeta = resultMeta;
    }
}
