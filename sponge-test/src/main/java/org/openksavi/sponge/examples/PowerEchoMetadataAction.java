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

import org.openksavi.sponge.action.ArgMeta;
import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.type.AnyType;
import org.openksavi.sponge.type.NumberType;
import org.openksavi.sponge.type.StringType;

public class PowerEchoMetadataAction extends JAction {

    @Override
    public void onConfigure() {
        withFeatures(SpongeUtils.immutableMapOf("visibility", true));
        withArgs(new ArgMeta<>("value", new NumberType()).withLabel("Argument 1"),
                new ArgMeta<>("text", new StringType()).withLabel("Argument 2"));
        withResult(new ResultMeta<>(new AnyType()));
    }

    public Object onCall(Number value, String text) {
        return new Object[] { value.intValue() + 1, text.toUpperCase() };
    }
}
