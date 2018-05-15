/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.examples;

import org.openksavi.sponge.Type;
import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.java.JAction;

public class PowerEchoMetadataAction extends JAction {

    @Override
    public void onConfigure() {
        setArgsMeta(new ArgMeta("arg1", Type.NUMBER, true, "Argument 1"), new ArgMeta("arg2", Type.STRING, true, "Argument 2"));
    }

    @Override
    public Object onCall(Object[] args) {
        getLogger().debug("{} called", getName());

        args[0] = Integer.valueOf(((Number) args[0]).intValue() + 1);
        args[1] = ((String) args[1]).toUpperCase();

        return args;
    }
}
