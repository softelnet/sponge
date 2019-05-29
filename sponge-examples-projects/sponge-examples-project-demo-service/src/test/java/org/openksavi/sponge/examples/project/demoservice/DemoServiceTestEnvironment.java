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

package org.openksavi.sponge.examples.project.demoservice;

import org.openksavi.sponge.restapi.test.base.RemoteApiTestEnvironment;

public class DemoServiceTestEnvironment extends RemoteApiTestEnvironment {

    public static final String PROPERTY_DIGITS_HOME = "digits.home";

    public static final String PROPERTY_PASSWORD_FILE = "password.file";

    @Override
    public void init() {
        super.init();

        System.setProperty(PROPERTY_DIGITS_HOME, "../../sponge-tensorflow/examples/tensorflow/digits");
        System.setProperty(PROPERTY_PASSWORD_FILE, "password.txt");
    }

    @Override
    public void clear() {
        super.clear();

        System.clearProperty(PROPERTY_DIGITS_HOME);
        System.clearProperty(PROPERTY_PASSWORD_FILE);
    }
}
