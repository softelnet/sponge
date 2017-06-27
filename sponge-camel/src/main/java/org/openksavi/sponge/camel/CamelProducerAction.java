/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.camel;

import org.apache.camel.Exchange;

import org.openksavi.sponge.java.JavaAction;

/**
 * Default producer action.
 */
public class CamelProducerAction extends JavaAction {

    public static final String NAME = "CamelProducerAction";

    @Override
    public void configure() {
        setName(NAME);
    }

    @Override
    public Object run(Object... args) {
        CamelUtils.assertCamelProducerActionArgs(this, args);

        return getEps().event(CamelUtils.getOrCreateInputEvent(getEps().getEngine(), (Exchange) args[0])).send();
    }
}