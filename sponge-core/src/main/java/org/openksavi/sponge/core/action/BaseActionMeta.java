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

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.core.BaseProcessorMeta;
import org.openksavi.sponge.type.AnnotatedType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.util.ValueHolder;

/**
 * A base action metadata.
 */
public class BaseActionMeta extends BaseProcessorMeta implements ActionMeta {

    private List<ArgMeta> argsMeta;

    private ResultMeta<?> resultMeta;

    @Override
    public List<ArgMeta> getArgsMeta() {
        return argsMeta;
    }

    @Override
    public void setArgsMeta(List<ArgMeta> argsMeta) {
        this.argsMeta = argsMeta != null ? new ArrayList<>(argsMeta) : null;
    }

    @Override
    public void addArgsMeta(List<ArgMeta> argsMeta) {
        if (this.argsMeta != null) {
            this.argsMeta.addAll(argsMeta);
        } else {
            setArgsMeta(argsMeta);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ArgMeta getArgMeta(String name) {
        Validate.notNull(argsMeta, "Arguments metadata not defined");

        List<String> elements = ActionUtils.getArgNameElements(name);

        ValueHolder<ArgMeta> argMetaHolder = new ValueHolder<>(argsMeta.get(getArgIndex(elements.get(0))));
        elements.stream().skip(1).forEach(element -> {
            ArgMeta argMeta = argMetaHolder.getValue();
            // Verify Record/Map type.
            Validate.isTrue(
                    argMeta.getType() instanceof RecordType || argMeta.getType() instanceof AnnotatedType
                            && ((AnnotatedType) argMeta.getType()).getValueType() instanceof RecordType,
                    "The argument %s doesn't containt a record", name);
            argMetaHolder.setValue(
                    argMeta.getSubArgs().stream().filter(subArgMeta -> subArgMeta.getName().equals(element)).findFirst().orElse(null));
            Validate.notNull(argMeta, "Metadata for argument '%s' not found", name);
        });

        return argMetaHolder.getValue();
    }

    public int getArgIndex(String argName) {
        for (int i = 0; i < argsMeta.size(); i++) {
            if (argsMeta.get(i).getName().equals(argName)) {
                return i;
            }
        }

        return -1;
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
