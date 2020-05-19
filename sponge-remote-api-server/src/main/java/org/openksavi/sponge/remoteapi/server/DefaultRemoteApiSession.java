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

package org.openksavi.sponge.remoteapi.server;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openksavi.sponge.remoteapi.server.security.UserAuthentication;

/**
 * A default Remote API session.
 */
public class DefaultRemoteApiSession implements RemoteApiSession {

    private UserAuthentication userAuthentication;

    private Map<String, Object> features = Collections.synchronizedMap(new LinkedHashMap<>());

    private Map<String, Object> responseFeatures = Collections.synchronizedMap(new LinkedHashMap<>());

    public DefaultRemoteApiSession(UserAuthentication userAuthentication) {
        this.userAuthentication = userAuthentication;
    }

    @Override
    public UserAuthentication getUserAuthentication() {
        return userAuthentication;
    }

    public void setUserAuthentication(UserAuthentication userAuthentication) {
        this.userAuthentication = userAuthentication;
    }

    @Override
    public Map<String, Object> getFeatures() {
        return features;
    }

    @Override
    public Map<String, Object> getResponseFeatures() {
        return responseFeatures;
    }
}
