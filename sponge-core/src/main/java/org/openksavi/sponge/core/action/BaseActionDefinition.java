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

import org.openksavi.sponge.action.ActionDefinition;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.core.BaseProcessorDefinition;

public class BaseActionDefinition extends BaseProcessorDefinition implements ActionDefinition {

    private List<ArgMeta<?>> argsMeta;

    private ResultMeta<?> resultMeta;

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public List<ArgMeta<?>> getArgsMeta() {
        return argsMeta;
    }

    @Override
    public void setArgsMeta(List<ArgMeta<?>> argsMeta) {
        this.argsMeta = argsMeta;
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
