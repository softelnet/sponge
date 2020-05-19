/*
 * Copyright 2016-2020 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.server.discovery;

/**
 * A service discovery info.
 */
public class ServiceDiscoveryInfo {

    /** The service URL used in a service discovery. */
    private String url;

    public ServiceDiscoveryInfo() {
    }

    public ServiceDiscoveryInfo(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ServiceDiscoveryInfo withUrl(String url) {
        setUrl(url);
        return this;
    }

    @Override
    public String toString() {
        return "ServiceDiscoveryInfo [url=" + url + "]";
    }
}
