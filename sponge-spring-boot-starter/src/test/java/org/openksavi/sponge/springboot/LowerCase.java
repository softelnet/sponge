/*
 * Copyright 2016-2021 The Sponge authors.
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
package org.openksavi.sponge.springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.openksavi.sponge.java.JAction;
import org.openksavi.sponge.type.StringType;

@Component
public class LowerCase extends JAction {

    private final StringService stringService;

    @Autowired
    public LowerCase(StringService stringService) {
        this.stringService = stringService;
    }

    @Override
    public void onConfigure() {
        withLabel("Lower case").withArg(new StringType("text")).withResult(new StringType());
    }

    public String onCall(String text) {
        return stringService.toLowerCase(text);
    }
}
