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

package org.openksavi.sponge.examples.project.usermanagement;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.openksavi.sponge.remoteapi.server.RemoteApiServerConstants;
import org.openksavi.sponge.remoteapi.test.base.RemoteApiTestEnvironment;

public class UserManagementMain {

    public static void main(String... args) throws UnknownHostException {
        System.setProperty(RemoteApiServerConstants.PROP_NAME, "USER MANAGEMENT");

        System.setProperty(RemoteApiServerConstants.PROP_SERVICE_DISCOVERY_URL,
                String.format("http://%s:%s", InetAddress.getLocalHost().getHostAddress(), RemoteApiTestEnvironment.DEFAULT_PORT));

        new RemoteApiTestEnvironment().run();
    }
}
