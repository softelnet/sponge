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

import java.util.ArrayList;
import java.util.List;

import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.core.BaseProcessorMeta;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.util.SpongeApiUtils;

/**
 * A base action metadata.
 */
@SuppressWarnings("rawtypes")
public class BaseActionMeta extends BaseProcessorMeta implements ActionMeta {

    private List<DataType> args;

    private DataType result;

    @Override
    public List<DataType> getArgs() {
        return args;
    }

    @Override
    public void setArgs(List<DataType> args) {
        this.args = args != null ? new ArrayList<>(args) : null;
    }

    @Override
    public void addArgs(List<DataType> args) {
        if (this.args != null) {
            this.args.addAll(args);
        } else {
            setArgs(args);
        }
    }

    @Override
    public DataType getArg(String name) {
        return SpongeApiUtils.getActionArgType(args, name);
    }

    @Override
    public DataType getResult() {
        return result;
    }

    @Override
    public void setResult(DataType result) {
        this.result = result;
    }
}
