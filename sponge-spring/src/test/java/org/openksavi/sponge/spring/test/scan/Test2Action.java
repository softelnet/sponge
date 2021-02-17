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

package org.openksavi.sponge.spring.test.scan;

import org.springframework.stereotype.Component;

import org.openksavi.sponge.java.JAction;

@Component
public class Test2Action extends JAction {

    private TestService service;

    public Test2Action(TestService service) {
        this.service = service;
    }

    public String onCall(String text) {
        return service.getName() + " " + text;
    }
}
