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

package org.openksavi.sponge.restapi.client.listener;

import org.openksavi.sponge.restapi.model.request.SpongeRequest;
import org.openksavi.sponge.restapi.model.response.SpongeResponse;

@FunctionalInterface
public interface OnResponseDeserializedListener {

    /**
     * A callback that will be invoked when the response is deserialized.
     *
     * @param request the request.
     * @param response the response. May be {@code null} if case of deserializing error.
     * @param responseString the deserialized response.
     */
    void onResponseDeserialized(SpongeRequest request, SpongeResponse response, String responseString);
}
